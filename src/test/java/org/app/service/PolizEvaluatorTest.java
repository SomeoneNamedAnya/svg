package org.app.service;

import org.app.domain.Token;
import org.app.exception.MathEvaluationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PolizEvaluatorTest {

    @Test
    void evaluatesArithmeticWithCorrectPrecedence() {
        assertAll(
                () -> assertEquals(14, evaluate("2+3*4", 0), 1e-12),
                () -> assertEquals(20, evaluate("(2+3)*4", 0), 1e-12),
                () -> assertEquals(-4, evaluate("-2^2", 0), 1e-12),
                () -> assertEquals(512, evaluate("2^3^2", 0), 1e-12)
        );
    }

    @Test
    void evaluatesSupportedFunctionsAndConstants() {
        assertAll(
                () -> assertEquals(1, evaluate("sin(x)", Math.PI / 2), 1e-12),
                () -> assertEquals(1, evaluate("cos(x)", 0), 1e-12),
                () -> assertEquals(1, evaluate("tg(x)", Math.PI / 4), 1e-12),
                () -> assertEquals(1, evaluate("ctg(x)", Math.PI / 4), 1e-12),
                () -> assertEquals(Math.PI / 2, evaluate("arcsin(x)", 1), 1e-12),
                () -> assertEquals(0, evaluate("arccos(x)", 1), 1e-12),
                () -> assertEquals(Math.PI / 4, evaluate("arctg(x)", 1), 1e-12),
                () -> assertEquals(Math.PI / 4, evaluate("arcctg(x)", 1), 1e-12),
                () -> assertEquals(Math.E, evaluate("exp(1)", 0), 1e-12),
                () -> assertEquals(1, evaluate("ln(e)", 0), 1e-12),
                () -> assertEquals(2, evaluate("log(100)", 0), 1e-12),
                () -> assertEquals(3, evaluate("log(8, 2)", 0), 1e-12),
                () -> assertEquals(2, evaluate("sqrt(4)", 0), 1e-12),
                () -> assertEquals(-2, evaluate("cbrt(-8)", 0), 1e-12),
                () -> assertEquals(5, evaluate("abs(-5)", 0), 1e-12)
        );
    }

    @Test
    void reportsMathematicalDomainErrors() {
        assertAll(
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("1/x", 0)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("ln(x)", 0)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("sqrt(x)", -1)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("arcsin(x)", 2)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("arccos(x)", -2)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("log(x, 1)", 10)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("(-2)^0.5", 0)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("tg(x)", Math.PI / 2)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("ctg(x)", 0))
        );
    }

    @Test
    void rejectsNonFiniteResults() {
        assertAll(
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("exp(1000)", 0)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("0^-1", 0)),
                () -> assertThrows(MathEvaluationException.class, () -> evaluate("sin(exp(1000))", 0))
        );
    }

    private static double evaluate(String expression, double variableValue) {
        List<Token> poliz = ExpressionParser.parseExplicit(expression);
        return PolizEvaluator.evaluate(poliz, variableValue);
    }
}
