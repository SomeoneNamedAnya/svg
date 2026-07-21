package org.app.exception;

/**
 * Базовое исключение обработки математического выражения
 * Хранит позицию символа в исходной строке для подсветки поля ввода
 */
public class ExpressionException extends RuntimeException {

    private final int position;

    /**
     * Создаёт ошибку выражения
     *
     * @param message описание ошибки
     * @param position индекс проблемного символа
     */
    public ExpressionException(String message, int position) {
        super(message);
        this.position = position;
    }

    /** @return индекс проблемного символа в исходном выражении */
    public int getPosition() {
        return position;
    }
}
