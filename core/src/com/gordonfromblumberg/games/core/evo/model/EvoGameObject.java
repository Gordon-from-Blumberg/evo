package com.gordonfromblumberg.games.core.evo.model;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.model.PhysicsGameObject;

public abstract class EvoGameObject extends PhysicsGameObject {
    private final Array<EvoGameObject> chasers = new Array<>();

    public void addChaser(EvoGameObject gameObject) {
        chasers.add(gameObject);
    }

    public void removeChaser(EvoGameObject gameObject) {
        chasers.removeValue(gameObject, true);
    }

    public abstract float getSize();
}
