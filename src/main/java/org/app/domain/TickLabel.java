package org.app.domain;

/**
 * Текстовая подпись метки шкалы в пиксельных координатах
 */
public final class TickLabel {

    private final String text;
    private final double x;
    private final double y;

    /**
     * Создаёт подпись в заданной точке SVG
     *
     * @param text отображаемый текст
     * @param x координата X
     * @param y координата Y
     */
    public TickLabel(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    /** @return отображаемый текст */
    public String getText() {
        return text;
    }

    /** @return координата X подписи */
    public double getX() {
        return x;
    }

    /** @return координата Y подписи */
    public double getY() {
        return y;
    }
}
