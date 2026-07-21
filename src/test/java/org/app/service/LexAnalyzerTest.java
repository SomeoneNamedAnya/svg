package org.app.service;

import org.app.domain.Token;
import org.app.domain.TokenType;
import org.app.exception.ExpressionSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LexAnalyzerTest {

    @Test
    void recognizesNumbersConstantsVariableAndFunctions() {
        List<Token> tokens = LexAnalyzer.analyze("sin(x) + log(100, 10) - pi * e");

        assertEquals(List.of(
                TokenType.SIN, TokenType.OPEN, TokenType.VARIABLE, TokenType.CLOSE,
                TokenType.PLUS,
                TokenType.LOG, TokenType.OPEN, TokenType.NUMBER, TokenType.COMMA,
                TokenType.NUMBER, TokenType.CLOSE,
                TokenType.MINUS, TokenType.PI, TokenType.MUL, TokenType.E, TokenType.END
        ), types(tokens));
    }

    @Test
    void recognizesBothPowerOperators() {
        List<Token> tokens = LexAnalyzer.analyze("2^3 ** 4");

        assertEquals(List.of(
                TokenType.NUMBER, TokenType.POW, TokenType.NUMBER,
                TokenType.POW, TokenType.NUMBER, TokenType.END
        ), types(tokens));
        assertEquals("^", tokens.get(1).getValue());
        assertEquals("**", tokens.get(3).getValue());
    }

    @Test
    void storesTokenPositionsInOriginalExpression() {
        List<Token> tokens = LexAnalyzer.analyze("  sqrt(x)");

        assertAll(
                () -> assertEquals(2, tokens.get(0).getPosition()),
                () -> assertEquals(6, tokens.get(1).getPosition()),
                () -> assertEquals(7, tokens.get(2).getPosition()),
                () -> assertEquals(8, tokens.get(3).getPosition()),
                () -> assertEquals(9, tokens.get(4).getPosition())
        );
    }

    @Test
    void rejectsMalformedInput() {
        assertAll(
                () -> assertThrows(ExpressionSyntaxException.class,
                        () -> LexAnalyzer.analyze("1.2.3")),
                () -> assertThrows(ExpressionSyntaxException.class,
                        () -> LexAnalyzer.analyze("unknown(x)")),
                () -> assertThrows(ExpressionSyntaxException.class,
                        () -> LexAnalyzer.analyze("2 & 3")),
                () -> assertThrows(ExpressionSyntaxException.class,
                        () -> LexAnalyzer.analyze(".5")),
                () -> assertThrows(ExpressionSyntaxException.class,
                        () -> LexAnalyzer.analyze("5."))
        );
    }

    private static List<TokenType> types(List<Token> tokens) {
        return tokens.stream().map(Token::getType).collect(Collectors.toList());
    }
}
