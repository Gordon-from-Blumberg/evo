package com.gordonfromblumberg.games.core.evo.event;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.event.Event;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public class HomeReachedEvent implements Event {
    private static final Pool<HomeReachedEvent> pool = new Pool<HomeReachedEvent>() {
        @Override
        protected HomeReachedEvent newObject() {
            return new HomeReachedEvent();
        }
    };

    private Creature creature;

    public static HomeReachedEvent of(Creature creature) {
        HomeReachedEvent event = pool.obtain();
        event.creature = creature;
        return event;
    }

    @Override
    public String getType() {
        return "HomeReached";
    }

    public Creature getCreature() {
        return creature;
    }

    @Override
    public void release() {
        pool.free(this);
    }

    @Override
    public void reset() {
        creature = null;
    }
}
