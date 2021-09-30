package com.gordonfromblumberg.games.core.evo.creature;

public class DNA {
    private static final int FOOD_TYPE    = 0;
    private static final int SIZE         = 1;
    private static final int VELOCITY     = 2;
    private static final int SENSE_RADIUS = 3;

    private final byte[] genes = new byte[4];

    public enum FoodType {
        HERBIVOROUS,
        PREDATOR;
    }

    float getSize() {
        return 1 + 0.1f * genes[SIZE];
    }
}
