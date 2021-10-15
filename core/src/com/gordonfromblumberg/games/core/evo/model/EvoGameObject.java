package com.gordonfromblumberg.games.core.evo.model;

import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.model.PhysicsGameObject;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public abstract class EvoGameObject extends PhysicsGameObject {
    private final Array<Creature> chasers = new Array<>();

    public void addChaser(Creature creature) {
        chasers.add(creature);
    }

    public void removeChaser(Creature creature) {
        chasers.removeValue(creature, true);
    }

    public Array<Creature> getChasers() {
        return chasers;
    }

    public abstract float getSize();
}
