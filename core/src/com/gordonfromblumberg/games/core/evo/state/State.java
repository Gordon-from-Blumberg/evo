package com.gordonfromblumberg.games.core.evo.state;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.gordonfromblumberg.games.core.common.Main;
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
        private final float DELAY = 0.4f;
        private final Vector2 temp = new Vector2();
        private final Vector2 temp2 = new Vector2();

        @Override
        public void enter(Creature creature) {
            creature.getStateParams(this).put(0, DELAY);
        }

        @Override
        public void update(Creature creature, float dt) {
            final float RADIUS = 2.5f;
            final float DIST = 5;
            IntMap<Object> stateParams = creature.getStateParams(this);
            float delay = (float) stateParams.get(0) - dt;
            if (delay <= 0) {
                temp.setToRandomDirection().scl(Main.CREATURE_SIZE * RADIUS);
                temp2.set(creature.velocity)
                        .setLength(Main.CREATURE_SIZE * DIST)
                        .add(creature.position)
                        .add(temp);
                creature.setTarget(temp2.x, temp2.y);
                delay = DELAY;
            }
            stateParams.put(0, delay);
        }
    };

    public void enter(Creature creature) {
    }
    public abstract void update(Creature creature, float dt);
}
