package org.app.web;

import org.app.domain.FunctionMode;
import org.app.domain.LineStyle;
import org.app.domain.PlotConfig;
import org.app.domain.PlotResult;
import org.app.domain.ViewBox;
import org.app.exception.ExpressionException;
import org.app.exception.MathEvaluationException;
import org.app.service.ExpressionParser;
import org.app.service.GraphBuilder;
import org.app.service.PlotConfigCodec;
import org.app.service.PlotHistory;
import org.app.service.SvgFileExporter;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.dom.html.HTMLOptionElement;
import org.teavm.jso.dom.html.HTMLSelectElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Точка входа для TeaVM (компилируется в JS). Связывает разметку index.html с бизнес-логикой:
 * GraphBuilder, ExpressionParser, лексером, синтаксическим и семантическим анализом,
 * вычислителем ПОЛИЗ и построением SVG
 */
public class GraphWebApp {

    private static final PlotHistory history = new PlotHistory();
    private static HTMLDocument document;

    private static HTMLSelectElement historySelect;
    private static HTMLSelectElement modeSelect;

    private static HTMLElement fLabel;
    private static HTMLInputElement fField;
    private static HTMLElement fError;

    private static HTMLElement gLabel;
    private static HTMLInputElement gField;
    private static HTMLElement gError;

    private static HTMLElement rangeMinLabel;
    private static HTMLInputElement rangeMinField;
    private static HTMLElement rangeMinError;
    private static HTMLElement rangeMaxLabel;
    private static HTMLInputElement rangeMaxField;
    private static HTMLElement rangeMaxError;

    private static HTMLInputElement pointCountField;

    private static HTMLInputElement colorField;
    private static HTMLInputElement strokeWidthField;
    private static HTMLElement strokeWidthValue;
    private static HTMLSelectElement lineStyleSelect;

    private static HTMLInputElement autoScaleCheck;
    private static HTMLElement viewBoxFields;
    private static HTMLInputElement vbXMinField;
    private static HTMLInputElement vbXMaxField;
    private static HTMLInputElement vbYMinField;
    private static HTMLInputElement vbYMaxField;

    private static HTMLInputElement showAxesCheck;
    private static HTMLInputElement showGridCheck;
    private static HTMLInputElement axisColorField;
    private static HTMLInputElement axisWidthField;
    private static HTMLElement axisWidthValue;

    private static HTMLButtonElement buildButton;
    private static HTMLElement hint;
    private static HTMLButtonElement downloadButton;

    private static HTMLElement preview;
    private static HTMLElement warnings;

    private static PlotResult lastResult;

    /**
     * Находит элементы страницы, подключает обработчики и устанавливает начальное состояние формы
     *
     * @param args аргументы запуска; веб-приложением не используются
     */
    public static void main(String[] args) {
        document = Window.current().getDocument();

        historySelect = (HTMLSelectElement) document.getElementById("historySelect");
        modeSelect = (HTMLSelectElement) document.getElementById("modeSelect");

        fLabel = document.getElementById("fLabel");
        fField = (HTMLInputElement) document.getElementById("fField");
        fError = document.getElementById("fError");

        gLabel = document.getElementById("gLabel");
        gField = (HTMLInputElement) document.getElementById("gField");
        gError = document.getElementById("gError");

        rangeMinLabel = document.getElementById("rangeMinLabel");
        rangeMinField = (HTMLInputElement) document.getElementById("rangeMinField");
        rangeMinError = document.getElementById("rangeMinError");
        rangeMaxLabel = document.getElementById("rangeMaxLabel");
        rangeMaxField = (HTMLInputElement) document.getElementById("rangeMaxField");
        rangeMaxError = document.getElementById("rangeMaxError");

        pointCountField = (HTMLInputElement) document.getElementById("pointCountField");

        colorField = (HTMLInputElement) document.getElementById("colorField");
        strokeWidthField = (HTMLInputElement) document.getElementById("strokeWidthField");
        strokeWidthValue = document.getElementById("strokeWidthValue");
        lineStyleSelect = (HTMLSelectElement) document.getElementById("lineStyleSelect");

        autoScaleCheck = (HTMLInputElement) document.getElementById("autoScaleCheck");
        viewBoxFields = document.getElementById("viewBoxFields");
        vbXMinField = (HTMLInputElement) document.getElementById("vbXMinField");
        vbXMaxField = (HTMLInputElement) document.getElementById("vbXMaxField");
        vbYMinField = (HTMLInputElement) document.getElementById("vbYMinField");
        vbYMaxField = (HTMLInputElement) document.getElementById("vbYMaxField");

        showAxesCheck = (HTMLInputElement) document.getElementById("showAxesCheck");
        showGridCheck = (HTMLInputElement) document.getElementById("showGridCheck");
        axisColorField = (HTMLInputElement) document.getElementById("axisColorField");
        axisWidthField = (HTMLInputElement) document.getElementById("axisWidthField");
        axisWidthValue = document.getElementById("axisWidthValue");

        buildButton = (HTMLButtonElement) document.getElementById("buildButton");
        hint = document.getElementById("hint");
        downloadButton = (HTMLButtonElement) document.getElementById("downloadButton");

        preview = document.getElementById("preview");
        warnings = document.getElementById("warnings");

        wireEvents();
        refreshHistorySelect();
        refreshFieldsForMode();
        updateBuildButtonState();
    }

