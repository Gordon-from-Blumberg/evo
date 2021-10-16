package com.gordonfromblumberg.games.core.evo.model;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.model.PhysicsGameObject;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public abstract class EvoGameObject extends PhysicsGameObject {
    private final Array<Creature> chasers = new Array<>();
    protected float size;

    public void addChaser(Creature creature) {
        chasers.add(creature);
    }

    public void removeChaser(Creature creature) {
        chasers.removeValue(creature, true);
    }

    public Array<Creature> getChasers() {
        return chasers;
    }

    public float getSize() {
        return size;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        size = width;
    }
}
