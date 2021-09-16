package com.gordonfromblumberg.games.core.common.physics;

import com.badlogic.gdx.math.Vector2;

public class ToTargetWithDecelerationMovingStrategy extends ToTargetMovingStrategy {
    protected float decelerationDistance, decelerationDistance2;
    protected float maxDeceleration, maxDeceleration2;

    public ToTargetWithDecelerationMovingStrategy() {}

    public ToTargetWithDecelerationMovingStrategy(float maxDeceleration) {
        setMaxDeceleration(maxDeceleration);
    }

    public ToTargetWithDecelerationMovingStrategy(float targetX, float targetY, float maxDeceleration) {
        super(targetX, targetY);

        setMaxDeceleration(maxDeceleration);
    }

    @Override
    public void setMaxVelocity(float maxVelocity) {
        super.setMaxVelocity(maxVelocity);

        float calculatedDecelerationDist = calcDecelerationDist();
        if (calculatedDecelerationDist > decelerationDistance) {
            setDecelerationDistance(calculatedDecelerationDist);
        }
    }

    @Override
    protected float getVelocityLimit() {
        final float desiredMovementLen2 = desiredMovement.len2();
        return maxVelocity2 * (desiredMovementLen2 < decelerationDistance2
                ? desiredMovementLen2 / decelerationDistance2
                : 1);
    }

    @Override
    protected void limitAcceleration(Vector2 velocity, Vector2 acceleration) {
        final float accelerationLimit = acceleration.dot(velocity) > 0
            ? maxAcceleration2
            : maxDeceleration2;

        if (accelerationLimit > 0)
            acceleration.limit2(accelerationLimit);
    }

    protected void setMaxDeceleration(float maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
        this.maxDeceleration2 = maxDeceleration * maxDeceleration;

        float calculatedDecelerationDist = calcDecelerationDist();
        if (calculatedDecelerationDist > decelerationDistance) {
            setDecelerationDistance(calculatedDecelerationDist);
        }
    }

    protected void setDecelerationDistance(float decelerationDistance) {
        this.decelerationDistance = decelerationDistance;
        this.decelerationDistance2 = decelerationDistance * decelerationDistance;
    }

    protected float calcDecelerationDist() {
        return maxVelocity2 / (2 * maxDeceleration);
    }
}
