package org.app.exception;

/**
 * Ошибка синтаксического разбора выражения
 * (неизвестный символ, неожиданный токен, несбалансированные скобки)
 */
public class ExpressionSyntaxException extends ExpressionException {

    /**
     * @param message описание ошибки
     * @param position индекс проблемного символа
     */
    public ExpressionSyntaxException(String message, int position) {
        super(message, position);
    }
}
