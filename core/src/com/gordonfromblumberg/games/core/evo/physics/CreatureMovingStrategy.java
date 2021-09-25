package com.gordonfromblumberg.games.core.evo.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gordonfromblumberg.games.core.common.physics.ToTargetWithDecelerationMovingStrategy;
import com.gordonfromblumberg.games.core.common.utils.MathHelper;

public class CreatureMovingStrategy extends ToTargetWithDecelerationMovingStrategy {
    private static final Vector2 temp = new Vector2();

    public float maxVelMag = 0;
    public float maxAccMag = 0;

    private float maxVelocityForward, maxVelocityBackward;
    private float maxAngleVelocity, maxRotation;

    public CreatureMovingStrategy() {
        friction = 1.5f;
    }

    @Override
    protected void updateVelocity(Vector2 velocity, Vector2 acceleration, Vector2 rotation, float dt) {
//        Gdx.app.log("Acceleration", acceleration + ", mag = " + acceleration.len());
        Vector2 newVelocity = temp.set(velocity).mulAdd(acceleration, dt);

        // limit change of velocity angle
        float maxAngleVelDiff = maxAngleVelocity * dt;
        float angleVelDiff = MathHelper.clampAngleDeg(newVelocity.angleDeg(velocity));
        if (Math.abs(angleVelDiff) > maxAngleVelDiff) {
            float realAngleVelDiff = MathUtils.clamp(angleVelDiff, -maxAngleVelDiff, maxAngleVelDiff);
            temp.rotateDeg(realAngleVelDiff - angleVelDiff);
        }

        // limit rotation
        float velAngle = temp.angleDeg() - 90;
        float desRotation = targetReached ? velAngle : desiredMovement.angleDeg() - 90;
        float rotationDiff = MathHelper.clampAngleDeg(desRotation - rotation.x);
        float maxRotationDiff = maxRotation * dt;
        if (Math.abs(rotationDiff) > maxRotationDiff) {
            rotationDiff = MathUtils.clamp(rotationDiff, -maxRotationDiff, maxRotationDiff);
        }
//        Gdx.app.log("Rotation diff", "velAngle=" + velAngle + ", desRot=" + desRotation + ", rotDiff=" + rotationDiff);
        rotation.x = MathHelper.clampAngleDeg(rotation.x + rotationDiff);

        // limit velocity magnitude
        float velToRotationAngle = Math.abs(MathHelper.clampAngleDeg(velAngle - rotation.x));
        float velocityLimit = velToRotationAngle > 90
                ? maxVelocityBackward
                : MathUtils.lerp(maxVelocityForward, maxVelocityBackward, velToRotationAngle / 90);
//        Gdx.app.log("Velocity", "Before limit " + temp.len() + ", limit = " + velocityLimit);
        temp.limit2(velocityLimit * velocityLimit);
//        Gdx.app.log("Velocity", "After limit " + temp.len());

        velocity.set(temp);

        float velMag = velocity.len();
        if (velMag > maxVelMag)
            maxVelMag = velMag;
        float accMag = acceleration.len();
        if (accMag > maxAccMag)
            maxAccMag = accMag;
    }

    @Override
    protected void rotate(Vector2 velocity, Vector2 rotation, float dt) {
    }

    public float getMaxVelocityForward() {
        return maxVelocityForward;
    }

    public void setMaxVelocityForward(float maxVelocityForward) {
        this.maxVelocityForward = maxVelocityForward;
        setMaxVelocity(maxVelocityForward);
    }

    public float getMaxVelocityBackward() {
        return maxVelocityBackward;
    }

    public void setMaxVelocityBackward(float maxVelocityBackward) {
        this.maxVelocityBackward = maxVelocityBackward;
    }

    public float getMaxAngleVelocity() {
        return maxAngleVelocity;
    }

    public void setMaxAngleVelocity(float maxAngleVelocity) {
        this.maxAngleVelocity = maxAngleVelocity;
    }

    public float getMaxRotation() {
        return maxRotation;
    }

    public void setMaxRotation(float maxRotation) {
        this.maxRotation = maxRotation;
    }
}
