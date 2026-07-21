package org.app.service;

/**
 * Переводим дробные числа в строки с фиксированным количеством знаков после запятой
 */
public class DecimalFormatter {

    private DecimalFormatter() {
    }

    /**
     * Форматирует число с фиксированным количеством знаков и точкой как разделителем
     *
     * @param value исходное число
     * @param decimals количество знаков после точки
     * @return форматированная строка, например {@code -4.00}
     */
    public static String format(double value, int decimals) {
        double abs = Math.abs(value);

        long scale = 1;
        for (int i = 0; i < decimals; i++) {
            scale *= 10;
        }

        long rounded = Math.round(abs * scale);
        long intPart = rounded / scale;
        long fracPart = rounded % scale;

        StringBuilder sb = new StringBuilder();
        if (value < 0 && rounded != 0) {
            sb.append('-');
        }
        sb.append(intPart);

        if (decimals > 0) {
            sb.append('.');
            String fracStr = Long.toString(fracPart);
            for (int i = fracStr.length(); i < decimals; i++) {
                sb.append('0');
            }
            sb.append(fracStr);
        }

        return sb.toString();
    }
}
