package com.gordonfromblumberg.games.core.evo.food;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.model.GameObject;

public class Food extends GameObject {

    private static final Pool<Food> pool = new Pool<Food>() {
        @Override
        protected Food newObject() {
            return new Food();
        }
    };

    private float value;

    private Food() {
    }

    public static Food getInstance() {
        return pool.obtain();
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
