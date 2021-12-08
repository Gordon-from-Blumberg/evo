package com.gordonfromblumberg.games.core.evo.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntIntMap;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.creature.Gene;
import com.gordonfromblumberg.games.core.evo.food.Food;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;

public class Statistic {
    int generation;
    private int creatureCount, foodCount;
    private float foodValue;
    private final GeneStatistic[] statistics = new GeneStatistic[Gene.values().length];

    {
        for (Gene gene : Gene.values()) {
            statistics[gene.ordinal()] = new GeneStatistic();
        }
    }

    public int nextGeneration() {
        return ++generation;
    }

    public int getGeneration() {
        return generation;
    }

    public void add(EvoGameObject object) {
        if (object instanceof Creature) {
            creatureCount++;
            Creature creature = (Creature) object;
            for (Gene gene : Gene.values()) {
                statistics[gene.ordinal()].add(creature.getGeneValue(gene));
            }
        } else if (object instanceof Food) {
            foodCount++;
            foodValue += ((Food) object).getValue();
        }
    }

    public void reset() {
        creatureCount = foodCount = 0;
        foodValue = 0;
        for (Gene gene : Gene.values()) {
            statistics[gene.ordinal()].reset();
        }
    }

    public void print() {
        Gdx.app.log("Statistic", "Generation #" + generation);
        Gdx.app.log("Statistic", "Food: count = " + foodCount + ", total value = " + foodValue);
        for (Gene gene : Gene.values()) {
            Gdx.app.log("Statistic", gene + "\t" + statistics[gene.ordinal()]);
        }
    }

    class GeneStatistic {
        private final IntIntMap genes = new IntIntMap();
        private int minValue = Byte.MAX_VALUE;
        private int maxValue = Byte.MIN_VALUE;
        private int sum = 0;
        private float average = 0;

        void add(int geneValue) {
            genes.getAndIncrement(geneValue, 0, 1);
            if (geneValue < minValue)
                minValue = geneValue;
            if (geneValue > maxValue)
                maxValue = geneValue;
            sum += geneValue;
            average = (float) sum / creatureCount;
        }

        void reset() {
            genes.clear();
            minValue = Byte.MAX_VALUE;
            maxValue = Byte.MIN_VALUE;
            sum = 0;
            average = 0;
        }

        @Override
        public String toString() {
            return "MIN=" + minValue + "\tAVG=" + average + "\tMAX=" + maxValue;
        }
    }
}
