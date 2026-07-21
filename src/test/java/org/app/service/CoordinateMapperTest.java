package org.app.service;

import org.app.domain.ViewBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateMapperTest {

    private static final ViewBox VIEW_BOX = new ViewBox(-10, 10, -5, 5);

    @Test
    void mapsXCoordinatesToCanvas() {
        assertEquals(0, CoordinateMapper.toPixelX(-10, VIEW_BOX, 600), 1e-12);
        assertEquals(300, CoordinateMapper.toPixelX(0, VIEW_BOX, 600), 1e-12);
        assertEquals(600, CoordinateMapper.toPixelX(10, VIEW_BOX, 600), 1e-12);
    }

    @Test
    void mapsAndInvertsYCoordinates() {
        assertEquals(400, CoordinateMapper.toPixelY(-5, VIEW_BOX, 400), 1e-12);
        assertEquals(200, CoordinateMapper.toPixelY(0, VIEW_BOX, 400), 1e-12);
        assertEquals(0, CoordinateMapper.toPixelY(5, VIEW_BOX, 400), 1e-12);
    }
}
