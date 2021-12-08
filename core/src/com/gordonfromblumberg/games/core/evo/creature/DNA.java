package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.Gdx;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;

public class DNA {
    private static final float MUTATION_CHANCE = AbstractFactory.getInstance().configManager().getFloat("game.mutation.chance");

    private final byte[] genes = new byte[Gene.values().length];

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

    byte getGeneValue(Gene gene) {
        return genes[gene.ordinal()];
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

                if (Main.DEBUG)
                    Gdx.app.log("Mutation", geneType + ": " + current + " -> " + genes[geneType.ordinal()]);
            }
        }
    }

    void copy(DNA other) {
        System.arraycopy(genes, 0, other.genes, 0, genes.length);
    }

    public String getDescription() {
        return "Genes: SIZE=" + genes[Gene.SIZE.ordinal()] + ", VELOCITY=" + genes[Gene.VELOCITY.ordinal()] + ", ROTATION=" + genes[Gene.ROTATION.ordinal()]
                + ", SENSE=" + genes[Gene.SENSE.ordinal()] + ", MOUTH_SIZE=" + genes[Gene.MOUTH_SIZE.ordinal()];
    }
}
