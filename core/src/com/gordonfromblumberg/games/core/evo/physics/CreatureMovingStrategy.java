package com.gordonfromblumberg.games.core.evo.physics;

import com.gordonfromblumberg.games.core.common.physics.ToTargetWithDecelerationMovingStrategy;

public class CreatureMovingStrategy extends ToTargetWithDecelerationMovingStrategy {
    private float maxVelocityForward, maxVelocityBackward;
    private float maxAngleVelocity;
}
