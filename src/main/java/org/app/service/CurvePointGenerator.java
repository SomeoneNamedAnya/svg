package org.app.service;

import org.app.domain.CurvePoint;
import org.app.domain.Token;
import org.app.exception.MathEvaluationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Строит равномерную сетку точек кривой по ПОЛИЗ выражения
 * Точки, в которых вычисление упало (MathEvaluationException), помечаются как невалидные,
 * это создаёт разрыв линии и не прерывает построение всей кривой
 */
public class CurvePointGenerator {

    private static final double DISCONTINUITY_JUMP_FACTOR = 50;
    private static final double DISCONTINUITY_DEVIATION_FACTOR = 0.5;
    private static final double MIN_DISCONTINUITY_SCALE = 1e-12;

    private CurvePointGenerator() {
    }

    /**
     * Генерирует точки явной функции на равномерной сетке X
     *
     * @param poliz разобранное выражение функции
     * @param xMin нижняя граница X
     * @param xMax верхняя граница X
     * @param pointCount количество точек
     * @return точки кривой, включая маркеры разрывов
     */
    public static List<CurvePoint> generateExplicit(List<Token> poliz, double xMin, double xMax, int pointCount) {
        List<CurvePoint> rawPoints = new ArrayList<>(pointCount);
        double step = (xMax - xMin) / (pointCount - 1);

        for (int i = 0; i < pointCount; i++) {
            double x = xMin + step * i;
            try {
                double y = PolizEvaluator.evaluate(poliz, x);
                rawPoints.add(CurvePoint.valid(x, y));
            } catch (MathEvaluationException e) {
                rawPoints.add(CurvePoint.invalid());
            }
        }

        double scale = calculateScale(rawPoints, false);
        List<CurvePoint> points = new ArrayList<>(pointCount);
        CurvePoint previous = null;

        for (CurvePoint current : rawPoints) {
            if (previous != null && previous.isValid() && current.isValid()
                    && hasDiscontinuity(poliz, previous.getX(), previous.getY(),
                    current.getX(), current.getY(), scale)) {
                points.add(CurvePoint.invalid());
            }
            points.add(current);
            previous = current;
        }

        return points;
    }

    /**
     * Генерирует точки параметрической кривой на равномерной сетке параметра
     *
     * @param fPoliz разобранное выражение координаты X
     * @param gPoliz разобранное выражение координаты Y
     * @param tMin нижняя граница параметра
     * @param tMax верхняя граница параметра
     * @param pointCount количество точек
     * @return точки кривой, включая маркеры разрывов
     */
    public static List<CurvePoint> generateParametric(List<Token> fPoliz, List<Token> gPoliz,
                                                        double tMin, double tMax, int pointCount) {
        List<CurvePoint> rawPoints = new ArrayList<>(pointCount);
        double step = (tMax - tMin) / (pointCount - 1);

        for (int i = 0; i < pointCount; i++) {
            double t = tMin + step * i;
            try {
                double x = PolizEvaluator.evaluate(fPoliz, t);
                double y = PolizEvaluator.evaluate(gPoliz, t);
                rawPoints.add(CurvePoint.valid(x, y));
            } catch (MathEvaluationException e) {
                rawPoints.add(CurvePoint.invalid());
            }
        }

        double xScale = calculateScale(rawPoints, true);
        double yScale = calculateScale(rawPoints, false);
        List<CurvePoint> points = new ArrayList<>(pointCount);
        CurvePoint previous = null;
        double previousT = 0;

        for (int i = 0; i < rawPoints.size(); i++) {
            CurvePoint current = rawPoints.get(i);
            double currentT = tMin + step * i;

            if (previous != null && previous.isValid() && current.isValid()
                    && (hasDiscontinuity(fPoliz, previousT, previous.getX(),
                    currentT, current.getX(), xScale)
                    || hasDiscontinuity(gPoliz, previousT, previous.getY(),
                    currentT, current.getY(), yScale))) {
                points.add(CurvePoint.invalid());
            }
            points.add(current);
            previous = current;
            previousT = currentT;
        }

        return points;
    }

    private static boolean hasDiscontinuity(List<Token> poliz,
                                            double argumentStart, double valueStart,
                                            double argumentEnd, double valueEnd,
                                            double scale) {
        double argumentMiddle = argumentStart + (argumentEnd - argumentStart) * 0.5;
        double valueMiddle;

        try {
            valueMiddle = PolizEvaluator.evaluate(poliz, argumentMiddle);
        } catch (MathEvaluationException e) {
            return true;
        }

        double expectedMiddle = valueStart * 0.5 + valueEnd * 0.5;
        double jump = Math.abs(valueEnd - valueStart);
        double deviation = Math.abs(valueMiddle - expectedMiddle);

        return Math.max(jump, deviation) > DISCONTINUITY_JUMP_FACTOR * scale
                && deviation > DISCONTINUITY_DEVIATION_FACTOR * Math.max(jump, scale);
    }

    private static double calculateScale(List<CurvePoint> points, boolean xCoordinate) {
        List<Double> differences = new ArrayList<>();
        Double previousValue = null;

        for (CurvePoint point : points) {
            if (!point.isValid()) {
                previousValue = null;
                continue;
            }

            double value = xCoordinate ? point.getX() : point.getY();

            if (previousValue != null) {
                double difference = Math.abs(value - previousValue);
                if (Double.isInfinite(difference)) {
                    difference = Double.MAX_VALUE;
                }
                differences.add(difference);
            }
            previousValue = value;
        }

        if (differences.isEmpty()) {
            return MIN_DISCONTINUITY_SCALE;
        }

        Collections.sort(differences);
        int middle = differences.size() / 2;
        double medianDifference = differences.size() % 2 == 0
                ? differences.get(middle - 1) * 0.5 + differences.get(middle) * 0.5
                : differences.get(middle);

        return Math.max(MIN_DISCONTINUITY_SCALE, medianDifference);
    }
}
