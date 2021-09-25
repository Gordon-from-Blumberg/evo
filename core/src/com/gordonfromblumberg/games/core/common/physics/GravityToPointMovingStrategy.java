package com.gordonfromblumberg.games.core.common.physics;

import com.badlogic.gdx.math.Vector2;

public class GravityToPointMovingStrategy extends GravityMovingStrategy {
    private final Vector2 point = new Vector2();
    private float gravityValue, gravityValue2;

    public GravityToPointMovingStrategy(float pointX, float pointY, float gravityValue) {
        point.x = pointX;
        point.y = pointY;
        this.gravityValue = gravityValue;
        this.gravityValue2 = gravityValue * gravityValue;
    }

    @Override
    public void update(Vector2 position, Vector2 velocity, Vector2 acceleration, Vector2 rotation, float dt) {
        gravity.set(position)
                .sub(point)
                .setLength2(gravityValue2);
        super.update(position, velocity, acceleration, rotation, dt);
    }
}
