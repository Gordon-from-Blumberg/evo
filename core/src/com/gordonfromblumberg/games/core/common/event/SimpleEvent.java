package com.gordonfromblumberg.games.core.common.event;

import com.badlogic.gdx.utils.Pool;

public class SimpleEvent implements Event {

    private static final Pool<SimpleEvent> pool = new Pool<SimpleEvent>() {
        @Override
        protected SimpleEvent newObject() {
            return new SimpleEvent();
        }
    };

    private String type;

    private SimpleEvent() {
    }

    public static SimpleEvent getInstance() {
        return pool.obtain();
    }

    public static SimpleEvent of(String type) {
        SimpleEvent event = pool.obtain();
        event.type = type;
        return event;
    }

    public void release() {
        pool.free(this);
    }


    @Override
    public String getType() {
        return type;
    }

    @Override
    public void reset() {
        this.type = null;
    }
}
