package com.gordonfromblumberg.games.core.common.event;

import com.badlogic.gdx.utils.Pool;

public interface Event extends Pool.Poolable {
    String getType();
}
