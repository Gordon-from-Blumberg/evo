package com.gordonfromblumberg.games.core.evo.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public class SpawnPoint {
    private final Rectangle spawnArea = new Rectangle();
    private final Array<Creature> creatures = new Array<>();
}
