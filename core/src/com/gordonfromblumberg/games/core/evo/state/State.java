package com.gordonfromblumberg.games.core.evo.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.event.HomeReachedEvent;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;
import com.gordonfromblumberg.games.core.evo.world.SpawnPoint;

public enum State {
    WAITING {
        @Override
        public void enter(Creature creature) {
            creature.getStateParams(this).put(0, RandomUtils.nextFloat(0.4f));
            creature.setSpawnPoint(null);
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
            final float FOOD_SIZE_MOD = 0.1f;
            final float CREATURE_SIZE_MOD = 0.1f;
            IntMap<Object> stateParams = creature.getStateParams(this);
            float delay = (float) stateParams.get(0) - dt;
            final Vector2 position = creature.position;
            if (delay <= 0) {
                temp.setToRandomDirection().scl(RADIUS);
                temp2.set(creature.velocity)
                        .setLength(DIST)
                        .add(position)
                        .add(temp);
                creature.setTarget(temp2.x, temp2.y);
                delay = DELAY;
            }
            stateParams.put(0, delay);

            final float radius2 = (float) Math.pow(creature.getSenseRadius(), 2);
            EvoGameObject target = null;
            float minDist2 = Float.MAX_VALUE;
            Array<EvoGameObject> foods = creature.gameWorld.getGameObjects();
            for (int i = 0, size = foods.size; i < size; i++) {
                EvoGameObject food = foods.get(i);
                if (creature != food && creature.isEatable(food)) {
                    float coef = (1 - (food.getSize() - 0.5f) * FOOD_SIZE_MOD) * (1 + (creature.getSize() - 1) * CREATURE_SIZE_MOD);
                    float dist2 = position.dst2(food.position) * coef;
                    if (dist2 < minDist2) {
                        minDist2 = dist2;
                        target = food;
                    }
                }
            }

            if (target != null && minDist2 <= radius2) {
                creature.setTarget(target);
                creature.setState(MOVEMENT_TO_FOOD);
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
                EvoGameObject target = creature.getTarget();

                if (target == null) {
                    creature.setState(FOOD_SEARCHING);
                    return;
                }

                float dist = (creature.getSize() + target.getSize()) * 0.5f * 0.8f;
//                Gdx.app.log("MOVEMENT_TO_FOOD", "Creature size = " + creature.getSize() + ", target = " + target.getSize() + ", dist = " + dist);
                if (target.position.dst2(creature.position) <= dist * dist) {
                    Gdx.app.log("MOVEMENT_TO_FOOD", "Creature #" + creature.getId() + " reaches food, real dist = " + target.position.dst(creature.position));
                    creature.setState(EATING);
                }
            }
        }
    },

    EATING {
        @Override
        public void update(Creature creature, float dt) {
            EvoGameObject target = creature.getTarget();

            if (target == null) {
                creature.setState(FOOD_SEARCHING);
                return;
            }

            creature.eat(target, dt);

            creature.setState(creature.getSatiety() >= creature.getOffspringSatiety()
                    ? MOVEMENT_TO_HOME
                    : FOOD_SEARCHING
            );
        }
    },

    MOVEMENT_TO_HOME {
        @Override
        public void enter(Creature creature) {

            SpawnPoint closest = null;
            float closestDist = Float.MAX_VALUE;
            int index = -1;

            final Array<SpawnPoint> spawnPoints = creature.gameWorld.getSpawnPoints();
            for (int i = 0, count = creature.gameWorld.getSpawnPointCount(); i < count; i++) {
                SpawnPoint sp = spawnPoints.get(i);
                float dist = creature.position.dst2(sp.getX(), sp.getY());
                if (closest == null || dist < closestDist) {
                    closest = sp;
                    closestDist = dist;
                    index = i;
                }
            }

            creature.getStateParams(this).put(0, index);
            creature.setTarget(closest.getX(), closest.getY());
            creature.setForceMultiplier(0.7f);
            creature.setDecelerate(true);
        }

        @Override
        public void update(Creature creature, float dt) {
            // TODO check for predators

            if (creature.isTargetReached()) {
                creature.gameWorld.pushEvent(HomeReachedEvent.of(creature));
                creature.setSpawnPoint(creature.gameWorld.getSpawnPoints().get((int) creature.getStateParams(this).get(0)));
                creature.setState(HOME);
            }
        }
    },

    HOME {
        @Override
        public void update(Creature creature, float dt) {
        }
    };

    public void enter(Creature creature) {
    }
    public abstract void update(Creature creature, float dt);
}
