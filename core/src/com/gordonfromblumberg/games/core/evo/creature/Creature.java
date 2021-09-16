package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.model.PhysicsGameObject;
import com.gordonfromblumberg.games.core.common.physics.ToTargetWithDecelerationMovingStrategy;

public class Creature extends PhysicsGameObject {

    public Creature(Pool pool) {
        super(pool);

        ToTargetWithDecelerationMovingStrategy str = new ToTargetWithDecelerationMovingStrategy(100);
        str.setMaxVelocity(100);
        str.setMaxAcceleration(200);

        movingStrategy = str;
    }

    public void setTarget(float x, float y) {
        ((ToTargetWithDecelerationMovingStrategy) movingStrategy).setTarget(x, y);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        sprite.setRotation(velocity.angleDeg());
    }
}
