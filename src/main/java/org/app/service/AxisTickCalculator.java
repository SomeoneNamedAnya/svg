package org.app.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Вычисляет красивые позиции меток шкалы для диапазона [min, max] с примерно ожидаемым числом меток
 */
public class AxisTickCalculator {

    private AxisTickCalculator() {
    }

    /**
     * Подбирает удобный шаг и позиции меток внутри диапазона
     *
     * @param min нижняя граница диапазона
     * @param max верхняя граница диапазона
     * @param desiredCount желаемое приблизительное количество меток
     * @return набор позиций и выбранный шаг
     */
    public static TickSet calculateTicks(double min, double max, int desiredCount) {
        double rawStep = (max - min) / desiredCount;
        double magnitude = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double residual = rawStep / magnitude;

        double niceStep;
        if (residual > 5) {
            niceStep = 10 * magnitude;
        } else if (residual > 2) {
            niceStep = 5 * magnitude;
        } else if (residual > 1) {
            niceStep = 2 * magnitude;
        } else {
            niceStep = magnitude;
        }

        double start = Math.ceil(min / niceStep) * niceStep;
        int count = (int) Math.floor((max - start) / niceStep + 1e-9) + 1;

        List<Double> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double value = start + i * niceStep;
            if (value >= min - niceStep * 1e-9 && value <= max + niceStep * 1e-9) {
                values.add(value);
            }
        }

        return new TickSet(values, niceStep);
    }

    /** Набор меток шкалы вместе с выбранным шагом (нужен для точного форматирования подписи) */
    public static final class TickSet {

        private final List<Double> values;
        private final double step;

        /**
         * @param values позиции меток в координатах данных
         * @param step расстояние между соседними метками
         */
        public TickSet(List<Double> values, double step) {
            this.values = values;
            this.step = step;
        }

        /** @return позиции меток в координатах данных */
        public List<Double> getValues() {
            return values;
        }

        /** @return расстояние между соседними метками */
        public double getStep() {
            return step;
        }
    }
}
