package com.gordonfromblumberg.games.core.evo.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.common.world.GameWorld;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public class SpawnPoint {
    private final Rectangle spawnArea = new Rectangle();
    private final Array<Creature> creatures = new Array<>();
    private GameWorld.Direction dir;

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
        switch (dir) {
            case BOTTOM:
                creature.setRotation(0);
                break;
            case RIGHT:
                creature.setRotation(90);
                break;
            case TOP:
                creature.setRotation(180);
                break;
            case LEFT:
                creature.setRotation(270);
                break;
        }
    }

    public void clear() {
        creatures.clear();
    }

    public int getCreatureCount() {
        return creatures.size;
    }

    public GameWorld.Direction getDir() {
        return dir;
    }

    public void setDir(GameWorld.Direction dir) {
        this.dir = dir;
    }
}
