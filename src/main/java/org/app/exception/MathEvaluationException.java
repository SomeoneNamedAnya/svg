package org.app.exception;

/**
 * Ошибка вычисления значения функции в конкретной точке (деление на 0, выход за область,
 * комплексный результат). Хранит значение аргумента, на котором вычисление сломалось.
 */
public class MathEvaluationException extends RuntimeException {

    private final double argumentValue;

    /**
     * @param message описание ошибки
     * @param argumentValue значение аргумента, на котором произошла ошибка
     */
    public MathEvaluationException(String message, double argumentValue) {
        super(message);
        this.argumentValue = argumentValue;
    }

    /** @return значение аргумента, на котором произошло ошибочное вычисление */
    public double getArgumentValue() {
        return argumentValue;
    }
}
