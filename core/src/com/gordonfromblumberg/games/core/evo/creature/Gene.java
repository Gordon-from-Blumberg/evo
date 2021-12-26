package com.gordonfromblumberg.games.core.evo.creature;

public enum Gene {
    FERTILITY(true, true, (byte) 1),
    FOOD_TYPE(false, false), // TODO
    SIZE(true, true),
    VELOCITY(true, true),
    ROTATION(true, true),
    SENSE(true, true),
    MOUTH_SIZE(true, true);

    private static final byte MIN_VALUE = -9;

    final boolean mutable;
    final boolean mutableByOne;
    final byte defaultValue;
    final byte minValue;
    final byte maxValue;

    Gene(boolean mutable, boolean mutableByOne) {
        this(mutable, mutableByOne, (byte) 0, MIN_VALUE, Byte.MAX_VALUE);
    }

    Gene(boolean mutable, boolean mutableByOne, byte defaultValue) {
        this(mutable, mutableByOne, defaultValue, MIN_VALUE, Byte.MAX_VALUE);
    }

    Gene(boolean mutable, boolean mutableByOne, byte defaultValue, byte minValue, byte maxValue) {
        this.mutable = mutable;
        this.mutableByOne = mutableByOne;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
