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

    private int generationNumber;

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

    @Override
    public void reset() {
        generationNumber = -1;
    }
}
