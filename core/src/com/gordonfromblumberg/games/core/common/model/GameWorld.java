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
    private final Array<Creature> creatures = new Array<>();
    private final Array<Food> foods = new Array<>();
    public Creature creature;

    private final EventProcessor eventProcessor = new EventProcessor();

    int generation = 1;
    public float width, height;

    private NinePatch background;

    private int maxCreatureCount = 0;
    private int maxFoodCount = 0;

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

        addCreature(creature);
    }

    public void generateFood() {
        foods.clear();

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
            addFood(food);
        }
    }

    public void setSize(float worldSize) {
        width = worldSize;
        height = worldSize;
    }

    public void addFood(Food food) {
        foods.add(food);
        food.setGameWorld(this);
        food.active = true;
        food.id = GameObject.nextId++;
        if (foods.size > maxFoodCount) maxFoodCount = creatures.size;
    }

    public void addCreature(Creature creature) {
        creatures.add(creature);
        creature.setGameWorld(this);
        creature.active = true;
        creature.id = GameObject.nextId++;
        if (creatures.size > maxCreatureCount) maxCreatureCount = creatures.size;
    }

    public void removeFood(Food food) {
        foods.removeValue(food, true);
        food.setGameWorld(null);
        food.active = false;
        food.release();
    }

    public void removeCreature(Creature creature) {
        creatures.removeValue(creature, true);
        creature.setGameWorld(null);
        creature.active = false;
        creature.release();
    }

    public void update(float delta) {
        time += delta;

        for (Food food : foods)
            food.update(delta);

        for (Creature creature : creatures) {
            creature.update(delta);
        }

        eventProcessor.process();

        if (time > 2) {
            time = 0;
            Gdx.app.log("GameWorld", "Creatures: current count = " + creatures.size + ", max count = " + maxCreatureCount);
            Gdx.app.log("GameWorld", "Foods: current count = " + foods.size + ", max count = " + maxFoodCount);
        }
    }

    public void render(Batch batch) {
        background.draw(batch, 0, 0, width, height);

        for (Food food : foods)
            food.render(batch);

        for (Creature creature : creatures) {
            creature.render(batch);
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
        for (GameObject gameObject : creatures) {
            gameObject.dispose();
        }
    }
}
