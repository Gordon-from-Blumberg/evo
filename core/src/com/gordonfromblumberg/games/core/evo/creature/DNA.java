package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.Gdx;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;

public class DNA {
    private static final float MUTATION_CHANCE = AbstractFactory.getInstance().configManager().getFloat("game.mutation.chance");
    private static final byte FEMALE_SHIFT = -1;
    private static final byte MALE_SHIFT = 0;

    private final byte[] genes = new byte[Gene.values().length * 2 - 1];

    private byte genderShift = FEMALE_SHIFT;

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
        if (gene == Gene.FERTILITY)
            return genes[0];
        return genes[gene.ordinal() * 2 + genderShift];
    }

    Gene getGene(int index) {
        if (index == 0)
            return Gene.FERTILITY;
        return Gene.values()[(index + 1) / 2];
    }

    void mutate() {
        for (int i = 0, len = genes.length; i < len; i++) {
            Gene geneType = getGene(i);
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

                if (geneType == Gene.FERTILITY)
                    genderShift = genes[0] > 0 ? FEMALE_SHIFT : MALE_SHIFT;

                if (Main.DEBUG)
                    Gdx.app.log("Mutation", geneType + ": " + current + " -> " + genes[geneType.ordinal()]);
            }
        }
    }

    void copy(DNA other) {
        other.genderShift = genderShift;
        System.arraycopy(genes, 0, other.genes, 0, genes.length);
    }

    void pair(DNA parent1, DNA parent2) {
        for (int i = 0, len = genes.length; i < len; i++) {
            genes[i] = RandomUtils.nextBool() ? parent1.genes[i] : parent2.genes[i];
        }
        genderShift = genes[0] > 0 ? FEMALE_SHIFT : MALE_SHIFT;
    }

    public String getDescription() {
        return "Genes: SIZE=" + genes[Gene.SIZE.ordinal()] + ", VELOCITY=" + genes[Gene.VELOCITY.ordinal()] + ", ROTATION=" + genes[Gene.ROTATION.ordinal()]
                + ", SENSE=" + genes[Gene.SENSE.ordinal()] + ", MOUTH_SIZE=" + genes[Gene.MOUTH_SIZE.ordinal()];
    }
}
