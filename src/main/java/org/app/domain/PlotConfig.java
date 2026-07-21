package org.app.domain;

/**
 * Настройки одного построения графика в одном объекте
 * Паттерн - Builder
 */
public final class PlotConfig {

    private final FunctionMode mode;
    private final String fExpression;
    private final String gExpression;
    private final double rangeMin;
    private final double rangeMax;
    private final int pointCount;
    private final String colorHex;
    private final double strokeWidth;
    private final LineStyle lineStyle;
    private final boolean autoScale;
    private final ViewBox manualViewBox;
    private final boolean showAxes;
    private final boolean showGrid;
    private final String axisColorHex;
    private final double axisStrokeWidth;

    private PlotConfig(Builder b) {
        this.mode = b.mode;
        this.fExpression = b.fExpression;
        this.gExpression = b.gExpression;
        this.rangeMin = b.rangeMin;
        this.rangeMax = b.rangeMax;
        this.pointCount = b.pointCount;
        this.colorHex = b.colorHex;
        this.strokeWidth = b.strokeWidth;
        this.lineStyle = b.lineStyle;
        this.autoScale = b.autoScale;
        this.manualViewBox = b.manualViewBox;
        this.showAxes = b.showAxes;
        this.showGrid = b.showGrid;
        this.axisColorHex = b.axisColorHex;
        this.axisStrokeWidth = b.axisStrokeWidth;
    }

    /**
     * Создаёт билдер конфигурации явной функции {@code y = f(x)}
     *
     * @param fExpression выражение функции
     * @param xMin нижняя граница X
     * @param xMax верхняя граница X
     * @return билдер с настройками по умолчанию
     */
    public static Builder explicit(String fExpression, double xMin, double xMax) {
        return new Builder(FunctionMode.EXPLICIT, fExpression, null, xMin, xMax);
    }

    /**
     * Создаёт билдер конфигурации параметрической системы.
     *
     * @param fOfT выражение координаты X
     * @param gOfT выражение координаты Y
     * @param tMin нижняя граница параметра
     * @param tMax верхняя граница параметра
     * @return билдер с настройками по умолчанию
     */
    public static Builder parametric(String fOfT, String gOfT, double tMin, double tMax) {
        return new Builder(FunctionMode.PARAMETRIC, fOfT, gOfT, tMin, tMax);
    }

    /** @return способ задания функции */
    public FunctionMode getMode() {
        return mode;
    }

    /** @return выражение {@code f(x)} или {@code f(t)} */
    public String getFExpression() {
        return fExpression;
    }

    /** @return выражение {@code g(t)} или {@code null} для явной функции */
    public String getGExpression() {
        return gExpression;
    }

    /** @return нижняя граница диапазона независимой переменной */
    public double getRangeMin() {
        return rangeMin;
    }

    /** @return верхняя граница диапазона независимой переменной */
    public double getRangeMax() {
        return rangeMax;
    }

    /** @return количество вычисляемых точек кривой */
    public int getPointCount() {
        return pointCount;
    }

    /** @return цвет линии графика в HEX-формате */
    public String getColorHex() {
        return colorHex;
    }

    /** @return толщина линии графика в пикселях */
    public double getStrokeWidth() {
        return strokeWidth;
    }

    /** @return стиль линии графика */
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /** @return {@code true}, если область отображения вычисляется автоматически */
    public boolean isAutoScale() {
        return autoScale;
    }

    /** @return ручная область отображения или {@code null} при автоматическом масштабе */
    public ViewBox getManualViewBox() {
        return manualViewBox;
    }

    /** @return {@code true}, если координатные оси следует добавить в SVG */
    public boolean isShowAxes() {
        return showAxes;
    }

    /** @return {@code true}, если координатную сетку следует добавить в SVG */
    public boolean isShowGrid() {
        return showGrid;
    }

    /** @return цвет осей и подписей в HEX формате */
    public String getAxisColorHex() {
        return axisColorHex;
    }

    /** @return толщина координатных осей в пикселях */
    public double getAxisStrokeWidth() {
        return axisStrokeWidth;
    }

    /**
     * Пошаговый билдер неизменяемой конфигурации графика.
     */
    public static final class Builder {

        private final FunctionMode mode;
        private final String fExpression;
        private final String gExpression;
        private final double rangeMin;
        private final double rangeMax;
        private int pointCount = 1000;
        private String colorHex = "#1E64C8";
        private double strokeWidth = 2;
        private LineStyle lineStyle = LineStyle.SOLID;
        private boolean autoScale = true;
        private ViewBox manualViewBox;
        private boolean showAxes = true;
        private boolean showGrid = true;
        private String axisColorHex = "#000000";
        private double axisStrokeWidth = 1.5;

        private Builder(FunctionMode mode, String fExpression, String gExpression, double rangeMin, double rangeMax) {
            this.mode = mode;
            this.fExpression = fExpression;
            this.gExpression = gExpression;
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
        }

        /**
         * @param v количество вычисляемых точек
         * @return этот билдер
         */
        public Builder pointCount(int v) {
            this.pointCount = v;
            return this;
        }

        /**
         * @param v цвет линии в HEX-формате
         * @return этот билдер
         */
        public Builder colorHex(String v) {
            this.colorHex = v;
            return this;
        }

        /**
         * @param v толщина линии в пикселях
         * @return этот билдер
         */
        public Builder strokeWidth(double v) {
            this.strokeWidth = v;
            return this;
        }

        /**
         * @param v стиль линии
         * @return этот билдер
         */
        public Builder lineStyle(LineStyle v) {
            this.lineStyle = v;
            return this;
        }

        /**
         * @param v признак автоматического масштаба
         * @return этот билдер
         */
        public Builder autoScale(boolean v) {
            this.autoScale = v;
            return this;
        }

        /**
         * @param v вручную заданная область отображения
         * @return этот билдер
         */
        public Builder manualViewBox(ViewBox v) {
            this.manualViewBox = v;
            return this;
        }

        /**
         * @param v признак отображения осей
         * @return этот билдер
         */
        public Builder showAxes(boolean v) {
            this.showAxes = v;
            return this;
        }

        /**
         * @param v признак отображения сетки
         * @return этот билдер
         */
        public Builder showGrid(boolean v) {
            this.showGrid = v;
            return this;
        }

        /**
         * @param v цвет осей в HEX формате
         * @return этот билдер
         */
        public Builder axisColorHex(String v) {
            this.axisColorHex = v;
            return this;
        }

        /**
         * @param v толщина осей в пикселях
         * @return этот билдер
         */
        public Builder axisStrokeWidth(double v) {
            this.axisStrokeWidth = v;
            return this;
        }

        /** @return неизменяемая конфигурация с заданными параметрами */
        public PlotConfig build() {
            return new PlotConfig(this);
        }
    }
}
