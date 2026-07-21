package org.app.service;

import org.app.domain.FunctionMode;
import org.app.domain.LineStyle;
import org.app.domain.PlotConfig;
import org.app.domain.ViewBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlotConfigCodecTest {

    @Test
    void roundTripsExplicitAutoScaleConfiguration() {
        PlotConfig original = PlotConfig.explicit("sin(x)", -10, 10)
                .pointCount(1500)
                .colorHex("#123456")
                .strokeWidth(3.5)
                .lineStyle(LineStyle.DASHED)
                .autoScale(true)
                .showAxes(false)
                .showGrid(true)
                .axisColorHex("#654321")
                .axisStrokeWidth(2.5)
                .build();

        PlotConfig restored = PlotConfigCodec.decode(PlotConfigCodec.encode(original));

        assertAll(
                () -> assertEquals(FunctionMode.EXPLICIT, restored.getMode()),
                () -> assertEquals("sin(x)", restored.getFExpression()),
                () -> assertNull(restored.getGExpression()),
                () -> assertEquals(-10, restored.getRangeMin(), 1e-12),
                () -> assertEquals(10, restored.getRangeMax(), 1e-12),
                () -> assertEquals(1500, restored.getPointCount()),
                () -> assertEquals("#123456", restored.getColorHex()),
                () -> assertEquals(3.5, restored.getStrokeWidth(), 1e-12),
                () -> assertEquals(LineStyle.DASHED, restored.getLineStyle()),
                () -> assertTrue(restored.isAutoScale()),
                () -> assertNull(restored.getManualViewBox()),
                () -> assertFalse(restored.isShowAxes()),
                () -> assertTrue(restored.isShowGrid()),
                () -> assertEquals("#654321", restored.getAxisColorHex()),
                () -> assertEquals(2.5, restored.getAxisStrokeWidth(), 1e-12)
        );
    }

    @Test
    void roundTripsParametricManualViewBoxConfiguration() {
        PlotConfig original = PlotConfig.parametric("cos(t)", "sin(t)", 0, 2 * Math.PI)
                .autoScale(false)
                .manualViewBox(new ViewBox(-2, 2, -3, 3))
                .build();

        PlotConfig restored = PlotConfigCodec.decode(PlotConfigCodec.encode(original));

        assertEquals(FunctionMode.PARAMETRIC, restored.getMode());
        assertEquals("cos(t)", restored.getFExpression());
        assertEquals("sin(t)", restored.getGExpression());
        assertFalse(restored.isAutoScale());
        assertEquals(-2, restored.getManualViewBox().getXMin(), 1e-12);
        assertEquals(2, restored.getManualViewBox().getXMax(), 1e-12);
        assertEquals(-3, restored.getManualViewBox().getYMin(), 1e-12);
        assertEquals(3, restored.getManualViewBox().getYMax(), 1e-12);
    }

    @Test
    void rejectsDamagedHistoryRecord() {
        assertThrows(IllegalArgumentException.class, () -> PlotConfigCodec.decode("broken record"));
    }
}
