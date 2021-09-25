package com.gordonfromblumberg.games.core.common.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class ToTargetMovingStrategy extends AccelerationMovingStrategy {
    protected final Vector2 target = new Vector2();
    protected final Vector2 desiredMovement = new Vector2();
    protected final Vector2 desiredVelocity = new Vector2();

    public ToTargetMovingStrategy() {}

    public ToTargetMovingStrategy(float targetX, float targetY) {
        setTarget(targetX, targetY);
    }

    public ToTargetMovingStrategy(float maxVelocity, float maxAcceleration, float targetX, float targetY) {
        super(maxVelocity, maxAcceleration);

        setTarget(targetX, targetY);
    }

    @Override
    public void update(Vector2 position, Vector2 velocity, Vector2 acceleration, Vector2 rotation, float dt) {
        final Vector2 desiredMovement = this.desiredMovement;
        final Vector2 desiredVelocity = this.desiredVelocity;
        desiredMovement.set(target).sub(position);
//        Gdx.app.log("Des movement", desiredMovement + ", mag = " + desiredMovement.len());
        desiredVelocity.set(desiredMovement);
        adjustDesiredVelocity(velocity);
//        Gdx.app.log("Des velocity", desiredVelocity + ", mag = " + desiredVelocity.len());
        acceleration.set(desiredVelocity).sub(velocity).scl(1 / dt);
        super.update(position, velocity, acceleration, rotation, dt);
    }

    public void setTarget(float x, float y) {
        target.x = x;
        target.y = y;
    }

    protected void adjustDesiredVelocity(Vector2 velocity) {
        desiredVelocity.setLength2(maxVelocity2);
    }
}
