package org.app.service;

import org.app.domain.Token;

import java.util.List;

/**
 * Принимает строи математического выражения из UI,
 * прогоняет их через лексический, синтаксический и семантический анализ
 * и отдаёт готовый ПОЛИЗ, по которому позже будут вычисляться точки кривой
 */
public class ExpressionParser {

    private ExpressionParser() {
    }

    /**
     * Разбирает явную функцию y = f(x).
     *
     * @param fExpression исходное выражение функции
     * @return выражение в постфиксной записи
     * @throws org.app.exception.ExpressionException при синтаксической/семантической ошибке
     */
    public static List<Token> parseExplicit(String fExpression) {
        List<Token> poliz = toPoliz(fExpression);
        SemanticAnalyzer.validateSingleVariable(poliz);
        return poliz;
    }

    /**
     * Разбирает параметрическую систему x = f(t), y = g(t)
     * Обе части должны использовать одну и ту же переменную
     *
     * @param fOfT выражение координаты X
     * @param gOfT выражение координаты Y
     * @return пара выражений в постфиксной записи
     * @throws org.app.exception.ExpressionException при синтаксической/семантической ошибке
     */
    public static ParametricExpressions parseParametric(String fOfT, String gOfT) {
        List<Token> xPoliz = toPoliz(fOfT);
        List<Token> yPoliz = toPoliz(gOfT);
        SemanticAnalyzer.validateSingleVariable(xPoliz, yPoliz);
        return new ParametricExpressions(xPoliz, yPoliz);
    }


    /**
     * Разбирает выражение в ПОЛИЗ
     *
     * @param expression исходное выражение
     * @return разобранное выражение в виде списка токенов
     */
    private static List<Token> toPoliz(String expression) {
        List<Token> tokens = LexAnalyzer.analyze(expression);
        List<Token> poliz = SyntaxAnalyzer.toPoliz(tokens);
        SemanticAnalyzer.validateArity(poliz);
        return poliz;
    }

    /**
     * Вычисляет значение константного выражения (например, границы диапазона: "2*pi", "sqrt(2)")
     * @param expression константное математическое выражение
     * @return вычисленное значение
     * @throws org.app.exception.ExpressionException при синтаксической/семантической ошибке
     *         или если в выражении встретилась переменная
     */
    public static double evaluateConstant(String expression) {
        List<Token> poliz = toPoliz(expression);
        SemanticAnalyzer.validateNoVariable(poliz);
        return PolizEvaluator.evaluate(poliz, 0.0);
    }

    /**
     * Пара разобранных ПОЛИЗ-выражений параметрической системы
     */
    public static final class ParametricExpressions {

        private final List<Token> xPoliz;
        private final List<Token> yPoliz;

        /**
         * @param xPoliz ПОЛИЗ выражения координаты X
         * @param yPoliz ПОЛИЗ выражения координаты Y
         */
        public ParametricExpressions(List<Token> xPoliz, List<Token> yPoliz) {
            this.xPoliz = xPoliz;
            this.yPoliz = yPoliz;
        }

        /** @return ПОЛИЗ выражения координаты X */
        public List<Token> getXPoliz() {
            return xPoliz;
        }

        /** @return ПОЛИЗ выражения координаты Y */
        public List<Token> getYPoliz() {
            return yPoliz;
        }
    }
}
