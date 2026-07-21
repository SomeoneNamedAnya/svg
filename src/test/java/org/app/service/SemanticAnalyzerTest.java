package org.app.service;

import org.app.domain.Token;
import org.app.exception.ExpressionSemanticException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SemanticAnalyzerTest {

    @Test
    void acceptsSupportedFunctionArity() {
        assertAll(
                () -> assertDoesNotThrow(() -> SemanticAnalyzer.validateArity(poliz("sin(x)"))),
                () -> assertDoesNotThrow(() -> SemanticAnalyzer.validateArity(poliz("log(x)"))),
                () -> assertDoesNotThrow(() -> SemanticAnalyzer.validateArity(poliz("log(x, 2)")))
        );
    }

    @Test
    void rejectsUnsupportedFunctionArity() {
        assertAll(
                () -> assertThrows(ExpressionSemanticException.class,
                        () -> SemanticAnalyzer.validateArity(poliz("sin(x, 2)"))),
                () -> assertThrows(ExpressionSemanticException.class,
                        () -> SemanticAnalyzer.validateArity(poliz("log(x, 2, 3)")))
        );
    }

    @Test
    void validatesOneVariableAcrossSeveralExpressions() {
        assertDoesNotThrow(() -> SemanticAnalyzer.validateSingleVariable(
                poliz("cos(t)"), poliz("sin(t)")));

        assertThrows(ExpressionSemanticException.class,
                () -> SemanticAnalyzer.validateSingleVariable(poliz("cos(t)"), poliz("sin(x)")));
    }

    @Test
    void rejectsVariableInConstantExpression() {
        assertDoesNotThrow(() -> SemanticAnalyzer.validateNoVariable(poliz("2*pi")));
        assertThrows(ExpressionSemanticException.class,
                () -> SemanticAnalyzer.validateNoVariable(poliz("2*x")));
    }

    private static List<Token> poliz(String expression) {
        return SyntaxAnalyzer.toPoliz(LexAnalyzer.analyze(expression));
    }
}
