package com.gordonfromblumberg.games.core.common.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.event.Event;
import com.gordonfromblumberg.games.core.common.event.EventProcessor;
import com.gordonfromblumberg.games.core.common.screens.AbstractScreen;
import com.gordonfromblumberg.games.core.evo.WorldParams;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.food.Food;

import static com.gordonfromblumberg.games.core.common.utils.RandomUtils.*;

public class GameWorld implements Disposable {

    private final WorldParams params = new WorldParams();
    private final Array<GameObject> gameObjects = new Array<>();
    private final Array<Food> foods = new Array<>();
    public Creature creature;

    private final EventProcessor eventProcessor = new EventProcessor();

    int generation = 1;
    public float width, height;

    private NinePatch background;

    private int maxCount = 0;

    private float time = 0;
    private int score = 0;

    public GameWorld() {
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

    public void generateWorld() {
        final float fromX = 64;
        final float toX = width - 64;
        final float fromY = 64;
        final float toY = height - 64;

        final int foodCount = nextInt(params.getFoodCountFrom(), params.getFoodCountTo());
        for (int i = 0; i < foodCount; i++) {
            Food food = Food.getInstance();
            food.setValue(nextFloat(params.getFoodValueFrom(), params.getFoodValueTo()));
            food.setPosition(nextFloat(fromX, toX), nextFloat(fromY, toY));
            food.setRegion("food");
            food.setSize(32, 32);
            addGameObject(food);
        }
    }

    public void setSize(float worldSize) {
        width = worldSize;
        height = worldSize;
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

        for (GameObject gameObject : gameObjects) {
            gameObject.update(delta);
        }

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

    @Override
    public void dispose() {
        for (GameObject gameObject : gameObjects) {
            gameObject.dispose();
        }
    }
}
