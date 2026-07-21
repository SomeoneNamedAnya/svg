package org.app.domain;

import java.util.List;

/**
 * Результат построения графика: набор SVG-команд для элемента path (атрибут d) и 
 * готовый SVG для отображения в браузере и экспорта в файл
 */
public final class PlotResult {

    private final String pathData;
    private final String svgDocument;
    private final ViewBox viewBox;
    private final List<String> warnings;

    /**
     * Создаёт результат успешного построения
     *
     * @param pathData значение атрибута {@code d} линии графика
     * @param svgDocument полный SVG
     * @param viewBox использованная область координат данных
     * @param warnings предупреждения о пропущенных точках
     */
    public PlotResult(String pathData, String svgDocument, ViewBox viewBox, List<String> warnings) {
        this.pathData = pathData;
        this.svgDocument = svgDocument;
        this.viewBox = viewBox;
        this.warnings = warnings;
    }

    /** @return значение SVG атрибута {@code d} линии графика */
    public String getPathData() {
        return pathData;
    }

    /** @return готовый SVG */
    public String getSvgDocument() {
        return svgDocument;
    }

    /** @return область координат, использованная при построении */
    public ViewBox getViewBox() {
        return viewBox;
    }

    /** @return предупреждения, сформированные во время вычисления точек */
    public List<String> getWarnings() {
        return warnings;
    }
}
