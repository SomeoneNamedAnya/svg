package org.app.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AxisTickCalculatorTest {

    @Test
    void choosesNiceStepForIntegerRange() {
        AxisTickCalculator.TickSet ticks = AxisTickCalculator.calculateTicks(-10, 10, 8);

        assertEquals(5, ticks.getStep(), 1e-12);
        assertEquals(List.of(-10.0, -5.0, 0.0, 5.0, 10.0), ticks.getValues());
    }

    @Test
    void choosesNiceStepForFractionalRange() {
        AxisTickCalculator.TickSet ticks = AxisTickCalculator.calculateTicks(0, 0.9, 5);

        assertEquals(0.2, ticks.getStep(), 1e-12);
        assertEquals(5, ticks.getValues().size());
        for (int i = 0; i < ticks.getValues().size(); i++) {
            assertEquals(i * 0.2, ticks.getValues().get(i), 1e-12);
        }
    }
}
