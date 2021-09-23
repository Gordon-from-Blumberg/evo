package com.gordonfromblumberg.games.core.evo.state;

import com.badlogic.gdx.utils.IntMap;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public enum State {
    WAITING {
        @Override
        public void enter(Creature creature) {
            creature.getStateParams(this).put(0, RandomUtils.nextFloat(0.4f));
        }

        @Override
        public void update(Creature creature, float dt) {
            IntMap<Object> stateParams = creature.getStateParams(this);
            float delay = (float) stateParams.get(0) - dt;
            if (delay <= 0)
                creature.setState(FOOD_SEARCHING);
            else
                stateParams.put(0, delay);
        }
    },

    FOOD_SEARCHING {
        @Override
        public void update(Creature creature, float dt) {

        }
    };

    public void enter(Creature creature) {
    }
    public abstract void update(Creature creature, float dt);
}
