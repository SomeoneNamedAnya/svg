package org.app.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ViewBoxTest {

    @Test
    void rejectsNonFiniteBounds() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ViewBox(Double.NaN, 1, -1, 1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ViewBox(0, Double.POSITIVE_INFINITY, -1, 1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ViewBox(0, 1, Double.NEGATIVE_INFINITY, 1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ViewBox(0, 1, -1, Double.NaN))
        );
    }

    @Test
    void rejectsUnrepresentableSpan() {
        assertThrows(IllegalArgumentException.class,
                () -> new ViewBox(-Double.MAX_VALUE, Double.MAX_VALUE, -1, 1));
    }
}
