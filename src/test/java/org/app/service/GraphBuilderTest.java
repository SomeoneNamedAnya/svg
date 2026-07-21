package org.app.service;

import org.app.domain.PlotConfig;
import org.app.domain.PlotResult;
import org.app.exception.MathEvaluationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphBuilderTest {

    @Test
    void createsSeparatePathFragmentsAtAsymptote() {
        PlotResult result = GraphBuilder.build(
                PlotConfig.explicit("1/x", -10, 10).pointCount(1000).build());

        long moveCount = result.getPathData().chars().filter(character -> character == 'M').count();
        assertTrue(moveCount >= 2);
        assertFalse(result.getWarnings().isEmpty());
    }

    @Test
    void rejectsCurveWithOnlyNonFiniteValues() {
        PlotConfig config = PlotConfig.explicit("exp(1000)", -10, 10)
                .pointCount(100)
                .build();

        assertThrows(MathEvaluationException.class, () -> GraphBuilder.build(config));
    }

    @Test
    void buildsLargeConstantFunction() {
        PlotResult result = GraphBuilder.build(
                PlotConfig.explicit("10000000000", -10, 10).pointCount(1000).build());

        assertTrue(Double.isFinite(result.getViewBox().height()));
        assertFalse(result.getPathData().isEmpty());
    }
}
