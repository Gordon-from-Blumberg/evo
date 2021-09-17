package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.model.PhysicsGameObject;
import com.gordonfromblumberg.games.core.common.physics.ToTargetWithDecelerationMovingStrategy;

public class Creature extends PhysicsGameObject {

    private static final Pool<Creature> pool = new Pool<Creature>() {
        @Override
        protected Creature newObject() {
            return new Creature();
        }
    };

    private Creature() {
        ToTargetWithDecelerationMovingStrategy str = new ToTargetWithDecelerationMovingStrategy(1000);
        str.setMaxVelocity(200);
        str.setMaxAcceleration(500);

        movingStrategy = str;
    }

    public static Creature getInstance() {
        return pool.obtain();
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
