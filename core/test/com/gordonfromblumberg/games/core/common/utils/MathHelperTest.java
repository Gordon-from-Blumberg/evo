package com.gordonfromblumberg.games.core.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gordonfromblumberg.games.core.common.utils.MathHelper.clampAngleDeg;

public class MathHelperTest {
    @Test
    void clampAngleDegTest() {
        Assertions.assertEquals(0f, clampAngleDeg(0), 0.1f);
        Assertions.assertEquals(0f, clampAngleDeg(360), 0.1f);
        Assertions.assertEquals(0f, clampAngleDeg(-360), 0.1f);
        Assertions.assertEquals(0f, clampAngleDeg(-720), 0.1f);

        Assertions.assertEquals(1f, clampAngleDeg(1), 0.1f);
        Assertions.assertEquals(1f, clampAngleDeg(361), 0.1f);
        Assertions.assertEquals(1f, clampAngleDeg(-359), 0.1f);

        Assertions.assertEquals(-1.5f, clampAngleDeg(-1.5f), 0.1f);
        Assertions.assertEquals(-1.5f, clampAngleDeg(358.5f), 0.1f);
        Assertions.assertEquals(-1.5f, clampAngleDeg(-361.5f), 0.1f);

        Assertions.assertEquals(180f, clampAngleDeg(180f), 0.1f);
        Assertions.assertEquals(-180f, clampAngleDeg(-180f), 0.1f);
    }
}
