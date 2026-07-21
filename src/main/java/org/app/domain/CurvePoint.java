package org.app.domain;

/**
 * Одна точка кривой. Невалидная точка (деление на 0) вместо координат содержит Nan
 */
public final class CurvePoint {

    private final double x;
    private final double y;
    private final boolean valid;

    private CurvePoint(double x, double y, boolean valid) {
        this.x = x;
        this.y = y;
        this.valid = valid;
    }

    /**
     * Создаёт вычисленную точку кривой
     *
     * @param x координата по оси X
     * @param y координата по оси Y
     * @return валидная точка с заданными координатами
     */
    public static CurvePoint valid(double x, double y) {
        return new CurvePoint(x, y, true);
    }

    /**
     * Создаёт маркер разрыва кривой без координат
     *
     * @return невалидная точка
     */
    public static CurvePoint invalid() {
        return new CurvePoint(Double.NaN, Double.NaN, false);
    }

    /** @return координата X */
    public double getX() {
        return x;
    }

    /** @return координата Y */
    public double getY() {
        return y;
    }

    /** @return {@code true}, если координаты точки можно использовать при построении */
    public boolean isValid() {
        return valid;
    }
}
