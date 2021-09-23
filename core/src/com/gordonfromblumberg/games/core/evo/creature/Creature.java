package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.model.PhysicsGameObject;
import com.gordonfromblumberg.games.core.evo.physics.CreatureMovingStrategy;
import com.gordonfromblumberg.games.core.evo.state.State;

public class Creature extends PhysicsGameObject {

    private static final Pool<Creature> pool = new Pool<Creature>() {
        @Override
        protected Creature newObject() {
            return new Creature();
        }
    };

    private final IntMap[] stateParams = new IntMap[State.values().length];

    private State state = State.WAITING;

    private Creature() {
        movingStrategy = new CreatureMovingStrategy();
    }

    public static Creature getInstance() {
        return pool.obtain();
    }

    public void release() {
        pool.free(this);
    }

    @Override
    public void update(float delta) {
        state.update(this, delta);
        super.update(delta);
    }

    public void setTarget(float x, float y) {
        if (movingStrategy == null) {
            CreatureMovingStrategy str = new CreatureMovingStrategy();
            str.setMaxVelocityForward(500);
            str.setMaxVelocityBackward(250);

            str.setMaxAngleVelocity(120);
            str.setMaxRotation(200);

            str.setMaxAcceleration(1500);
            str.setMaxDeceleration(2000);

            Gdx.app.log("Dec dist", String.valueOf(str.getDecelerationDistance()));
            movingStrategy = str;
        }

        ((CreatureMovingStrategy) movingStrategy).setTarget(x, y);
    }

    @Override
    protected void adjustPosition() {
        Vector2 position = this.position;
        Vector2 velocity = this.velocity;
        float halfSize = getWidth() / 2;
        if (position.x < halfSize) {
            position.x = halfSize;
            if (velocity.x < 0)
                velocity.x = 0;
        }
        if (position.x > gameWorld.width - halfSize) {
            position.x = gameWorld.width - halfSize;
            if (velocity.x > 0)
                velocity.x = 0;
        }
        if (position.y < halfSize) {
            position.y = halfSize;
            if (velocity.y < 0)
                velocity.y = 0;
        }
        if (position.y > gameWorld.height - halfSize) {
            position.y = gameWorld.height - halfSize;
            if (velocity.y > 0)
                velocity.y = 0;
        }
    }

    public void setMaxVelocityForward(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxVelocityForward(value);
    }

    public void setMaxVelocityBackward(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxVelocityBackward(value);
    }

    public void setMaxAngleVelocity(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxAngleVelocity(value);
    }

    public void setMaxRotation(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxRotation(value);
    }

    public void setMaxAcceleration(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxAcceleration(value);
    }

    public void setMaxDeceleration(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxDeceleration(value);
    }

    public void setDecelerationDist(float value) {
        ((CreatureMovingStrategy) movingStrategy).setDecelerationDistance(value);
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            state.enter(this);
        }
    }

    public IntMap<Object> getStateParams(State state) {
        if (stateParams[state.ordinal()] == null)
            stateParams[state.ordinal()] = new IntMap<>();
        return stateParams[state.ordinal()];
    }
}
