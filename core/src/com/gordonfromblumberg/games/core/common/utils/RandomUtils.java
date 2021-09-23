package com.gordonfromblumberg.games.core.common.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random RAND = new Random();

    public static int nextInt(int from, int to) {
        return from == to ? from : from + RAND.nextInt(to - from);
    }

    public static float nextFloat(float max) {
        return max * RAND.nextFloat();
    }

    public static float nextFloat(float from, float to) {
        return from == to ? from : from + ((to - from) * RAND.nextFloat());
    }

    public static RandomGen randomGen(long seed) {
        return new RandomGen(seed);
    }

    public static class RandomGen {
        private final Random rand;

        private RandomGen(long seed) {
            rand = new Random(seed);
        }

        public int nextInt(int from, int to) {
            return from == to ? from : from + rand.nextInt(to - from);
        }

        public float nextFloat(float from, float to) {
            return from == to ? from : from + ((to - from) * rand.nextFloat());
        }
    }
}
