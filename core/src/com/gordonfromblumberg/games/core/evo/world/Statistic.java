package com.gordonfromblumberg.games.core.evo.world;

import com.badlogic.gdx.utils.IntIntMap;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;

public class Statistic {
    private int generation;
    private int creatureCount, foodCount;
    private final GeneStatistic sizeStatistic = new GeneStatistic();

    void add(EvoGameObject object) {
        if (object instanceof Creature) {
            Creature creature = (Creature) object;
            sizeStatistic.add(creature.getSizeGene());
        }
    }

    void reset() {
        sizeStatistic.clear();
    }

    static class GeneStatistic {
        private final IntIntMap genes = new IntIntMap();

        void add(int geneValue) {
            genes.getAndIncrement(geneValue, 0, 1);
        }

        void clear() {
            genes.clear();
        }

        @Override
        public String toString() {
            return ""; //TODO
        }
    }
}
