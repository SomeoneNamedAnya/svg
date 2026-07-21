package org.app.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DecimalFormatterTest {

    @Test
    void formatsAndRoundsUsingDotSeparator() {
        assertAll(
                () -> assertEquals("4.00", DecimalFormatter.format(4, 2)),
                () -> assertEquals("-4.57", DecimalFormatter.format(-4.567, 2)),
                () -> assertEquals("0.05", DecimalFormatter.format(0.05, 2)),
                () -> assertEquals("10", DecimalFormatter.format(9.6, 0)),
                () -> assertEquals("0.00", DecimalFormatter.format(-0.004, 2))
        );
    }
}
