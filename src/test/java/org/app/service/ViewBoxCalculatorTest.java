package org.app.service;

import org.app.domain.CurvePoint;
import org.app.domain.ViewBox;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewBoxCalculatorTest {

    @Test
    void calculatesExtremesWithSevenAndHalfPercentPadding() {
        ViewBox viewBox = ViewBoxCalculator.calculate(List.of(
                CurvePoint.valid(0, -2),
                CurvePoint.valid(10, 2)
        ));

        assertEquals(-0.75, viewBox.getXMin(), 1e-12);
        assertEquals(10.75, viewBox.getXMax(), 1e-12);
        assertEquals(-2.3, viewBox.getYMin(), 1e-12);
        assertEquals(2.3, viewBox.getYMax(), 1e-12);
    }

    @Test
    void ignoresInvalidPoints() {
        ViewBox viewBox = ViewBoxCalculator.calculate(List.of(
                CurvePoint.invalid(),
                CurvePoint.valid(-1, -3),
                CurvePoint.valid(1, 3)
        ));

        assertEquals(-1.15, viewBox.getXMin(), 1e-12);
        assertEquals(1.15, viewBox.getXMax(), 1e-12);
        assertEquals(-3.45, viewBox.getYMin(), 1e-12);
        assertEquals(3.45, viewBox.getYMax(), 1e-12);
    }

    @Test
    void expandsZeroSpanForConstantPoint() {
        ViewBox viewBox = ViewBoxCalculator.calculate(List.of(CurvePoint.valid(2, 3)));

        assertEquals(1.5e-7, viewBox.width(), 1e-12);
        assertEquals(1.5e-7, viewBox.height(), 1e-12);
    }

    @Test
    void rejectsCollectionWithoutValidPoints() {
        assertThrows(IllegalStateException.class,
                () -> ViewBoxCalculator.calculate(List.of(CurvePoint.invalid(), CurvePoint.invalid())));
    }

    @Test
    void expandsLargeConstantCoordinate() {
        ViewBox viewBox = ViewBoxCalculator.calculate(List.of(
                CurvePoint.valid(-1, 10_000_000_000.0),
                CurvePoint.valid(1, 10_000_000_000.0)
        ));

        assertTrue(Double.isFinite(viewBox.getYMin()));
        assertTrue(Double.isFinite(viewBox.getYMax()));
        assertTrue(Double.isFinite(viewBox.height()));
        assertTrue(viewBox.getYMin() < 10_000_000_000.0);
        assertTrue(viewBox.getYMax() > 10_000_000_000.0);
    }

    @Test
    void rejectsUnrepresentableCoordinateSpan() {
        assertThrows(IllegalArgumentException.class, () -> ViewBoxCalculator.calculate(List.of(
                CurvePoint.valid(-Double.MAX_VALUE, 0),
                CurvePoint.valid(Double.MAX_VALUE, 1)
        )));
    }

    @Test
    void expandsFiniteLimitConstants() {
        for (double value : List.of(Double.MAX_VALUE, -Double.MAX_VALUE)) {
            ViewBox viewBox = ViewBoxCalculator.calculate(List.of(CurvePoint.valid(value, value)));

            assertTrue(Double.isFinite(viewBox.width()));
            assertTrue(Double.isFinite(viewBox.height()));
        }
    }
}
