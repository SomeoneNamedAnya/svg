package org.app.domain;

/**
 * Лексема математического выражения с типом, текстовым значением и позицией в исходной строке
 */
public class Token {

    private final TokenType type;
    private final String value;
    private final int position;
    private final int argCount;

    /**
     * Создаёт обычный токен с одним аргументом
     *
     * @param type тип токена
     * @param value исходное текстовое значение
     * @param position позиция начала токена в выражении
     */
    public Token(TokenType type, String value, int position) {
        this(type, value, position, 1);
    }

    /**
     * Создаёт токен с явно заданным количеством аргументов функции
     *
     * @param type тип токена
     * @param value исходное текстовое значение
     * @param position позиция начала токена в выражении
     * @param argCount количество аргументов функции
     */
    public Token(TokenType type, String value, int position, int argCount) {
        this.type = type;
        this.value = value;
        this.position = position;
        this.argCount = argCount;
    }

    /** @return тип токена */
    public TokenType getType() {
        return type;
    }

    /** @return исходное текстовое значение токена */
    public String getValue() {
        return value;
    }

    /** @return индекс первого символа токена в исходной строке выражения */
    public int getPosition() {
        return position;
    }

    /**
     * @return количество аргументов вызова функции, для остальных токенов возвращает {@code 1}
     */
    public int getArgCount() {
        return argCount;
    }

    /** @return строковый вид токена */
    @Override
    public String toString() {
        return type + " : " + value;
    }
}
