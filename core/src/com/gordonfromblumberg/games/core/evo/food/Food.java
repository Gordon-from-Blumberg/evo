package com.gordonfromblumberg.games.core.evo.food;

import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.model.GameObject;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;

public class Food extends EvoGameObject {

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

    public void wasEaten(float value) {
        this.value -= value;
        updateSize();
    }

    public void updateSize() {
        float size = (float) Math.sqrt(value / 40);
        setSize(size, size);
    }

    @Override
    public void release() {
        pool.free(this);
    }
}
