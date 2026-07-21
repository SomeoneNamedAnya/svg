package org.app.service;

import org.app.domain.Token;
import org.app.domain.TokenType;
import org.app.exception.ExpressionSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Разбивает строку математического выражения на список токенов
 * Единственная буква, не совпадающая ни с одной константой/функцией (например, "x", "t", "y"),
 * считается переменной, конкретное имя переменной не фиксируется здесь,
 * проверку на единственность переменной делает SemanticAnalyzer.
 */
public class LexAnalyzer {

    /**
     * Выполняет лексический анализ исходного выражения
     *
     * @param expr математическое выражение
     * @return последовательность токенов с завершающим маркером {@code END}
     * @throws ExpressionSyntaxException если встречено неизвестное слово, символ или некорректное число
     */
    public static List<Token> analyze(String expr) {

        List<Token> tokens = new ArrayList<>();

        int i = 0;

        while (i < expr.length()) {

            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Проверяем что текущей токен число
            if (Character.isDigit(c) || c == '.') {

                int start = i;
                StringBuilder number = new StringBuilder();

                while (i < expr.length()) {

                    char ch = expr.charAt(i);

                    if (Character.isDigit(ch) || ch == '.') {
                        number.append(ch);
                        i++;
                    } else {
                        break;
                    }
                }

                String numberText = number.toString();
                if (!isValidNumber(numberText)) {
                    throw new ExpressionSyntaxException("Некорректное число: " + numberText, start);
                }

                tokens.add(new Token(TokenType.NUMBER, numberText, start));
                continue;
            }

            // Проверка на функцию, константу или переменную
            if (Character.isLetter(c)) {

                int start = i;
                StringBuilder word = new StringBuilder();

                while (i < expr.length()) {

                    char ch = expr.charAt(i);

                    if (Character.isLetter(ch)) {
                        word.append(ch);
                        i++;
                    } else {
                        break;
                    }
                }

                String s = word.toString().toLowerCase();

                switch (s) {

                    case "pi":
                        tokens.add(new Token(TokenType.PI, s, start));
                        break;

                    case "e":
                        tokens.add(new Token(TokenType.E, s, start));
                        break;

                    case "sin":
                        tokens.add(new Token(TokenType.SIN, s, start));
                        break;

                    case "cos":
                        tokens.add(new Token(TokenType.COS, s, start));
                        break;

                    case "tg":
                        tokens.add(new Token(TokenType.TG, s, start));
                        break;

                    case "ctg":
                        tokens.add(new Token(TokenType.CTG, s, start));
                        break;

                    case "arcsin":
                        tokens.add(new Token(TokenType.ARCSIN, s, start));
                        break;

                    case "arccos":
                        tokens.add(new Token(TokenType.ARCCOS, s, start));
                        break;

                    case "arctg":
                        tokens.add(new Token(TokenType.ARCTG, s, start));
                        break;

                    case "arcctg":
                        tokens.add(new Token(TokenType.ARCCTG, s, start));
                        break;

                    case "exp":
                        tokens.add(new Token(TokenType.EXP, s, start));
                        break;

                    case "ln":
                        tokens.add(new Token(TokenType.LN, s, start));
                        break;

                    case "log":
                        tokens.add(new Token(TokenType.LOG, s, start));
                        break;

                    case "sqrt":
                        tokens.add(new Token(TokenType.SQRT, s, start));
                        break;

                    case "cbrt":
                        tokens.add(new Token(TokenType.CBRT, s, start));
                        break;

                    case "abs":
                        tokens.add(new Token(TokenType.ABS, s, start));
                        break;

                    default:
                        if (s.length() == 1) {
                            tokens.add(new Token(TokenType.VARIABLE, s, start));
                        } else {
                            throw new ExpressionSyntaxException("Неизвестный идентификатор: " + word, start);
                        }
                }

                continue;
            }

            // Проверка на операции, скобки и запятую
            int start = i;
            switch (c) {

                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+", start));
                    break;

                case '-':
                    tokens.add(new Token(TokenType.MINUS, "-", start));
                    break;

                case '*':
                    if (i + 1 < expr.length() && expr.charAt(i + 1) == '*') {
                        tokens.add(new Token(TokenType.POW, "**", start));
                        i++;
                    } else {
                        tokens.add(new Token(TokenType.MUL, "*", start));
                    }
                    break;

                case '/':
                    tokens.add(new Token(TokenType.DIV, "/", start));
                    break;

                case '^':
                    tokens.add(new Token(TokenType.POW, "^", start));
                    break;

                case '(':
                    tokens.add(new Token(TokenType.OPEN, "(", start));
                    break;

                case ')':
                    tokens.add(new Token(TokenType.CLOSE, ")", start));
                    break;

                case ',':
                    tokens.add(new Token(TokenType.COMMA, ",", start));
                    break;

                default:
                    throw new ExpressionSyntaxException("Недопустимый символ: " + c, start);
            }

            i++;
        }

        tokens.add(new Token(TokenType.END, "", expr.length()));
        return tokens;
    }

    /** Ручная проверка что не больше одной точки, цифры до и после неё. */
    private static boolean isValidNumber(String s) {
        int dotIndex = s.indexOf('.');
        if (dotIndex == -1) {
            return !s.isEmpty();
        }

        if (dotIndex == 0 || dotIndex == s.length() - 1) {
            return false;
        }
        if (s.indexOf('.', dotIndex + 1) != -1) {
            return false;
        }
        return true;
    }
}
