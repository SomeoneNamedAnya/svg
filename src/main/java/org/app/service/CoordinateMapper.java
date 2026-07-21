package org.app.service;

import org.app.domain.ViewBox;

/**
 * Перевод координат данных (ViewBox) в пиксели картинки. Общая математика для кривой
 * (SvgPathBuilder) и для сетки/осей (AxesBuilder), чтобы формулы не дублировались.
 */
public class CoordinateMapper {

    private CoordinateMapper() {
    }

    /**
     * @param x координата X в данных
     * @param viewBox видимая область данных
     * @param canvasWidth ширина SVG картинка
     * @return соответствующая горизонтальная координата в пикселях
     */
    public static double toPixelX(double x, ViewBox viewBox, double canvasWidth) {
        return (x - viewBox.getXMin()) / viewBox.width() * canvasWidth;
    }

    /**
     * Преобразует Y с учётом того, что в SVG эта ось направлена вниз.
     *
     * @param y координата Y в данных
     * @param viewBox видимая область данных
     * @param canvasHeight высота SVG картинка
     * @return соответствующая вертикальная координата в пикселях
     */
    public static double toPixelY(double y, ViewBox viewBox, double canvasHeight) {
        // В SVG ось Y растёт вниз, поэтому инвертируем относительно координат данных.
        return canvasHeight - (y - viewBox.getYMin()) / viewBox.height() * canvasHeight;
    }
}
