package org.app.service;

import org.app.domain.FunctionMode;
import org.app.domain.LineStyle;
import org.app.domain.PlotConfig;
import org.app.domain.ViewBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Кодирует/раскодирует PlotConfig в одну строку (формат хранения записи истории)
 * Разделительем является символ табуляции
 */
public class PlotConfigCodec {

    private static final String SEPARATOR = "\t";
    private static final int FIELD_COUNT = 18;

    private PlotConfigCodec() {
    }

    /**
     * @param config конфигурация построения
     * @return строковое представление для сохранения в браузерной истории
     */
    public static String encode(PlotConfig config) {
        ViewBox vb = config.getManualViewBox();

        String[] parts = {
                config.getMode().name(),
                config.getFExpression(),
                config.getGExpression() == null ? "" : config.getGExpression(),
                Double.toString(config.getRangeMin()),
                Double.toString(config.getRangeMax()),
                Integer.toString(config.getPointCount()),
                config.getColorHex(),
                Double.toString(config.getStrokeWidth()),
                config.getLineStyle().name(),
                Boolean.toString(config.isAutoScale()),
                vb == null ? "" : Double.toString(vb.getXMin()),
                vb == null ? "" : Double.toString(vb.getXMax()),
                vb == null ? "" : Double.toString(vb.getYMin()),
                vb == null ? "" : Double.toString(vb.getYMax()),
                Boolean.toString(config.isShowAxes()),
                Boolean.toString(config.isShowGrid()),
                config.getAxisColorHex(),
                Double.toString(config.getAxisStrokeWidth())
        };

        return String.join(SEPARATOR, parts);
    }

    /**
     * @param encoded строковое представление конфигурации
     * @return восстановленная конфигурация
     * @throws RuntimeException если строка повреждена или имеет неожиданный формат
     */
    public static PlotConfig decode(String encoded) {
        String[] parts = splitByTab(encoded);
        if (parts.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Некорректный формат записи истории: " + encoded);
        }

        FunctionMode mode = FunctionMode.valueOf(parts[0]);
        String fExpression = parts[1];
        String gExpression = parts[2].isEmpty() ? null : parts[2];
        double rangeMin = Double.parseDouble(parts[3]);
        double rangeMax = Double.parseDouble(parts[4]);
        int pointCount = Integer.parseInt(parts[5]);
        String colorHex = parts[6];
        double strokeWidth = Double.parseDouble(parts[7]);
        LineStyle lineStyle = LineStyle.valueOf(parts[8]);
        boolean autoScale = Boolean.parseBoolean(parts[9]);

        ViewBox manualViewBox = parts[10].isEmpty() ? null : new ViewBox(
                Double.parseDouble(parts[10]), Double.parseDouble(parts[11]),
                Double.parseDouble(parts[12]), Double.parseDouble(parts[13]));

        boolean showAxes = Boolean.parseBoolean(parts[14]);
        boolean showGrid = Boolean.parseBoolean(parts[15]);
        String axisColorHex = parts[16];
        double axisStrokeWidth = Double.parseDouble(parts[17]);

        PlotConfig.Builder builder = mode == FunctionMode.EXPLICIT
                ? PlotConfig.explicit(fExpression, rangeMin, rangeMax)
                : PlotConfig.parametric(fExpression, gExpression, rangeMin, rangeMax);

        return builder
                .pointCount(pointCount)
                .colorHex(colorHex)
                .strokeWidth(strokeWidth)
                .lineStyle(lineStyle)
                .autoScale(autoScale)
                .manualViewBox(manualViewBox)
                .showAxes(showAxes)
                .showGrid(showGrid)
                .axisColorHex(axisColorHex)
                .axisStrokeWidth(axisStrokeWidth)
                .build();
    }

    /** Ручное разбиение строки по разделителю */
    private static String[] splitByTab(String s) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\t') {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(s.substring(start));
        return parts.toArray(new String[0]);
    }
}
