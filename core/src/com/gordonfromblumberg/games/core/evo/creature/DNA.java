package com.gordonfromblumberg.games.core.evo.creature;

import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;

public class DNA {
    private static final float MUTATION_CHANCE = AbstractFactory.getInstance().configManager().getFloat("game.mutation.chance");
    private static final byte MIN_VALUE = -9;

    private final byte[] genes = new byte[Gene.values().length];

    enum Gene {
        FOOD_TYPE(false, false),
        SIZE(true, true),
        VELOCITY(true, true),
        SENSE(true, true);

        private final boolean mutable;
        private final boolean mutableByOne;

        Gene(boolean mutable, boolean mutableByOne) {
            this.mutable = mutable;
            this.mutableByOne = mutableByOne;
        }
    }

    enum FoodType {
        HERBIVOROUS,
        PREDATOR;
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

    void copy(DNA other) {
        System.arraycopy(genes, 0, other.genes, 0, genes.length);
    }

    void mutate() {
        for (Gene geneType : Gene.values()) {
            if (geneType.mutable && RandomUtils.nextBool(MUTATION_CHANCE)) {
                byte current = genes[geneType.ordinal()];
                if (current == MIN_VALUE)
                    genes[geneType.ordinal()] = MIN_VALUE + 1;
                else if (RandomUtils.nextBool())
                    genes[geneType.ordinal()] = (byte) (current + 1);
                else
                    genes[geneType.ordinal()] = (byte) (current - 1);
            }
        }
    }

    public String getDescription() {
        return "Genes: SIZE=" + genes[Gene.SIZE.ordinal()] + ", VELOCITY=" + genes[Gene.VELOCITY.ordinal()]
                + ", SENSE=" + genes[Gene.SENSE.ordinal()];
    }
}