    private static void wireEvents() {
        modeSelect.addEventListener("change", e -> {
            refreshFieldsForMode();
            updateBuildButtonState();
        });

        fField.addEventListener("input", e -> updateBuildButtonState());
        gField.addEventListener("input", e -> updateBuildButtonState());
        rangeMinField.addEventListener("input", e -> updateBuildButtonState());
        rangeMaxField.addEventListener("input", e -> updateBuildButtonState());
        pointCountField.addEventListener("input", e -> updateBuildButtonState());
        vbXMinField.addEventListener("input", e -> updateBuildButtonState());
        vbXMaxField.addEventListener("input", e -> updateBuildButtonState());
        vbYMinField.addEventListener("input", e -> updateBuildButtonState());
        vbYMaxField.addEventListener("input", e -> updateBuildButtonState());

        autoScaleCheck.addEventListener("change", e -> {
            setViewBoxFieldsDisabled(autoScaleCheck.isChecked());
            updateBuildButtonState();
        });

        strokeWidthField.addEventListener("input", e -> strokeWidthValue.setTextContent(strokeWidthField.getValue()));
        axisWidthField.addEventListener("input", e -> axisWidthValue.setTextContent(axisWidthField.getValue()));

        buildButton.addEventListener("click", e -> onBuildClicked());
        downloadButton.addEventListener("click", e -> onDownloadClicked());

        historySelect.addEventListener("change", e -> {
            String value = historySelect.getValue();
            if (value != null && !value.isEmpty()) {
                try {
                    applyConfig(PlotConfigCodec.decode(value));
                } catch (RuntimeException ex) {
                    // повреждённая запись истории — молча игнорируем выбор
                }
            }
        });
    }

    private static FunctionMode currentMode() {
        return FunctionMode.valueOf(modeSelect.getValue());
    }

    private static void refreshFieldsForMode() {
        boolean parametric = currentMode() == FunctionMode.PARAMETRIC;

        setVisible(gLabel, parametric);
        setVisible(gField, parametric);
        setVisible(gError, parametric);

        if (parametric) {
            fLabel.setTextContent("x = f(t)");
            gLabel.setTextContent("y = g(t)");
            rangeMinLabel.setTextContent("t_min");
            rangeMaxLabel.setTextContent("t_max");
            rangeMinField.setValue("0");
            rangeMaxField.setValue(String.valueOf(2 * Math.PI));
        } else {
            fLabel.setTextContent("f(x) =");
            rangeMinLabel.setTextContent("x_min");
            rangeMaxLabel.setTextContent("x_max");
            rangeMinField.setValue("-10");
            rangeMaxField.setValue("10");
        }
    }

    private static void updateBuildButtonState() {
        List<String> problems = new ArrayList<>();
        FunctionMode mode = currentMode();

        if (fField.getValue().trim().isEmpty()) {
            problems.add("введите выражение " + (mode == FunctionMode.PARAMETRIC ? "f(t)" : "f(x)"));
        }
        if (mode == FunctionMode.PARAMETRIC && gField.getValue().trim().isEmpty()) {
            problems.add("введите выражение g(t)");
        }

        Double rangeMin = evaluateConstantOrNull(rangeMinField.getValue());
        Double rangeMax = evaluateConstantOrNull(rangeMaxField.getValue());
        if (rangeMin == null || rangeMax == null) {
            problems.add("диапазон должен быть корректным числовым выражением без переменной");
        } else if (!hasValidSpan(rangeMin, rangeMax)) {
            problems.add("минимум диапазона должен быть меньше максимума, а диапазон — конечным");
        }

        Integer pointCount = parseIntOrNull(pointCountField.getValue());
        if (pointCount == null || pointCount < 100 || pointCount > 5000) {
            problems.add("число точек должно быть целым от 100 до 5000");
        }

        if (!autoScaleCheck.isChecked()) {
            Double vbXMin = parseDoubleOrNull(vbXMinField.getValue());
            Double vbXMax = parseDoubleOrNull(vbXMaxField.getValue());
            Double vbYMin = parseDoubleOrNull(vbYMinField.getValue());
            Double vbYMax = parseDoubleOrNull(vbYMaxField.getValue());
            if (vbXMin == null || vbXMax == null || vbYMin == null || vbYMax == null) {
                problems.add("границы области построения должны быть числами");
            } else if (!hasValidSpan(vbXMin, vbXMax) || !hasValidSpan(vbYMin, vbYMax)) {
                problems.add("границы области построения некорректны (min должен быть меньше max)");
            }
        }

        boolean valid = problems.isEmpty();
        buildButton.setDisabled(!valid);
        hint.setTextContent(valid ? "" : problems.get(0));
    }

