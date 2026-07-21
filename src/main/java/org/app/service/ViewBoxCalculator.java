package org.app.service;

import org.app.domain.CurvePoint;
import org.app.domain.ViewBox;

import java.util.List;

/**
 * Считает область отображения графика по вычисленным точкам с отступом 5-10% по краям
 */
public class ViewBoxCalculator {

    // середина диапазона 5-10%
    private static final double PADDING_RATIO = 0.075;
    // защита от вырожденного (нулевого) размаха, если кривая константна по одной из осей
    private static final double MIN_SPAN = 1e-6;

    private ViewBoxCalculator() {
    }

    /**
     * Вычисляет минимальную область содержащую все валидные точки и добавляет поля
     *
     * @param points вычисленные точки кривой
     * @return область отображения с отступом
     * @throws IllegalStateException если среди точек нет ни одной валидной
     */
    public static ViewBox calculate(List<CurvePoint> points) {
        double xMin = Double.POSITIVE_INFINITY;
        double xMax = Double.NEGATIVE_INFINITY;
        double yMin = Double.POSITIVE_INFINITY;
        double yMax = Double.NEGATIVE_INFINITY;
        boolean any = false;

        for (CurvePoint point : points) {
            if (!point.isValid()) {
                continue;
            }
            any = true;
            xMin = Math.min(xMin, point.getX());
            xMax = Math.max(xMax, point.getX());
            yMin = Math.min(yMin, point.getY());
            yMax = Math.max(yMax, point.getY());
        }

        if (!any) {
            throw new IllegalStateException("Не удалось построить график: ни одна точка не вычислена");
        }

        double xPad = calculatePadding(xMin, xMax);
        double yPad = calculatePadding(yMin, yMax);
        double paddedXMin = xMin - xPad;
        double paddedXMax = xMax + xPad;
        double paddedYMin = yMin - yPad;
        double paddedYMax = yMax + yPad;

        if (Double.isInfinite(paddedXMin)) {
            paddedXMin = xMin;
        }
        if (Double.isInfinite(paddedXMax)) {
            paddedXMax = xMax;
        }
        if (Double.isInfinite(paddedYMin)) {
            paddedYMin = yMin;
        }
        if (Double.isInfinite(paddedYMax)) {
            paddedYMax = yMax;
        }

        return new ViewBox(paddedXMin, paddedXMax, paddedYMin, paddedYMax);
    }

    private static double calculatePadding(double min, double max) {
        double span = max - min;
        if (Double.isInfinite(span)) {
            throw new IllegalArgumentException("Диапазон координат слишком велик");
        }
        double baseSpan = Math.max(span, MIN_SPAN);
        double magnitude = Math.max(Math.abs(min), Math.abs(max));
        return Math.max(baseSpan * PADDING_RATIO, Math.ulp(magnitude) * 2);
    }
}
