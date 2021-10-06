package com.gordonfromblumberg.games.core.common.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.event.Event;
import com.gordonfromblumberg.games.core.common.event.EventHandler;
import com.gordonfromblumberg.games.core.common.event.EventProcessor;
import com.gordonfromblumberg.games.core.common.screens.AbstractScreen;
import com.gordonfromblumberg.games.core.evo.WorldParams;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.event.NewGenerationEvent;
import com.gordonfromblumberg.games.core.evo.food.Food;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;
import com.gordonfromblumberg.games.core.evo.physics.CreatureMovingStrategy;

import java.util.Iterator;

import static com.gordonfromblumberg.games.core.common.utils.RandomUtils.*;

public class GameWorld implements Disposable {

    public final WorldParams params = new WorldParams();
    private final Array<EvoGameObject> gameObjects = new Array<>();
    public Creature herb, herb2;
    public Creature pred;

    private final EventProcessor eventProcessor = new EventProcessor();

    int generation;
    public float width, height;

    private boolean paused, started;
    private final Color pauseColor = Color.GRAY;
    private final BitmapFontCache pauseText;

    private NinePatch background;

    int maxGameObjectCount = 0;
    private float time = 0;

    public GameWorld() {
        final AssetManager assets = Main.getInstance().assets();

        background = new NinePatch(
                assets.get("image/texture_pack.atlas", TextureAtlas.class)
                        .findRegion("world-background"),
                1, 1, 1, 1
        );

        pauseText = new BitmapFontCache(assets.get("ui/uiskin.json", Skin.class).getFont("default-font"));
        pauseText.setText("PAUSE", 100, 100);
    }

    public void initialize(float size) {
        final float baseSize = Main.CREATURE_SIZE;

        setSize(size);

        herb = Creature.getInstance();
        herb.setRegion("herbivorous");
        herb.setPosition(baseSize / 2, baseSize / 2);
        herb.setSize(baseSize, baseSize);
        herb.setMaxVelocityForward(baseSize * 5);
        herb.setMaxVelocityBackward(baseSize * 2);
        herb.setMaxAngleVelocity(180);
        herb.setMaxRotation(250);
        herb.setMaxAcceleration(baseSize * 5f);
        herb.setMaxDeceleration(baseSize * 5.5f);
        herb.init();
        addGameObject(herb);

        herb2 = Creature.getInstance();
        herb2.setRegion("herbivorous");
        herb2.setPosition(size - baseSize / 2, size - baseSize / 2);
        herb2.setSize(baseSize, baseSize);
        herb2.setMaxVelocityForward(baseSize * 5);
        herb2.setMaxVelocityBackward(baseSize * 2);
        herb2.setMaxAngleVelocity(180);
        herb2.setMaxRotation(250);
        herb2.setMaxAcceleration(baseSize * 5f);
        herb2.setMaxDeceleration(baseSize * 5.5f);
        herb2.init();
        addGameObject(herb2);

        pred = Creature.getInstance();
        pred.setRegion("predator");
        pred.setPosition(size - baseSize / 2, baseSize / 2);
        pred.setSize(baseSize, baseSize);
        pred.setMaxVelocityForward(baseSize * 5.5f);
        pred.setMaxVelocityBackward(baseSize * 2);
        pred.setMaxAngleVelocity(110);
        pred.setMaxRotation(180);
        pred.setMaxAcceleration(baseSize * 5);
        pred.setMaxDeceleration(baseSize * 6.5f);
//        addCreature(pred);
    }

    public void newGeneration() {
        generation++;
        generateFood();
        NewGenerationEvent event = NewGenerationEvent.getInstance();
        event.setGenerationNumber(generation);
        eventProcessor.push(event);
        started = true;
    }

    public void generateFood() {
        Iterator<EvoGameObject> goIt = gameObjects.iterator();
        while (goIt.hasNext()) {
            EvoGameObject go = goIt.next();
            if (go instanceof Food) {
                Food food = (Food) go;
                food.setGameWorld(null);
                food.active = false;
                food.release();
                goIt.remove();
            }
        }

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
            food.setSize(Main.CREATURE_SIZE * 0.5f, Main.CREATURE_SIZE * 0.5f);
            addGameObject(food);
        }
    }

    public void setSize(float worldSize) {
        width = worldSize;
        height = worldSize;
    }

    public void addGameObject(EvoGameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.setGameWorld(this);
        gameObject.active = true;
        gameObject.id = GameObject.nextId++;

        if (gameObjects.size > maxGameObjectCount) maxGameObjectCount = gameObjects.size;
    }

    public void removeGameObject(EvoGameObject gameObject) {
        gameObjects.removeValue(gameObject, true);
        gameObject.setGameWorld(null);
        gameObject.active = false;
        gameObject.release();
    }

    public void update(float delta) {
        if (started && !paused) {
            time += delta;

            for (EvoGameObject go : gameObjects) {
                go.update(delta);
            }

            pred.setTarget(herb.position.x, herb.position.y);

            eventProcessor.process();

            if (time > 2) {
                time = 0;
                Gdx.app.log("GameWorld", "Game objects: current count = " + gameObjects.size + ", max count = " + maxGameObjectCount);
                Gdx.app.log("Max velocity", "Herb = " + ((CreatureMovingStrategy) herb.movingStrategy).maxVelMag + ", pred = " + ((CreatureMovingStrategy) pred.movingStrategy).maxVelMag);
                Gdx.app.log("Max acceleration", "Herb = " + ((CreatureMovingStrategy) herb.movingStrategy).maxAccMag + ", pred = " + ((CreatureMovingStrategy) pred.movingStrategy).maxAccMag);
            }
        }
    }

    public void render(Batch batch) {
        final Color origColor = batch.getColor();
        if (paused)
            batch.setColor(pauseColor);

        background.draw(batch, 0, 0, 0, 0, 64 * width, 64 * height, 1f/64, 1f/64, 0);

        if (paused) {
            for (EvoGameObject go : gameObjects) {
                go.sprite.setColor(pauseColor);
                go.render(batch);
                go.sprite.setColor(Color.WHITE);
            }
        } else {
            for (EvoGameObject go : gameObjects) {
                go.render(batch);
            }
        }

        if (paused) {
            pauseText.draw(batch);
            batch.setColor(origColor);
        }
    }

    public Array<EvoGameObject> getGameObjects() {
        return gameObjects;
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

    public void pause() {
        this.paused = !this.paused;
    }

    public void registerHandler(String type, EventHandler handler) {
        eventProcessor.registerHandler(type, handler);
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