    private static void onBuildClicked() {
        clearFieldError(fField, fError);
        clearFieldError(gField, gError);
        clearFieldError(rangeMinField, rangeMinError);
        clearFieldError(rangeMaxField, rangeMaxError);
        warnings.setTextContent("");

        FunctionMode mode = currentMode();
        String fText = fField.getValue().trim();
        String gText = mode == FunctionMode.PARAMETRIC ? gField.getValue().trim() : null;

        try {
            ExpressionParser.parseExplicit(fText);
        } catch (ExpressionException e) {
            showFieldError(fField, fError, e.getMessage());
            return;
        }
        if (mode == FunctionMode.PARAMETRIC) {
            try {
                ExpressionParser.parseExplicit(gText);
            } catch (ExpressionException e) {
                showFieldError(gField, gError, e.getMessage());
                return;
            }
        }

        double rangeMin;
        double rangeMax;
        try {
            rangeMin = ExpressionParser.evaluateConstant(rangeMinField.getValue().trim());
        } catch (ExpressionException | MathEvaluationException e) {
            showFieldError(rangeMinField, rangeMinError, e.getMessage());
            return;
        }
        try {
            rangeMax = ExpressionParser.evaluateConstant(rangeMaxField.getValue().trim());
        } catch (ExpressionException | MathEvaluationException e) {
            showFieldError(rangeMaxField, rangeMaxError, e.getMessage());
            return;
        }
        if (!hasValidSpan(rangeMin, rangeMax)) {
            showFieldError(rangeMinField, rangeMinError, "Некорректный или слишком большой диапазон");
            showFieldError(rangeMaxField, rangeMaxError, "Некорректный или слишком большой диапазон");
            return;
        }

        int pointCount = Integer.parseInt(pointCountField.getValue().trim());

        boolean autoScale = autoScaleCheck.isChecked();
        ViewBox manualViewBox = null;
        if (!autoScale) {
            Double xMin = parseDoubleOrNull(vbXMinField.getValue());
            Double xMax = parseDoubleOrNull(vbXMaxField.getValue());
            Double yMin = parseDoubleOrNull(vbYMinField.getValue());
            Double yMax = parseDoubleOrNull(vbYMaxField.getValue());
            if (xMin == null || xMax == null || yMin == null || yMax == null
                    || !hasValidSpan(xMin, xMax) || !hasValidSpan(yMin, yMax)) {
                hint.setTextContent("Границы области построения должны быть конечными числами");
                return;
            }
            try {
                manualViewBox = new ViewBox(xMin, xMax, yMin, yMax);
            } catch (IllegalArgumentException e) {
                hint.setTextContent(e.getMessage());
                return;
            }
        }

        String colorHex = colorField.getValue();
        double strokeWidth = Double.parseDouble(strokeWidthField.getValue());
        LineStyle lineStyle = LineStyle.valueOf(lineStyleSelect.getValue());

        PlotConfig.Builder configBuilder = mode == FunctionMode.EXPLICIT
                ? PlotConfig.explicit(fText, rangeMin, rangeMax)
                : PlotConfig.parametric(fText, gText, rangeMin, rangeMax);

        PlotConfig config = configBuilder
                .pointCount(pointCount)
                .colorHex(colorHex)
                .strokeWidth(strokeWidth)
                .lineStyle(lineStyle)
                .autoScale(autoScale)
                .manualViewBox(manualViewBox)
                .showAxes(showAxesCheck.isChecked())
                .showGrid(showGridCheck.isChecked())
                .axisColorHex(axisColorField.getValue())
                .axisStrokeWidth(Double.parseDouble(axisWidthField.getValue()))
                .build();

        try {
            PlotResult result = GraphBuilder.build(config);
            lastResult = result;

            preview.setInnerHTML(result.getSvgDocument());
            downloadButton.setDisabled(false);

            if (!result.getWarnings().isEmpty()) {
                warnings.setTextContent(String.join("; ", result.getWarnings()));
            }

            history.add(config);
            refreshHistorySelect();
            historySelect.setValue("");
        } catch (ExpressionException e) {
            // сюда попадает только семантическая ошибка на стыке двух выражений (например, разные переменные)
            showFieldError(fField, fError, e.getMessage());
            if (mode == FunctionMode.PARAMETRIC) {
                showFieldError(gField, gError, e.getMessage());
            }
        } catch (MathEvaluationException | IllegalArgumentException e) {
            lastResult = null;
            preview.setInnerHTML("");
            downloadButton.setDisabled(true);
            warnings.setTextContent(e.getMessage());
        }
    }

