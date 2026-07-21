package org.app.service;

import org.app.domain.CurvePoint;
import org.app.domain.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurvePointGeneratorTest {

    @Test
    void generatesUniformExplicitPointsIncludingBothBounds() {
        List<Token> poliz = ExpressionParser.parseExplicit("x^2");
        List<CurvePoint> points = CurvePointGenerator.generateExplicit(poliz, -2, 2, 5);

        assertEquals(5, points.size());
        for (int i = 0; i < points.size(); i++) {
            double expectedX = -2 + i;
            assertTrue(points.get(i).isValid());
            assertEquals(expectedX, points.get(i).getX(), 1e-12);
            assertEquals(expectedX * expectedX, points.get(i).getY(), 1e-12);
        }
    }

    @Test
    void marksDomainErrorAsInvalidAndContinuesGeneration() {
        List<Token> poliz = ExpressionParser.parseExplicit("1/x");
        List<CurvePoint> points = CurvePointGenerator.generateExplicit(poliz, -1, 1, 3);

        assertTrue(points.get(0).isValid());
        assertFalse(points.get(1).isValid());
        assertTrue(points.get(2).isValid());
    }

    @Test
    void generatesParametricCurve() {
        ExpressionParser.ParametricExpressions parsed =
                ExpressionParser.parseParametric("cos(t)", "sin(t)");
        List<CurvePoint> points = CurvePointGenerator.generateParametric(
                parsed.getXPoliz(), parsed.getYPoliz(), 0, Math.PI / 2, 2);

        assertEquals(1, points.get(0).getX(), 1e-12);
        assertEquals(0, points.get(0).getY(), 1e-12);
        assertEquals(0, points.get(1).getX(), 1e-12);
        assertEquals(1, points.get(1).getY(), 1e-12);
    }

    @Test
    void marksDiscontinuityBetweenExplicitSamples() {
        List<Token> poliz = ExpressionParser.parseExplicit("1/x");
        List<CurvePoint> points = CurvePointGenerator.generateExplicit(poliz, -10, 10, 1000);

        assertTrue(points.stream().anyMatch(point -> !point.isValid()));
    }

    @Test
    void marksTangentAsymptotesBetweenSamples() {
        List<Token> poliz = ExpressionParser.parseExplicit("tg(x)");
        List<CurvePoint> points = CurvePointGenerator.generateExplicit(poliz, -2, 2, 1000);

        long invalidCount = points.stream().filter(point -> !point.isValid()).count();
        assertTrue(invalidCount >= 2);
    }

    @Test
    void marksMultipleTangentAsymptotes() {
        List<Token> poliz = ExpressionParser.parseExplicit("tg(10*x)");
        List<CurvePoint> points = CurvePointGenerator.generateExplicit(poliz, -2, 2, 1000);

        long invalidCount = points.stream().filter(point -> !point.isValid()).count();
        assertTrue(invalidCount >= 10);
    }

    @Test
    void keepsSmoothFunctionsContinuous() {
        List<Token> polynomial = ExpressionParser.parseExplicit("x^3");
        List<Token> exponential = ExpressionParser.parseExplicit("exp(x)");

        assertTrue(CurvePointGenerator.generateExplicit(polynomial, -10, 10, 1000)
                .stream().allMatch(CurvePoint::isValid));
        assertTrue(CurvePointGenerator.generateExplicit(exponential, -10, 10, 1000)
                .stream().allMatch(CurvePoint::isValid));
    }

    @Test
    void marksDiscontinuityInParametricCurve() {
        ExpressionParser.ParametricExpressions parsed =
                ExpressionParser.parseParametric("t", "1/t");
        List<CurvePoint> points = CurvePointGenerator.generateParametric(
                parsed.getXPoliz(), parsed.getYPoliz(), -10, 10, 1000);

        assertTrue(points.stream().anyMatch(point -> !point.isValid()));
    }

    @Test
    void detectsScaledDiscontinuities() {
        List<Token> reciprocal = ExpressionParser.parseExplicit("0.000001/(x-0.003)");
        List<Token> tangent = ExpressionParser.parseExplicit("0.0001*tg(x)");

        assertTrue(CurvePointGenerator.generateExplicit(reciprocal, -10, 10, 1000)
                .stream().anyMatch(point -> !point.isValid()));
        assertTrue(CurvePointGenerator.generateExplicit(tangent, -2, 2, 1000)
                .stream().anyMatch(point -> !point.isValid()));
    }

    @Test
    void keepsHighFrequencyFunctionContinuous() {
        List<Token> poliz = ExpressionParser.parseExplicit("sin(1000000*x)");
        List<CurvePoint> points = CurvePointGenerator.generateExplicit(poliz, -10, 10, 1000);

        assertTrue(points.stream().allMatch(CurvePoint::isValid));
    }
}
