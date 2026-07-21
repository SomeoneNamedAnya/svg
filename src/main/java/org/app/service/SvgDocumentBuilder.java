package org.app.service;

import org.app.domain.AxesLayout;
import org.app.domain.GridLine;
import org.app.domain.LineStyle;
import org.app.domain.TickLabel;

/**
 * Оборачивает готовую d-строку (от SvgPathBuilder) в SVG,
 * который рендерится прямо на странице и скачивается как .svg файл
 * порядок отрисовки: сетка -> оси -> подписи -> кривая (всегда видна поверх)
 */
public class SvgDocumentBuilder {

    private static final String GRID_COLOR = "#dddddd";
    private static final double GRID_STROKE_WIDTH = 1;
    private static final double LABEL_MARGIN = 4;

    private SvgDocumentBuilder() {
    }

    /**
     * Собирает полный SVG из геометрии графика и визуальных настроек.
     *
     * @param pathData команды линии графика
     * @param axesLayout рассчитанные линии сетки, осей и подписи
     * @param canvasWidth ширина SVG картинки
     * @param canvasHeight высота SVG картинки
     * @param colorHex цвет линии графика
     * @param strokeWidth толщина линии графика
     * @param lineStyle стиль линии графика
     * @param showAxes признак отображения осей и подписей
     * @param showGrid признак отображения сетки
     * @param axisColorHex цвет осей и подписей
     * @param axisStrokeWidth толщина осей
     * @return самостоятельный SVG
     */
    public static String build(String pathData, AxesLayout axesLayout, double canvasWidth, double canvasHeight,
                                String colorHex, double strokeWidth, LineStyle lineStyle,
                                boolean showAxes, boolean showGrid, String axisColorHex, double axisStrokeWidth) {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 ")
                .append(format(canvasWidth)).append(' ').append(format(canvasHeight))
                .append("\" width=\"").append(format(canvasWidth))
                .append("\" height=\"").append(format(canvasHeight)).append("\">\n");

        if (showGrid) {
            for (GridLine line : axesLayout.getGridLines()) {
                appendLine(svg, line, GRID_COLOR, GRID_STROKE_WIDTH);
            }
        }

        if (showAxes) {
            for (GridLine line : axesLayout.getAxisLines()) {
                appendLine(svg, line, axisColorHex, axisStrokeWidth);
            }
            for (TickLabel label : axesLayout.getTickLabels()) {
                appendLabel(svg, label, axisColorHex, canvasWidth, canvasHeight);
            }
        }

        String dashArray = lineStyle.toSvgDashArray();
        svg.append("  <path d=\"").append(pathData)
                .append("\" fill=\"none\" stroke=\"").append(colorHex)
                .append("\" stroke-width=\"").append(format(strokeWidth)).append('"');
        if (dashArray != null) {
            svg.append(" stroke-dasharray=\"").append(dashArray).append('"');
        }
        svg.append("/>\n");

        svg.append("</svg>");
        return svg.toString();
    }

    private static void appendLine(StringBuilder svg, GridLine line, String color, double width) {
        svg.append("  <line x1=\"").append(format(line.getX1()))
                .append("\" y1=\"").append(format(line.getY1()))
                .append("\" x2=\"").append(format(line.getX2()))
                .append("\" y2=\"").append(format(line.getY2()))
                .append("\" stroke=\"").append(color)
                .append("\" stroke-width=\"").append(format(width))
                .append("\"/>\n");
    }

    private static void appendLabel(StringBuilder svg, TickLabel label, String color,
                                    double canvasWidth, double canvasHeight) {
        String anchor = label.getX() <= LABEL_MARGIN ? "start"
                : label.getX() >= canvasWidth - LABEL_MARGIN ? "end" : "middle";
        String baseline = label.getY() <= LABEL_MARGIN ? "hanging"
                : label.getY() >= canvasHeight - LABEL_MARGIN ? "text-after-edge" : "middle";
        svg.append("  <text x=\"").append(format(label.getX()))
                .append("\" y=\"").append(format(label.getY()))
                .append("\" font-size=\"10\" fill=\"").append(color)
                .append("\" text-anchor=\"").append(anchor)
                .append("\" dominant-baseline=\"").append(baseline)
                .append("\">").append(label.getText())
                .append("</text>\n");
    }

    private static String format(double value) {
        return DecimalFormatter.format(value, 2);
    }
}
