package com.gordonfromblumberg.games.core.common.utils;

import com.badlogic.gdx.utils.Pool;

public interface Poolable extends Pool.Poolable {
    void release();
}
