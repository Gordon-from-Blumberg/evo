package com.gordonfromblumberg.games.core.common.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class ToTargetMovingStrategy extends AccelerationMovingStrategy {
    private static final float DEC_DIST_COEF = 1.05f;

    protected final Vector2 target = new Vector2();
    protected final Vector2 desiredMovement = new Vector2();
    protected final Vector2 desiredVelocity = new Vector2();

    protected boolean decelerate;

    protected float decelerationDistance, decelerationDistance2;
    protected float maxDeceleration, maxDeceleration2;

    protected float targetVelocityLimit2 = 0.1f;
    protected float targetRadius2 = 0.1f;
    protected boolean targetReached = true;

    public ToTargetMovingStrategy() {}

    @Override
    public void update(Vector2 position, Vector2 velocity, Vector2 acceleration, Vector2 rotation, float dt) {
        if (targetReached)
            return;

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

    @Override
    public void setMaxVelocity(float maxVelocity) {
        super.setMaxVelocity(maxVelocity);

        float calculatedDecelerationDist = DEC_DIST_COEF * calcDecelerationDist();
        if (calculatedDecelerationDist < Float.MAX_VALUE) {
            setDecelerationDistance(calculatedDecelerationDist);
        }
    }

    public void setMaxDeceleration(float maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
        this.maxDeceleration2 = maxDeceleration * maxDeceleration;

        float calculatedDecelerationDist = DEC_DIST_COEF * calcDecelerationDist();
        if (calculatedDecelerationDist < Float.MAX_VALUE) {
            setDecelerationDistance(calculatedDecelerationDist);
        }
    }

    public void setDecelerationDistance(float decelerationDistance) {
        this.decelerationDistance = decelerationDistance;
        this.decelerationDistance2 = decelerationDistance * decelerationDistance;
    }

    public void setTarget(float x, float y) {
        target.x = x;
        target.y = y;
        targetReached = false;
    }

    public void setDecelerate(boolean decelerate) {
        this.decelerate = decelerate;
    }

    @Override
    protected void limitAcceleration(Vector2 velocity, Vector2 acceleration) {
        if (decelerate) {
            float desMovLen2 = desiredMovement.len2();
            float desVelocityToVelocityAngle = desiredVelocity.angleDeg(velocity);
            boolean decelerate = acceleration.dot(velocity) < 0
                    && (desVelocityToVelocityAngle < 10 || desVelocityToVelocityAngle > 350);

            if (decelerate) {
                float velocity2 = velocity.len2();
                float limit2 = velocity2 * velocity2 / (4 * desMovLen2);
                if (limit2 > maxDeceleration2)
                    limit2 = maxDeceleration2;
                acceleration.setLength2(limit2);
            } else {
                acceleration.limit2(maxAcceleration2);
            }
        } else {
            acceleration.limit2(maxAcceleration2);
        }
    }

    public float getDecelerationDistance() {
        return decelerationDistance;
    }

    protected float calcDecelerationDist() {
        return maxVelocity2 / (2 * maxDeceleration);
    }

    protected void adjustDesiredVelocity(Vector2 velocity) {
        float desMovLen2 = desiredMovement.len2();

        if (desMovLen2 <= targetRadius2 && velocity.len2() <= targetVelocityLimit2)
            targetReached = true;

//        Gdx.app.log("Decel dist", "decel dist = " + decelerationDistance2 + ", desired movnt = " + desMovLen2);
        if (decelerate && desMovLen2 < decelerationDistance2) {
            desiredVelocity.setLength2(maxVelocity2 * desMovLen2 / decelerationDistance2);
//            Gdx.app.log("", "desVelocity = " + desiredVelocity);
        } else {
            desiredVelocity.setLength2(maxVelocity2);
        }
    }
}
