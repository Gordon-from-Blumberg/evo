package com.gordonfromblumberg.games.core.common.physics;

import com.badlogic.gdx.math.Vector2;

public interface MovingStrategy {
    /**
     * Modifies received position and velocity instances
     * @param position Current position
     * @param velocity Current velocity
     * @param acceleration Current acceleration
     * @param rotation Rotation: x - current angle, y - current angle velocity
     * @param dt Delta time
     */
    void update(Vector2 position, Vector2 velocity, Vector2 acceleration, Vector2 rotation, float dt);
}