    private static void onDownloadClicked() {
        if (lastResult == null) {
            return;
        }
        SvgFileExporter.export(lastResult.getSvgDocument(), SvgFileExporter.generateFileName());
    }

    /** Заполняет все поля формы значениями из сохранённой конфигурации (выбор из истории). */
    private static void applyConfig(PlotConfig config) {
        modeSelect.setValue(config.getMode().name());
        refreshFieldsForMode();

        fField.setValue(config.getFExpression());
        gField.setValue(config.getGExpression() == null ? "" : config.getGExpression());
        rangeMinField.setValue(formatNumber(config.getRangeMin()));
        rangeMaxField.setValue(formatNumber(config.getRangeMax()));
        pointCountField.setValue(String.valueOf(config.getPointCount()));
        colorField.setValue(config.getColorHex());
        strokeWidthField.setValue(String.valueOf(config.getStrokeWidth()));
        strokeWidthValue.setTextContent(String.valueOf(config.getStrokeWidth()));
        lineStyleSelect.setValue(config.getLineStyle().name());
        autoScaleCheck.setChecked(config.isAutoScale());
        setViewBoxFieldsDisabled(config.isAutoScale());

        ViewBox manualViewBox = config.getManualViewBox();
        vbXMinField.setValue(manualViewBox == null ? "" : formatNumber(manualViewBox.getXMin()));
        vbXMaxField.setValue(manualViewBox == null ? "" : formatNumber(manualViewBox.getXMax()));
        vbYMinField.setValue(manualViewBox == null ? "" : formatNumber(manualViewBox.getYMin()));
        vbYMaxField.setValue(manualViewBox == null ? "" : formatNumber(manualViewBox.getYMax()));

        showAxesCheck.setChecked(config.isShowAxes());
        showGridCheck.setChecked(config.isShowGrid());
        axisColorField.setValue(config.getAxisColorHex());
        axisWidthField.setValue(String.valueOf(config.getAxisStrokeWidth()));
        axisWidthValue.setTextContent(String.valueOf(config.getAxisStrokeWidth()));

        updateBuildButtonState();
    }

    private static void refreshHistorySelect() {
        historySelect.setInnerHTML("<option value=\"\">История (последние 5)</option>");

        for (PlotConfig config : history.loadAll()) {
            HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
            option.setValue(PlotConfigCodec.encode(config));
            option.setText(PlotHistory.describe(config));
            historySelect.appendChild(option);
        }
    }

    private static void setViewBoxFieldsDisabled(boolean disabled) {
        if (disabled) {
            viewBoxFields.setAttribute("disabled", "disabled");
        } else {
            viewBoxFields.removeAttribute("disabled");
        }
    }

    private static void setVisible(HTMLElement element, boolean visible) {
        element.getStyle().setProperty("display", visible ? "" : "none");
    }

    private static void showFieldError(HTMLInputElement field, HTMLElement errorLabel, String message) {
        field.getClassList().add("field-error");
        errorLabel.setTextContent(message);
    }

    private static void clearFieldError(HTMLInputElement field, HTMLElement errorLabel) {
        field.getClassList().remove("field-error");
        errorLabel.setTextContent("");
    }

    private static String formatNumber(double value) {
        if (Math.abs(value - Math.round(value)) < 1e-9) {
            return String.valueOf(Math.round(value));
        }
        return String.valueOf(value);
    }

    private static Double evaluateConstantOrNull(String text) {
        try {
            return ExpressionParser.evaluateConstant(text.trim());
        } catch (ExpressionException | MathEvaluationException e) {
            return null;
        }
    }

    private static Double parseDoubleOrNull(String text) {
        try {
            double value = Double.parseDouble(text.trim());
            return Double.isNaN(value) || Double.isInfinite(value) ? null : value;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Integer parseIntOrNull(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static boolean hasValidSpan(double min, double max) {
        double span = max - min;
        return min < max && !Double.isNaN(span) && !Double.isInfinite(span);
    }
}
