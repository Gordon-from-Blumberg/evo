package com.gordonfromblumberg.games.core.evo.creature;

import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;

public class DNA {
    private static final float MUTATION_CHANCE = AbstractFactory.getInstance().configManager().getFloat("game.mutation.chance");
    private static final byte MIN_VALUE = -9;

    private final byte[] genes = new byte[Gene.values().length];

    enum Gene {
        FOOD_TYPE(false, false), // TODO
        SIZE(true, true),
        VELOCITY(true, true),
        SENSE(true, true),
        MOUTH_SIZE(true, true),
        FERTILITY(true, true, (byte) 1, (byte) 0, Byte.MAX_VALUE);

        private final boolean mutable;
        private final boolean mutableByOne;
        private final byte defaultValue;
        private final byte minValue;
        private final byte maxValue;

        Gene(boolean mutable, boolean mutableByOne) {
            this(mutable, mutableByOne, (byte) 0, MIN_VALUE, Byte.MAX_VALUE);
        }

        Gene(boolean mutable, boolean mutableByOne, byte defaultValue, byte minValue, byte maxValue) {
            this.mutable = mutable;
            this.mutableByOne = mutableByOne;
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }

    enum FoodType {
        HERBIVOROUS,
        PREDATOR;
    }

    DNA() {
        for (Gene geneType : Gene.values()) {
            genes[geneType.ordinal()] = geneType.defaultValue;
        }
    }

    FoodType getFoodType() {
        return FoodType.values()[genes[Gene.FOOD_TYPE.ordinal()]];
    }

    byte getSize() {
        return genes[Gene.SIZE.ordinal()];
    }

    byte getVelocity() {
        return genes[Gene.VELOCITY.ordinal()];
    }

    byte getSense() {
        return genes[Gene.SENSE.ordinal()];
    }

    byte getMouthSize() {
        return genes[Gene.MOUTH_SIZE.ordinal()];
    }

    byte getFertility() {
        return genes[Gene.FERTILITY.ordinal()];
    }

    void mutate() {
        for (Gene geneType : Gene.values()) {
            if (geneType.mutable && RandomUtils.nextBool(MUTATION_CHANCE)) {
                byte current = genes[geneType.ordinal()];
                if (current == geneType.minValue)
                    genes[geneType.ordinal()] = (byte) (current + 1);
                else if (current == geneType.maxValue)
                    genes[geneType.ordinal()] = (byte) (current - 1);
                else if (RandomUtils.nextBool())
                    genes[geneType.ordinal()] = (byte) (current + 1);
                else
                    genes[geneType.ordinal()] = (byte) (current - 1);
            }
        }
    }

    void copy(DNA other) {
        System.arraycopy(genes, 0, other.genes, 0, genes.length);
    }

    public String getDescription() {
        return "Genes: SIZE=" + genes[Gene.SIZE.ordinal()] + ", VELOCITY=" + genes[Gene.VELOCITY.ordinal()]
                + ", SENSE=" + genes[Gene.SENSE.ordinal()] + ", MOUTH_SIZE=" + genes[Gene.MOUTH_SIZE.ordinal()];
    }
}
