package com.gordonfromblumberg.games.core.common.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.physics.MovingStrategy;

public class PhysicsGameObject extends GameObject {
    public final Vector2 velocity = new Vector2();
    public final Vector2 acceleration = new Vector2();

    protected MovingStrategy movingStrategy;

    protected boolean turnInVelocityDirection = true;

    {
        this.colliding = true;
    }

    public PhysicsGameObject() {}

    @SuppressWarnings("rawtypes")
    public PhysicsGameObject(Pool pool) {
        super(pool);
    }

    @Override
    public void update(final float delta) {
        super.update(delta);

        if (movingStrategy != null)
            movingStrategy.update(position, velocity, acceleration, delta);

        if (turnInVelocityDirection)
            // vector 0 deg - right
            // sprite / polygon 0 deg - top
            setRotation(velocity.angleDeg() - 90);

        adjustPosition();
        polygon.setPosition(position.x, position.y);
    }

    /**
     * Checks whether position out of limit bounds and handles such case
     */
    protected void adjustPosition() {}
}
