package org.app.service;

import org.app.domain.AxesLayout;
import org.app.domain.TickLabel;
import org.app.domain.ViewBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AxesBuilderTest {

    @Test
    void keepsEdgeLabelsOnCanvasBounds() {
        AxesLayout layout = AxesBuilder.build(new ViewBox(-10, 10, -10, 10), 600, 400);

        assertTrue(layout.getTickLabels().stream().allMatch(label ->
                label.getX() >= 0 && label.getX() <= 600
                        && label.getY() >= 0 && label.getY() <= 400));

        TickLabel rightLabel = layout.getTickLabels().stream()
                .filter(label -> label.getText().equals("10") && label.getX() == 600)
                .findFirst()
                .orElseThrow();
        TickLabel topLabel = layout.getTickLabels().stream()
                .filter(label -> label.getText().equals("10") && label.getY() == 0)
                .findFirst()
                .orElseThrow();

        assertEquals(600, rightLabel.getX(), 1e-12);
        assertEquals(0, topLabel.getY(), 1e-12);
    }
}
