package com.gordonfromblumberg.games.core.evo.event;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.event.Event;

public class PauseEvent implements Event {
    private static final Pool<PauseEvent> pool = new Pool<PauseEvent>() {
        @Override
        protected PauseEvent newObject() {
            return new PauseEvent();
        }
    };

    private boolean paused;

    private PauseEvent() {}

    public static PauseEvent getInstance() {
        return pool.obtain();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public String getType() {
        return "Pause";
    }

    @Override
    public void release() {
        pool.free(this);
    }

    @Override
    public void reset() {
        paused = false;
    }
}
