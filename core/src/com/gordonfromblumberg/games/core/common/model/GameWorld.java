package com.gordonfromblumberg.games.core.common.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.event.Event;
import com.gordonfromblumberg.games.core.common.event.EventProcessor;
import com.gordonfromblumberg.games.core.common.screens.AbstractScreen;
import com.gordonfromblumberg.games.core.common.utils.BSPTree;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

import java.util.Iterator;

public class GameWorld implements Disposable {

    private final Viewport viewport;
    private final Array<GameObject> gameObjects = new Array<>();
    public Creature creature;

    private final BSPTree tree;
    private final EventProcessor eventProcessor = new EventProcessor();

    public float width, height;

    private NinePatch background;

    private int maxCount = 0;

    private float time = 0;
    private int score = 0;

    public GameWorld(Viewport viewport, float worldWidth, float worldHeight) {
        this.viewport = viewport;
        width = worldWidth;
        height = worldHeight;
        tree = new BSPTree(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        background = new NinePatch(Main.getInstance()
                .assets()
                .get("image/texture_pack.atlas", TextureAtlas.class)
                .findRegion("world-background"),
                1, 1, 1, 1
        );

        creature = Creature.getInstance();
        creature.setRegion("herbivorous");
        creature.setPosition(32, 32);
        creature.setSize(64, 64);

        addGameObject(creature);
    }

    public void init() {
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.setGameWorld(this);
        gameObject.active = true;
        gameObject.id = GameObject.nextId++;
        if (gameObjects.size > maxCount) maxCount = gameObjects.size;
    }

    public void removeGameObject(GameObject gameObject) {
        gameObjects.removeValue(gameObject, true);
        gameObject.setGameWorld(null);
        gameObject.active = false;
    }

    public void update(float delta) {
        time += delta;
        tree.resetAndMove(0, 0);

        for (GameObject gameObject : gameObjects) {
            gameObject.update(delta);
            if (gameObject.active && gameObject.colliding) {
                tree.addObject(gameObject);
            }
        }

        detectCollisions();

        eventProcessor.process();

        if (time > 2) {
            time = 0;
            Gdx.app.log("GameWorld", gameObjects.size + " objects in the world of maximum " + maxCount);
        }
    }

    public void render(Batch batch) {
        background.draw(batch, 0, 0, width, height);

        for (GameObject gameObject : gameObjects) {
            gameObject.render(batch);
        }
    }

    public void convertWorldToScreen(Vector3 coords) {
        viewport.project(coords);
    }

    public void convertScreenToWorld(Vector3 coords) {
        viewport.unproject(coords);
    }

//    public float getMinVisibleX() {
//        return visibleArea.x;
//    }
//
//    public float getMaxVisibleX() {
//        return visibleArea.x + visibleArea.width;
//    }
//
//    public float getMinVisibleY() {
//        return visibleArea.y;
//    }
//
//    public float getMaxVisibleY() {
//        return visibleArea.y + visibleArea.height;
//    }

    public void gameOver() {
        AbstractScreen screen = Main.getInstance().getCurrentScreen();
        Main.getInstance().goToMainMenu();
        screen.dispose();
    }

    public int getScore() {
        return score;
    }

    public void pushEvent(Event event) {
        eventProcessor.push(event);
    }

    private void detectCollisions() {
        while (tree.hasNext()) {
            final Iterator<GameObject> iterator = tree.next();
            final Iterator<GameObject> internalIterator = tree.internalIterator();
            while (iterator.hasNext()) {
                final GameObject gameObject = iterator.next();
                if (!gameObject.active)
                    continue;

                while (internalIterator.hasNext()) {
                    final GameObject internalGameObject = internalIterator.next();
                    if (!internalGameObject.active)
                        continue;

                    if (detectCollision(gameObject, internalGameObject)) {
                        gameObject.collide(internalGameObject);
                    }
                }
            }
        }
    }

    private boolean detectCollision(GameObject obj1, GameObject obj2) {
        return obj1.getBoundingRectangle().overlaps(obj2.getBoundingRectangle())
                && Intersector.intersectPolygons(obj1.getPolygon(), obj2.getPolygon(), null);
    }

    @Override
    public void dispose() {
        for (GameObject gameObject : gameObjects) {
            gameObject.dispose();
        }
    }
}
