package org.app.service;

import org.app.domain.AxesLayout;
import org.app.domain.GridLine;
import org.app.domain.TickLabel;
import org.app.domain.ViewBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Считает геометрию сетки, осей и подписей-меток в пикселях для SVG (SvgDocumentBuilder),
 * который идёт и в превью на странице, и в файл. Подпись оси X размещаем у нижнего края картинки, 
 * подпись оси Y размещаем у левого края картинки
 */
public class AxesBuilder {

    private static final int DESIRED_TICKS_X = 8;
    private static final int DESIRED_TICKS_Y = 6;
    private static final double LABEL_MARGIN = 4;

    private AxesBuilder() {
    }

    /**
     * Рассчитывает линии сетки, оси и подписи для заданной области
     *
     * @param viewBox видимая область в координатах данных
     * @param canvasWidth ширина SVG картинки
     * @param canvasHeight высота SVG картинки
     * @return готовая координатная система
     */
    public static AxesLayout build(ViewBox viewBox, double canvasWidth, double canvasHeight) {
        AxisTickCalculator.TickSet xTicks =
                AxisTickCalculator.calculateTicks(viewBox.getXMin(), viewBox.getXMax(), DESIRED_TICKS_X);
        AxisTickCalculator.TickSet yTicks =
                AxisTickCalculator.calculateTicks(viewBox.getYMin(), viewBox.getYMax(), DESIRED_TICKS_Y);

        List<GridLine> gridLines = new ArrayList<>();
        List<TickLabel> tickLabels = new ArrayList<>();

        for (double xTick : xTicks.getValues()) {
            double px = CoordinateMapper.toPixelX(xTick, viewBox, canvasWidth);
            gridLines.add(new GridLine(px, 0, px, canvasHeight));
            tickLabels.add(new TickLabel(formatTick(xTick, xTicks.getStep()), px, canvasHeight - LABEL_MARGIN));
        }
        for (double yTick : yTicks.getValues()) {
            double py = CoordinateMapper.toPixelY(yTick, viewBox, canvasHeight);
            gridLines.add(new GridLine(0, py, canvasWidth, py));
            tickLabels.add(new TickLabel(formatTick(yTick, yTicks.getStep()), LABEL_MARGIN, py));
        }

        List<GridLine> axisLines = new ArrayList<>();
        if (viewBox.getYMin() <= 0 && viewBox.getYMax() >= 0) {
            double py = CoordinateMapper.toPixelY(0, viewBox, canvasHeight);
            axisLines.add(new GridLine(0, py, canvasWidth, py));
        }
        if (viewBox.getXMin() <= 0 && viewBox.getXMax() >= 0) {
            double px = CoordinateMapper.toPixelX(0, viewBox, canvasWidth);
            axisLines.add(new GridLine(px, 0, px, canvasHeight));
        }

        return new AxesLayout(gridLines, axisLines, tickLabels);
    }
    /**
    * Форматирует числовое значение для подписи деления оси
    * @param value значение деления
    * @param step шаг делений
    */
    private static String formatTick(double value, double step) {
        if (Math.abs(value - Math.round(value)) < step * 1e-6) {
            return String.valueOf(Math.round(value));
        }
        int decimals = Math.max(0, -(int) Math.floor(Math.log10(step)));
        return DecimalFormatter.format(value, decimals);
    }
}
