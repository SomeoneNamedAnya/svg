package org.app.domain;

/**
 * Отрезок линии (сетки или оси) в пиксельных координатах
 */
public final class GridLine {

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    /**
     * Создаёт отрезок между двумя точками
     *
     * @param x1 координата X начала
     * @param y1 координата Y начала
     * @param x2 координата X конца
     * @param y2 координата Y конца
     */
    public GridLine(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /** @return координата X начала отрезка */
    public double getX1() {
        return x1;
    }

    /** @return координата Y начала отрезка */
    public double getY1() {
        return y1;
    }

    /** @return координата X конца отрезка */
    public double getX2() {
        return x2;
    }

    /** @return координата Y конца отрезка */
    public double getY2() {
        return y2;
    }
}
