package org.app.domain;

/**
 * Стиль линии графика (stroke-dasharray)
 */
public enum LineStyle {

    /** Сплошная линия без штрихов */
    SOLID(null),

    /** Пунктирная линия с чередованием штриха и пробела */
    DASHED(new double[]{6, 4});

    private final double[] dashPattern;

    LineStyle(double[] dashPattern) {
        this.dashPattern = dashPattern;
    }

    /** @return шаблон штрихов или {@code null} для сплошной линии */
    public double[] getDashPattern() {
        return dashPattern;
    }

    /**
     * @return значение SVG атрибута {@code stroke-dasharray} или {@code null}, если атрибут не нужен
     */
    public String toSvgDashArray() {
        if (dashPattern == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dashPattern.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append((int) dashPattern[i]);
        }
        return sb.toString();
    }
}
