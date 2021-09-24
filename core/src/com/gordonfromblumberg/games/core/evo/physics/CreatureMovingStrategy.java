package com.gordonfromblumberg.games.core.evo.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gordonfromblumberg.games.core.common.physics.ToTargetWithDecelerationMovingStrategy;
import com.gordonfromblumberg.games.core.common.utils.MathHelper;

public class CreatureMovingStrategy extends ToTargetWithDecelerationMovingStrategy {
    private static final Vector2 temp = new Vector2();

    private float maxVelocityForward, maxVelocityBackward;
    private float maxAngleVelocity;

    public CreatureMovingStrategy() {
        friction = 0.96f;
    }

    @Override
    protected void updateVelocity(Vector2 velocity, Vector2 acceleration, float dt) {
        Vector2 newVelocity = temp.set(velocity).mulAdd(acceleration, dt);

        float maxAngleVelDiff = maxAngleVelocity * dt;
        float angleVelDiff = MathHelper.clampAngleDeg(newVelocity.angleDeg(velocity));
        if (Math.abs(angleVelDiff) > maxAngleVelDiff) {
            float realAngleVelDiff = MathUtils.clamp(angleVelDiff, -maxAngleVelDiff, maxAngleVelDiff);

            // todo
        }
    }
}
