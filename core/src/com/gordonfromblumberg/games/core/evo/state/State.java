package com.gordonfromblumberg.games.core.evo.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.food.Food;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;

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
            creature.setForceMultiplier(0.7f);
            creature.setDecelerate(false);
        }

        @Override
        public void update(Creature creature, float dt) {
            final float RADIUS = 2.5f;
            final float DIST = 5;
            IntMap<Object> stateParams = creature.getStateParams(this);
            float delay = (float) stateParams.get(0) - dt;
            final Vector2 position = creature.position;
            if (delay <= 0) {
                temp.setToRandomDirection().scl(Main.CREATURE_SIZE * RADIUS);
                temp2.set(creature.velocity)
                        .setLength(Main.CREATURE_SIZE * DIST)
                        .add(position)
                        .add(temp);
                creature.setTarget(temp2.x, temp2.y);
                delay = DELAY;
            }
            stateParams.put(0, delay);

            final float radius2 = (float) Math.pow(creature.getSenseRadius() * Main.CREATURE_SIZE, 2);
            EvoGameObject target = null;
            float minDist2 = Float.MAX_VALUE;
            Array<EvoGameObject> foods = creature.gameWorld.getGameObjects();
            for (int i = 0, size = foods.size; i < size; i++) {
                EvoGameObject food = foods.get(i);
                if (creature != food && creature.isEatable(food)) {
                    float dist2 = position.dst2(food.position);
                    if (dist2 < minDist2) {
                        minDist2 = dist2;
                        target = food;
                    }

                    if (target != null && minDist2 <= radius2) {
                        creature.setTarget(target);
                        creature.setState(MOVEMENT_TO_FOOD);
                    }
                }
            }
        }
    },

    MOVEMENT_TO_FOOD {
        @Override
        public void enter(Creature creature) {
            creature.setForceMultiplier(1);
            creature.setDecelerate(!creature.isPredator());
        }

        @Override
        public void update(Creature creature, float dt) {
            if (creature.isPredator()) {

            } else {
                // TODO: check food has not been eaten
                EvoGameObject target = (EvoGameObject) creature.getTarget();
                float dist = (creature.getSize() + target.getSize()) * Main.CREATURE_SIZE * 0.5f * 0.8f;
//                Gdx.app.log("MOVEMENT_TO_FOOD", "Creature size = " + creature.getSize() + ", target = " + target.getSize() + ", dist = " + dist);
                if (target.position.dst2(creature.position) <= dist * dist) {
                    Gdx.app.log("MOVEMENT_TO_FOOD", "Creature #" + creature.getId() + " eats food, real dist = " + target.position.dst(creature.position));
                    creature.eat(target); // TODO use state for eating
                    creature.setState(creature.getSatiety() >= creature.getOffspringSatiety()
                            ? MOVEMENT_TO_HOME
                            : FOOD_SEARCHING
                    );
                }
            }
        }
    },

    MOVEMENT_TO_HOME {
        @Override
        public void enter(Creature creature) {
            float worldWidth = creature.gameWorld.width * Main.CREATURE_SIZE;
            float worldHeight = creature.gameWorld.height * Main.CREATURE_SIZE;
            float x = creature.position.x;
            float y = creature.position.y;
            float halfSize = creature.getSize() * Main.CREATURE_SIZE / 2;
            float dx = x > worldWidth / 2 ? worldWidth - x - halfSize : halfSize - x;
            float dy = y > worldHeight / 2 ? worldHeight - y - halfSize : halfSize - y;
            if (Math.abs(dx) > Math.abs(dy)) {
                creature.setTarget(x, y + dy);
            } else {
                creature.setTarget(x + dx, y);
            }
            creature.setForceMultiplier(0.7f);
            creature.setDecelerate(true);
        }

        @Override
        public void update(Creature creature, float dt) {
            // TODO check for predators
        }
    };

    public void enter(Creature creature) {
    }
    public abstract void update(Creature creature, float dt);
}
