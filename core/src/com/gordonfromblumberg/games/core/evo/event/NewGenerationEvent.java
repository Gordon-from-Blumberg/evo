package com.gordonfromblumberg.games.core.evo.event;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.event.Event;

public class NewGenerationEvent implements Event {

    private static final Pool<NewGenerationEvent> pool = new Pool<NewGenerationEvent>() {
        @Override
        protected NewGenerationEvent newObject() {
            return new NewGenerationEvent();
        }
    };

    private int generationNumber, creatureCount, foodCount;

    private NewGenerationEvent() {
    }

    public static NewGenerationEvent getInstance() {
        return pool.obtain();
    }

    public void release() {
        pool.free(this);
    }

    @Override
    public String getType() {
        return "NewGeneration";
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(int generationNumber) {
        this.generationNumber = generationNumber;
    }

    public int getCreatureCount() {
        return creatureCount;
    }

    public void setCreatureCount(int creatureCount) {
        this.creatureCount = creatureCount;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    @Override
    public void reset() {
        generationNumber = -1;
        creatureCount = 0;
        foodCount = 0;
    }
}
