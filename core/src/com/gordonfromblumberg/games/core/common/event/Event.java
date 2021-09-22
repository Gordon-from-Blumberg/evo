package com.gordonfromblumberg.games.core.common.event;

import com.gordonfromblumberg.games.core.common.utils.Poolable;

public interface Event extends Poolable {
    String getType();
}
