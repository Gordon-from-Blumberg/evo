package com.gordonfromblumberg.games.core.evo.creature;

public class DNA {
    private static final int MIN_VALUE = -9;

    private static final int FOOD_TYPE    = 0;
    private static final int SIZE         = 1;
    private static final int VELOCITY     = 2;
    private static final int SENSE        = 3;

    private final byte[] genes = new byte[4];

    enum FoodType {
        HERBIVOROUS,
        PREDATOR;
    }

    FoodType getFoodType() {
        return FoodType.values()[genes[FOOD_TYPE]];
    }

    float getSize() {
        return 1 + 0.1f * genes[SIZE];
    }

    float getVelocity() {
        return 1 + 0.1f * genes[VELOCITY];
    }

    float getVelocityMod() {
        return 0.1f * genes[VELOCITY];
    }

    float getSense() {
        return 1 + 0.1f * genes[SENSE];
    }

    float getSenseMod() {
        return 0.1f * genes[SENSE];
    }
}
