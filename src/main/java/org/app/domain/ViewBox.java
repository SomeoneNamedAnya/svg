package org.app.domain;

/**
 * Область построения графика в координатах данных (не пикселях)
 */
public final class ViewBox {

    private final double xMin;
    private final double xMax;
    private final double yMin;
    private final double yMax;

    /**
     * Создаёт прямоугольную область координат данных
     *
     * @param xMin минимальная координата X
     * @param xMax максимальная координата X
     * @param yMin минимальная координата Y
     * @param yMax максимальная координата Y
     * @throws IllegalArgumentException если минимум и максимум перепутаны
     */
    public ViewBox(double xMin, double xMax, double yMin, double yMax) {
        if (Double.isNaN(xMin) || Double.isInfinite(xMin)
                || Double.isNaN(xMax) || Double.isInfinite(xMax)
                || Double.isNaN(yMin) || Double.isInfinite(yMin)
                || Double.isNaN(yMax) || Double.isInfinite(yMax)) {
            throw new IllegalArgumentException("Границы viewBox должны быть конечными числами");
        }
        if (xMin >= xMax) {
            throw new IllegalArgumentException("xMin должен быть меньше xMax");
        }
        if (yMin >= yMax) {
            throw new IllegalArgumentException("yMin должен быть меньше yMax");
        }
        if (Double.isInfinite(xMax - xMin) || Double.isInfinite(yMax - yMin)) {
            throw new IllegalArgumentException("Диапазон viewBox слишком велик");
        }
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    /** @return минимальная координата X */
    public double getXMin() {
        return xMin;
    }

    /** @return максимальная координата X */
    public double getXMax() {
        return xMax;
    }

    /** @return минимальная координата Y */
    public double getYMin() {
        return yMin;
    }

    /** @return максимальная координата Y */
    public double getYMax() {
        return yMax;
    }

    /** @return ширина области по оси X */
    public double width() {
        return xMax - xMin;
    }

    /** @return высота области по оси Y */
    public double height() {
        return yMax - yMin;
    }
}
