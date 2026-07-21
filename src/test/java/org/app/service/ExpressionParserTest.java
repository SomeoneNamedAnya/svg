package org.app.service;

import org.app.domain.Token;
import org.app.exception.ExpressionSemanticException;
import org.app.exception.ExpressionSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionParserTest {

    @Test
    void parsesExplicitExpressionThroughAllAnalysisStages() {
        List<Token> poliz = ExpressionParser.parseExplicit("sqrt(x^2 + 1)");

        assertFalse(poliz.isEmpty());
        assertEquals("SQRT", poliz.get(poliz.size() - 1).getType().name());
    }

    @Test
    void parsesParametricExpressionsWithCommonVariable() {
        ExpressionParser.ParametricExpressions parsed =
                ExpressionParser.parseParametric("cos(t)", "sin(t)");

        assertFalse(parsed.getXPoliz().isEmpty());
        assertFalse(parsed.getYPoliz().isEmpty());
    }

    @Test
    void rejectsDifferentVariablesInParametricExpressions() {
        assertThrows(ExpressionSemanticException.class,
                () -> ExpressionParser.parseParametric("cos(t)", "sin(x)"));
    }

    @Test
    void evaluatesConstantExpressionAndRejectsVariable() {
        assertEquals(2 * Math.PI, ExpressionParser.evaluateConstant("2*pi"), 1e-12);
        assertEquals(Math.sqrt(2), ExpressionParser.evaluateConstant("sqrt(2)"), 1e-12);
        assertThrows(ExpressionSemanticException.class,
                () -> ExpressionParser.evaluateConstant("2*x"));
    }

    @Test
    void preservesSyntaxErrorPosition() {
        ExpressionSyntaxException error = assertThrows(ExpressionSyntaxException.class,
                () -> ExpressionParser.parseExplicit("2 + & 3"));

        assertEquals(4, error.getPosition());
    }
}
