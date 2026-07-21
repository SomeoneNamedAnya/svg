package org.app.service;

import org.app.domain.Token;
import org.app.domain.TokenType;
import org.app.exception.ExpressionSemanticException;

import java.util.List;

/**
 * Проверяет смысловую корректность уже построенного ПОЛИЗ:
 * количество аргументов функций и то, что во всём выражении используется
 * не более одного имени переменной (само имя переменной значения не имеет)
 */
public class SemanticAnalyzer {

    private SemanticAnalyzer() {
    }

    /**
     * Проверяет допустимое количество аргументов у всех функций
     *
     * @param poliz выражение в постфиксной записи
     * @throws ExpressionSemanticException если функция вызвана с недопустимым числом аргументов
     *         (log - 1 или 2, остальные функции - 1)
     */
    public static void validateArity(List<Token> poliz) {
        for (Token token : poliz) {
            if (!SyntaxAnalyzer.isFunction(token.getType())) {
                continue;
            }

            int maxArgs = token.getType() == TokenType.LOG ? 2 : 1;
            int actualArgs = token.getArgCount();

            if (actualArgs < 1 || actualArgs > maxArgs) {
                String expected = maxArgs == 1 ? "1 аргумент" : "1 или 2 аргумента";
                throw new ExpressionSemanticException(
                        "Функция '" + token.getValue() + "' ожидает " + expected
                                + ", передано: " + actualArgs,
                        token.getPosition());
            }
        }
    }

    /**
     * Проверяет, что в выражении нет ни одной переменной (для полей,
     * которые должны быть константой (границы диапазона))
     *
     * @param poliz выражение в постфиксной записи
     * @throws ExpressionSemanticException если встретилась переменная
     */
    public static void validateNoVariable(List<Token> poliz) {
        for (Token token : poliz) {
            if (token.getType() == TokenType.VARIABLE) {
                throw new ExpressionSemanticException(
                        "Здесь должно быть константное выражение без переменной '" + token.getValue() + "'",
                        token.getPosition());
            }
        }
    }

    /**
     * Проверяет, что во всех переданных выражениях не смешиваются разные имена переменных
     *
     * @param polizExpressions одно или несколько выражений в постфиксной записи
     * @throws ExpressionSemanticException если найдены две разные переменные
     */
    @SafeVarargs
    public static void validateSingleVariable(List<Token>... polizExpressions) {
        String variableName = null;

        for (List<Token> poliz : polizExpressions) {
            for (Token token : poliz) {
                if (token.getType() != TokenType.VARIABLE) {
                    continue;
                }

                if (variableName == null) {
                    variableName = token.getValue();
                } else if (!variableName.equals(token.getValue())) {
                    throw new ExpressionSemanticException(
                            "В выражении используются разные переменные: '"
                                    + variableName + "' и '" + token.getValue() + "'",
                            token.getPosition());
                }
            }
        }
    }
}
