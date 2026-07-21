package org.app.service;

import org.app.domain.Token;
import org.app.domain.TokenType;
import org.app.exception.ExpressionSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SyntaxAnalyzerTest {

    @Test
    void respectsOperatorPrecedenceAndParentheses() {
        assertAll(
                () -> assertEquals("NUMBER NUMBER NUMBER MUL PLUS", poliz("2+3*4")),
                () -> assertEquals("NUMBER NUMBER PLUS NUMBER MUL", poliz("(2+3)*4"))
        );
    }

    @Test
    void handlesUnaryMinusAndRightAssociativePower() {
        assertAll(
                () -> assertEquals("NUMBER NUMBER POW UMINUS", poliz("-2^2")),
                () -> assertEquals("NUMBER NUMBER UMINUS POW", poliz("2^-3")),
                () -> assertEquals("NUMBER NUMBER NUMBER POW POW", poliz("2^3^2"))
        );
    }

    @Test
    void recordsActualFunctionArgumentCount() {
        List<Token> oneArgument = parse("log(x)");
        List<Token> twoArguments = parse("log(x, 2)");

        assertEquals(1, oneArgument.get(oneArgument.size() - 1).getArgCount());
        assertEquals(2, twoArguments.get(twoArguments.size() - 1).getArgCount());
    }

    @Test
    void rejectsInvalidGrammar() {
        assertAll(
                () -> assertThrows(ExpressionSyntaxException.class, () -> parse("")),
                () -> assertThrows(ExpressionSyntaxException.class, () -> parse("2+")),
                () -> assertThrows(ExpressionSyntaxException.class, () -> parse("(2+3")),
                () -> assertThrows(ExpressionSyntaxException.class, () -> parse("sin x")),
                () -> assertThrows(ExpressionSyntaxException.class, () -> parse("2 3"))
        );
    }

    private static String poliz(String expression) {
        StringBuilder result = new StringBuilder();
        for (Token token : parse(expression)) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(token.getType());
        }
        return result.toString();
    }

    private static List<Token> parse(String expression) {
        return SyntaxAnalyzer.toPoliz(LexAnalyzer.analyze(expression));
    }
}
