package org.app.service;

import org.app.domain.Token;
import org.app.exception.MathEvaluationException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Вычисляет числовое значение выражения по его ПОЛИЗ для одного значения переменной
 * Стековый алгоритм. Числа и константы кладутся в стек, операторы и функции
 * снимают со стека нужное число значений и кладут обратно результат
 */
public class PolizEvaluator {

    private static final double EPSILON = 1e-9;

    private PolizEvaluator() {
    }

    /**
     * Вычисляет значение постфиксного выражения для заданного аргумента
     *
     * @param poliz выражение в постфиксной записи
     * @param variableValue значение независимой переменной
     * @return результат вычисления
     * @throws MathEvaluationException если в этой точке функция не определена
     *         (деление на 0, выход за область определения, комплексный результат)
     */
    public static double evaluate(List<Token> poliz, double variableValue) {
        Deque<Double> stack = new ArrayDeque<>();

        for (Token token : poliz) {
            switch (token.getType()) {
                case NUMBER:
                    stack.push(Double.parseDouble(token.getValue()));
                    break;

                case VARIABLE:
                    stack.push(variableValue);
                    break;

                case PI:
                    stack.push(Math.PI);
                    break;

                case E:
                    stack.push(Math.E);
                    break;

                case PLUS: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a + b);
                    break;
                }

                case MINUS: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a - b);
                    break;
                }

                case MUL: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a * b);
                    break;
                }

                case DIV: {
                    double b = stack.pop();
                    double a = stack.pop();
                    if (b == 0.0) {
                        throw new MathEvaluationException("Деление на ноль", variableValue);
                    }
                    stack.push(a / b);
                    break;
                }

                case POW: {
                    double b = stack.pop();
                    double a = stack.pop();
                    double result = Math.pow(a, b);
                    if (Double.isNaN(result)) {
                        throw new MathEvaluationException("Комплексный результат возведения в степень", variableValue);
                    }
                    stack.push(result);
                    break;
                }

                case UMINUS:
                    stack.push(-stack.pop());
                    break;

                case SIN:
                    stack.push(Math.sin(stack.pop()));
                    break;

                case COS:
                    stack.push(Math.cos(stack.pop()));
                    break;

                case TG: {
                    double arg = stack.pop();
                    double cos = Math.cos(arg);
                    if (Math.abs(cos) < EPSILON) {
                        throw new MathEvaluationException("tg не определён в этой точке", variableValue);
                    }
                    stack.push(Math.sin(arg) / cos);
                    break;
                }

                case CTG: {
                    double arg = stack.pop();
                    double sin = Math.sin(arg);
                    if (Math.abs(sin) < EPSILON) {
                        throw new MathEvaluationException("ctg не определён в этой точке", variableValue);
                    }
                    stack.push(Math.cos(arg) / sin);
                    break;
                }

                case ARCSIN: {
                    double arg = stack.pop();
                    if (arg < -1.0 || arg > 1.0) {
                        throw new MathEvaluationException("arcsin определён только на [-1, 1]", variableValue);
                    }
                    stack.push(Math.asin(arg));
                    break;
                }

                case ARCCOS: {
                    double arg = stack.pop();
                    if (arg < -1.0 || arg > 1.0) {
                        throw new MathEvaluationException("arccos определён только на [-1, 1]", variableValue);
                    }
                    stack.push(Math.acos(arg));
                    break;
                }

                case ARCTG:
                    stack.push(Math.atan(stack.pop()));
                    break;

                case ARCCTG:
                    stack.push(Math.PI / 2 - Math.atan(stack.pop()));
                    break;

                case EXP:
                    stack.push(Math.exp(stack.pop()));
                    break;

                case LN: {
                    double arg = stack.pop();
                    if (arg <= 0.0) {
                        throw new MathEvaluationException("ln определён только для положительных чисел", variableValue);
                    }
                    stack.push(Math.log(arg));
                    break;
                }

                case LOG: {
                    if (token.getArgCount() == 2) {
                        double base = stack.pop();
                        double arg = stack.pop();
                        if (arg <= 0.0 || base <= 0.0 || base == 1.0) {
                            throw new MathEvaluationException(
                                    "log определён для положительного числа и основания > 0, отличного от 1", variableValue);
                        }
                        stack.push(Math.log(arg) / Math.log(base));
                    } else {
                        double arg = stack.pop();
                        if (arg <= 0.0) {
                            throw new MathEvaluationException("log определён только для положительных чисел", variableValue);
                        }
                        stack.push(Math.log10(arg));
                    }
                    break;
                }

                case SQRT: {
                    double arg = stack.pop();
                    if (arg < 0.0) {
                        throw new MathEvaluationException("sqrt от отрицательного числа", variableValue);
                    }
                    stack.push(Math.sqrt(arg));
                    break;
                }

                case CBRT:
                    stack.push(Math.cbrt(stack.pop()));
                    break;

                case ABS:
                    stack.push(Math.abs(stack.pop()));
                    break;

                default:
                    throw new IllegalStateException("Неожиданный токен в ПОЛИЗ: " + token.getType());
            }

            if (!stack.isEmpty()) {
                double result = stack.peek();
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    throw new MathEvaluationException("Результат вычисления не является конечным числом", variableValue);
                }
            }
        }

        return stack.pop();
    }
}
