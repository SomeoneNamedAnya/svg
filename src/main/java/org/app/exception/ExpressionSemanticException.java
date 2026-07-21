package org.app.exception;

/**
 * Семантическая ошибка в разобранном выражении
 * (неверное число аргументов функции, несколько разных переменных)
 */
public class ExpressionSemanticException extends ExpressionException {

    /**
     * @param message описание ошибки
     * @param position индекс связанного с ошибкой символа
     */
    public ExpressionSemanticException(String message, int position) {
        super(message, position);
    }
}
