package org.app.service;

import org.app.domain.AxesLayout;
import org.app.domain.CurvePoint;
import org.app.domain.FunctionMode;
import org.app.domain.PlotConfig;
import org.app.domain.PlotResult;
import org.app.domain.Token;
import org.app.domain.ViewBox;
import org.app.exception.MathEvaluationException;

import java.util.List;

/**
 * Построение готового графика:
 * ПОЛИЗ (через ExpressionParser) -> точки кривой -> область отображения -> SVG
 */
public class GraphBuilder {

    /** Ширина SVG картинки в пикселях */
    public static final double CANVAS_WIDTH = 600;

    /** Высота SVG картинки в пикселях */
    public static final double CANVAS_HEIGHT = 400;

    private GraphBuilder() {
    }

    /**
     * Выполняет полный цикл построения графика по конфигурации
     *
     * @param config проверенная конфигурация построения
     * @return данные линии, SVG, область отображения и предупреждения
     * @throws org.app.exception.ExpressionException при синтаксической/семантической ошибке выражения
     * @throws MathEvaluationException если функция не определена ни в одной точке интервала
     */
    public static PlotResult build(PlotConfig config) {
        List<CurvePoint> points = computePoints(config);

        long invalidCount = 0;
        for (CurvePoint point : points) {
            if (!point.isValid()) {
                invalidCount++;
            }
        }
        if (invalidCount == points.size()) {
            throw new MathEvaluationException(
                    "Функция не определена ни в одной точке заданного интервала", Double.NaN);
        }

        ViewBox viewBox = config.isAutoScale()
                ? ViewBoxCalculator.calculate(points)
                : config.getManualViewBox();

        String pathData = SvgPathBuilder.buildPathData(points, viewBox, CANVAS_WIDTH, CANVAS_HEIGHT);
        AxesLayout axesLayout = AxesBuilder.build(viewBox, CANVAS_WIDTH, CANVAS_HEIGHT);
        String svgDocument = SvgDocumentBuilder.build(pathData, axesLayout, CANVAS_WIDTH, CANVAS_HEIGHT,
                config.getColorHex(), config.getStrokeWidth(), config.getLineStyle(),
                config.isShowAxes(), config.isShowGrid(), config.getAxisColorHex(), config.getAxisStrokeWidth());

        List<String> warnings = invalidCount == 0
                ? List.of()
                : List.of("Пропущено точек: " + invalidCount + " (функция не определена)");

        return new PlotResult(pathData, svgDocument, viewBox, warnings);
    }

    /**
     * Вычисляет точки кривой по конфигурации
     *
     * @param config проверенная конфигурация построения
     * @return точки кривой
     */
    private static List<CurvePoint> computePoints(PlotConfig config) {
        if (config.getMode() == FunctionMode.EXPLICIT) {
            List<Token> poliz = ExpressionParser.parseExplicit(config.getFExpression());
            return CurvePointGenerator.generateExplicit(
                    poliz, config.getRangeMin(), config.getRangeMax(), config.getPointCount());
        }

        ExpressionParser.ParametricExpressions parsed =
                ExpressionParser.parseParametric(config.getFExpression(), config.getGExpression());
        return CurvePointGenerator.generateParametric(
                parsed.getXPoliz(), parsed.getYPoliz(),
                config.getRangeMin(), config.getRangeMax(), config.getPointCount());
    }
}
