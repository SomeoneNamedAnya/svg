package org.app.service;

import org.app.domain.CurvePoint;
import org.app.domain.ViewBox;

import java.util.List;

/**
 * Переводит точки кривой из системы координат данных (ViewBox) в пиксели картинки
 * и строит SVG path data (атрибут d) в постфиксной M/L-записи
 * Эта же d-строка входит в итоговый SVG, который используется для
 * превью на странице и для скачиваемого файла.
 */
public class SvgPathBuilder {

    private SvgPathBuilder() {
    }

    /**
     * Создаёт команды {@code M}/{@code L}; невалидные точки начинают новый фрагмент линии.
     *
     * @param points точки кривой в координатах данных
     * @param viewBox видимая область данных
     * @param canvasWidth ширина SVG
     * @param canvasHeight высота SVG
     * @return значение атрибута {@code d} для SVG элемента {@code path}
     */
    public static String buildPathData(List<CurvePoint> points, ViewBox viewBox, double canvasWidth, double canvasHeight) {
        StringBuilder d = new StringBuilder();
        boolean needMove = true;

        for (CurvePoint point : points) {
            if (!point.isValid()) {
                needMove = true;
                continue;
            }

            double px = CoordinateMapper.toPixelX(point.getX(), viewBox, canvasWidth);
            double py = CoordinateMapper.toPixelY(point.getY(), viewBox, canvasHeight);

            d.append(needMove ? 'M' : 'L');
            needMove = false;

            d.append(format(px)).append(',').append(format(py)).append(' ');
        }

        return d.toString().trim();
    }

    private static String format(double value) {
        return DecimalFormatter.format(value, 2);
    }
}
