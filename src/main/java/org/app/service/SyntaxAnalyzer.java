package org.app.service;

import org.app.domain.Token;
import org.app.domain.TokenType;
import org.app.exception.ExpressionSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Разбирает список токенов по грамматике
 * и одновременно строит ПОЛИЗ (список токенов в постфиксной записи)
 * Количество аргументов функции здесь не проверяется (это делает SemanticAnalyzer)
 * грамматика допускает произвольное число аргументов через запятую и просто фиксирует
 * фактическое количество в поле argCount итогового токена функции.
 */
public class SyntaxAnalyzer {

    private final List<Token> input;
    private final List<Token> output = new ArrayList<>();
    private int pos = 0;

    private SyntaxAnalyzer(List<Token> input) {
        this.input = input;
    }

    /**
     * Строит ПОЛИЗ по списку токенов, полученному от LexAnalyzer
     *
     * @param tokens токены исходного выражения
     * @return выражение в постфиксной записи
     * @throws ExpressionSyntaxException при нарушении грамматики выражения
     */
    public static List<Token> toPoliz(List<Token> tokens) {
        if (tokens.size() == 1 && tokens.get(0).getType() == TokenType.END) {
            throw new ExpressionSyntaxException("Выражение пустое", 0);
        }

        SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokens);
        analyzer.parseExpression();
        analyzer.expect(TokenType.END);
        return analyzer.output;
    }

    private Token current() {
        return input.get(pos);
    }

    private Token advance() {
        Token token = input.get(pos);
        pos++;
        return token;
    }

    private void expect(TokenType type) {
        if (current().getType() != type) {
            throw new ExpressionSyntaxException(
                    "Неожиданный токен '" + current().getValue() + "'", current().getPosition());
        }
        advance();
    }

    private void parseExpression() {
        parseTerm();

        while (current().getType() == TokenType.PLUS || current().getType() == TokenType.MINUS) {
            Token op = advance();
            parseTerm();
            output.add(op);
        }
    }

    private void parseTerm() {
        parseUnary();

        while (current().getType() == TokenType.MUL || current().getType() == TokenType.DIV) {
            Token op = advance();
            parseUnary();
            output.add(op);
        }
    }

    private void parseUnary() {
        if (current().getType() == TokenType.MINUS) {
            Token minus = advance();
            parseUnary();
            output.add(new Token(TokenType.UMINUS, "-", minus.getPosition()));
        } else {
            parsePower();
        }
    }

    private void parsePower() {
        parsePrimary();

        if (current().getType() == TokenType.POW) {
            Token op = advance();
            parseUnary();
            output.add(op);
        }
    }

    private void parsePrimary() {
        Token token = current();

        switch (token.getType()) {
            case NUMBER:
            case VARIABLE:
            case PI:
            case E:
                advance();
                output.add(token);
                return;

            case OPEN:
                advance();
                parseExpression();
                expect(TokenType.CLOSE);
                return;

            default:
                if (isFunction(token.getType())) {
                    parseFunctionCall(token);
                } else {
                    throw new ExpressionSyntaxException(
                            "Неожиданный токен '" + token.getValue() + "'", token.getPosition());
                }
        }
    }

    private void parseFunctionCall(Token function) {
        advance();
        expect(TokenType.OPEN);

        parseExpression();
        int argCount = 1;

        while (current().getType() == TokenType.COMMA) {
            advance();
            parseExpression();
            argCount++;
        }

        expect(TokenType.CLOSE);
        output.add(new Token(function.getType(), function.getValue(), function.getPosition(), argCount));
    }

    static boolean isFunction(TokenType type) {
        switch (type) {
            case SIN:
            case COS:
            case TG:
            case CTG:
            case ARCSIN:
            case ARCCOS:
            case ARCTG:
            case ARCCTG:
            case EXP:
            case LN:
            case LOG:
            case SQRT:
            case CBRT:
            case ABS:
                return true;
            default:
                return false;
        }
    }
}
