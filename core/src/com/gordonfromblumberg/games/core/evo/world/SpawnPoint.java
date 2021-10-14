package com.gordonfromblumberg.games.core.evo.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public class SpawnPoint {
    private final Rectangle spawnArea = new Rectangle();
    private final Array<Creature> creatures = new Array<>();

    public float getX() {
        return spawnArea.getX() + spawnArea.getWidth() / 2;
    }

    public float getY() {
        return spawnArea.getY() + spawnArea.getHeight() / 2;
    }

    public void setSpawnArea(float x, float y, float width, float height) {
        spawnArea.set(x, y, width, height);
    }

    public void addCreature(Creature creature) {
        creatures.add(creature);
        creature.setPosition(getX(), getY());
    }

    public void clear() {
        creatures.clear();
    }

    public int getCreatureCount() {
        return creatures.size;
    }
}
