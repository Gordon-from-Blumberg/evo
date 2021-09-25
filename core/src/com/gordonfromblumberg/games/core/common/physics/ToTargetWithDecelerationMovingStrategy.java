package com.gordonfromblumberg.games.core.common.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;

public class ToTargetWithDecelerationMovingStrategy extends ToTargetMovingStrategy {
    protected float decelerationDistance, decelerationDistance2;
    protected float maxDeceleration, maxDeceleration2;

    protected float targetVelocityLimit2 = 0.1f;
    protected float targetRadius2 = 0.1f;
    protected boolean targetReached = true;

    public ToTargetWithDecelerationMovingStrategy() {}

    public ToTargetWithDecelerationMovingStrategy(float maxDeceleration) {
        setMaxDeceleration(maxDeceleration);
    }

    public ToTargetWithDecelerationMovingStrategy(float targetX, float targetY, float maxDeceleration) {
        super(targetX, targetY);

        setMaxDeceleration(maxDeceleration);
    }

    @Override
    public void update(Vector2 position, Vector2 velocity, Vector2 acceleration, Vector2 rotation, float dt) {
        if (targetReached)
            return;

        super.update(position, velocity, acceleration, rotation, dt);
    }

    @Override
    public void setMaxVelocity(float maxVelocity) {
        super.setMaxVelocity(maxVelocity);

        float calculatedDecelerationDist = calcDecelerationDist();
        if (calculatedDecelerationDist > decelerationDistance && calculatedDecelerationDist < Float.MAX_VALUE) {
            setDecelerationDistance(calculatedDecelerationDist);
        }
    }

    @Override
    public void setTarget(float x, float y) {
        super.setTarget(x, y);
        targetReached = false;
    }

    public float getDecelerationDistance() {
        return decelerationDistance;
    }

    @Override
    protected void limitAcceleration(Vector2 velocity, Vector2 acceleration) {
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
    }

    public void setMaxDeceleration(float maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
        this.maxDeceleration2 = maxDeceleration * maxDeceleration;

        float calculatedDecelerationDist = calcDecelerationDist();
        if (calculatedDecelerationDist > decelerationDistance && calculatedDecelerationDist < Float.MAX_VALUE) {
            setDecelerationDistance(calculatedDecelerationDist);
        }
    }

    public void setDecelerationDistance(float decelerationDistance) {
        this.decelerationDistance = decelerationDistance;
        this.decelerationDistance2 = decelerationDistance * decelerationDistance;
    }

    protected float calcDecelerationDist() {
        return 1.1f * maxVelocity2 / (2 * maxDeceleration);
    }

    @Override
    protected void adjustDesiredVelocity(Vector2 velocity) {
        float desMovLen2 = desiredMovement.len2();

        if (desMovLen2 <= targetRadius2 && velocity.len2() <= targetVelocityLimit2)
            targetReached = true;

//        Gdx.app.log("Decel dist", "decel dist = " + decelerationDistance2 + ", desired movnt = " + desMovLen2);
        if (desMovLen2 < decelerationDistance2) {
            desiredVelocity.setLength2(maxVelocity2 * desMovLen2 / decelerationDistance2);
//            Gdx.app.log("", "desVelocity = " + desiredVelocity);
        } else {
            super.adjustDesiredVelocity(velocity);
        }
    }
}
