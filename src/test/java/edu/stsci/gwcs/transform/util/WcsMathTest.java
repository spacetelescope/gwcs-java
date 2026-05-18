package edu.stsci.gwcs.transform.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WcsMathTest {

    @Test
    void sindIsExactAtMultiplesOf90() {
        assertEquals(0.0, WcsMath.sind(0.0));
        assertEquals(1.0, WcsMath.sind(90.0));
        assertEquals(0.0, WcsMath.sind(180.0));
        assertEquals(-1.0, WcsMath.sind(270.0));
        assertEquals(0.0, WcsMath.sind(360.0));
        assertEquals(-1.0, WcsMath.sind(-90.0));
        assertEquals(0.0, WcsMath.sind(-180.0));
        assertEquals(1.0, WcsMath.sind(450.0));
    }

    @Test
    void cosdIsExactAtMultiplesOf90() {
        assertEquals(1.0, WcsMath.cosd(0.0));
        assertEquals(0.0, WcsMath.cosd(90.0));
        assertEquals(-1.0, WcsMath.cosd(180.0));
        assertEquals(0.0, WcsMath.cosd(270.0));
        assertEquals(1.0, WcsMath.cosd(360.0));
        assertEquals(0.0, WcsMath.cosd(-90.0));
        assertEquals(-1.0, WcsMath.cosd(-180.0));
    }

    @Test
    void sindAtNonMultiplesMatchesMathSin() {
        assertEquals(Math.sin(Math.toRadians(30.0)), WcsMath.sind(30.0));
        assertEquals(Math.sin(Math.toRadians(45.123)), WcsMath.sind(45.123));
        assertEquals(Math.sin(Math.toRadians(-17.5)), WcsMath.sind(-17.5));
    }

    @Test
    void cosdAtNonMultiplesMatchesMathCos() {
        assertEquals(Math.cos(Math.toRadians(30.0)), WcsMath.cosd(30.0));
        assertEquals(Math.cos(Math.toRadians(60.001)), WcsMath.cosd(60.001));
        assertEquals(Math.cos(Math.toRadians(-22.5)), WcsMath.cosd(-22.5));
    }

    @Test
    void tandIsExactAtKeyAngles() {
        assertEquals(0.0, WcsMath.tand(0.0));
        assertEquals(0.0, WcsMath.tand(180.0));
        assertEquals(0.0, WcsMath.tand(-180.0));
        assertEquals(0.0, WcsMath.tand(360.0));
        assertEquals(1.0, WcsMath.tand(45.0));
        assertEquals(-1.0, WcsMath.tand(-45.0));
        assertEquals(-1.0, WcsMath.tand(135.0));
        assertEquals(1.0, WcsMath.tand(-135.0));
        assertEquals(1.0, WcsMath.tand(225.0));
        assertEquals(-1.0, WcsMath.tand(-225.0));
        assertEquals(-1.0, WcsMath.tand(315.0));
        assertEquals(1.0, WcsMath.tand(-315.0));
    }

    @ParameterizedTest
    @CsvSource({
            "90.0000000001",
            "-179.9999999999",
            "30.5",
            "-17.5"
    })
    void sindSlowPathMatchesMathSinForFinite(final double input) {
        assertEquals(Math.sin(Math.toRadians(input)), WcsMath.sind(input), 1e-15);
    }

    @ParameterizedTest
    @CsvSource({
            "36000.0, 0.0",
            "-36000.0, 0.0",
            "1.8e11, 0.0"
    })
    void sindLargeMultiplesOf90Snap(final double input, final double expected) {
        assertEquals(expected, WcsMath.sind(input));
    }

    @ParameterizedTest
    @CsvSource({
            "90.0000000001",
            "-179.9999999999",
            "30.5",
            "-17.5"
    })
    void cosdSlowPathMatchesMathCosForFinite(final double input) {
        assertEquals(Math.cos(Math.toRadians(input)), WcsMath.cosd(input), 1e-15);
    }

    @ParameterizedTest
    @CsvSource({
            "36000.0, 1.0",
            "-36000.0, 1.0",
            "1.8e11, 1.0"
    })
    void cosdLargeMultiplesOf90Snap(final double input, final double expected) {
        assertEquals(expected, WcsMath.cosd(input));
    }

    @ParameterizedTest
    @CsvSource({
            "30.5",
            "-17.5",
            "60.123",
            "120.0000000001"
    })
    void tandSlowPathMatchesMathTanForFinite(final double input) {
        assertEquals(Math.tan(Math.toRadians(input)), WcsMath.tand(input), 1e-15);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void sindNonFiniteReturnsNaN(final double input) {
        assertTrue(Double.isNaN(WcsMath.sind(input)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void cosdNonFiniteReturnsNaN(final double input) {
        assertTrue(Double.isNaN(WcsMath.cosd(input)));
    }

    @Test
    void tandReturnsLargeFiniteAt90And270() {
        assertTrue(Double.isFinite(WcsMath.tand(90.0)));
        assertTrue(Math.abs(WcsMath.tand(90.0)) > 1.0e15);
        assertTrue(Double.isFinite(WcsMath.tand(-90.0)));
        assertTrue(Math.abs(WcsMath.tand(-90.0)) > 1.0e15);
        assertTrue(Double.isFinite(WcsMath.tand(270.0)));
        assertTrue(Math.abs(WcsMath.tand(270.0)) > 1.0e15);
        assertTrue(Double.isFinite(WcsMath.tand(-270.0)));
        assertTrue(Math.abs(WcsMath.tand(-270.0)) > 1.0e15);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void tandNonFiniteReturnsNaN(final double input) {
        assertTrue(Double.isNaN(WcsMath.tand(input)));
    }

    @Test
    void asindClampsValuesJustAbovePositiveOne() {
        assertEquals(90.0, WcsMath.asind(1.0 + 1e-14));
    }

    @Test
    void asindClampsValuesJustBelowNegativeOne() {
        assertEquals(-90.0, WcsMath.asind(-1.0 - 1e-14));
    }

    @Test
    void asindReturnsNaNForValuesWellOutsideUnitRange() {
        assertTrue(Double.isNaN(WcsMath.asind(1.5)));
        assertTrue(Double.isNaN(WcsMath.asind(-1.5)));
    }

    @Test
    void asindReturnsExactlyAtUnitBoundaries() {
        assertEquals(90.0, WcsMath.asind(1.0));
        assertEquals(-90.0, WcsMath.asind(-1.0));
        assertEquals(0.0, WcsMath.asind(0.0));
    }

    @Test
    void asindReturnsNaNForNaN() {
        assertTrue(Double.isNaN(WcsMath.asind(Double.NaN)));
    }

    @Test
    void sindReturnsNaNForNonFinite() {
        assertTrue(Double.isNaN(WcsMath.sind(Double.NaN)));
        assertTrue(Double.isNaN(WcsMath.sind(Double.POSITIVE_INFINITY)));
    }

    @Test
    void cosdReturnsNaNForNonFinite() {
        assertTrue(Double.isNaN(WcsMath.cosd(Double.NaN)));
        assertTrue(Double.isNaN(WcsMath.cosd(Double.POSITIVE_INFINITY)));
    }

    @Test
    void tandReturnsNaNForNonFinite() {
        assertTrue(Double.isNaN(WcsMath.tand(Double.NaN)));
        assertTrue(Double.isNaN(WcsMath.tand(Double.POSITIVE_INFINITY)));
    }

    @Test
    void atan2dZeroYShortcutNonNegativeXReturnsZero() {
        assertEquals(0.0, WcsMath.atan2d(0.0, 1.0));
        assertEquals(0.0, WcsMath.atan2d(0.0, 0.0));
    }

    @Test
    void atan2dZeroYShortcutNegativeXReturnsOneEighty() {
        assertEquals(180.0, WcsMath.atan2d(0.0, -1.0));
    }

    @Test
    void atan2dZeroXShortcutReturnsPlusOrMinusNinety() {
        assertEquals(90.0, WcsMath.atan2d(1.0, 0.0));
        assertEquals(-90.0, WcsMath.atan2d(-1.0, 0.0));
    }

    @Test
    void atan2dPropagatesNaNFromX() {
        assertTrue(Double.isNaN(WcsMath.atan2d(0.0, Double.NaN)));
    }

    @Test
    void atan2dPropagatesNaNFromY() {
        assertTrue(Double.isNaN(WcsMath.atan2d(Double.NaN, 0.0)));
    }

    @Test
    void acosdReturnsExactAtUnitBoundaries() {
        assertEquals(0.0, WcsMath.acosd(1.0));
        assertEquals(180.0, WcsMath.acosd(-1.0));
        assertEquals(90.0, WcsMath.acosd(0.0));
    }

    @Test
    void acosdClampsValuesJustAbovePositiveOne() {
        assertEquals(0.0, WcsMath.acosd(1.0 + 1e-11));
    }

    @Test
    void acosdClampsValuesJustBelowNegativeOne() {
        assertEquals(180.0, WcsMath.acosd(-1.0 - 1e-11));
    }

    @Test
    void acosdReturnsNaNForValuesWellOutsideUnitRange() {
        assertTrue(Double.isNaN(WcsMath.acosd(1.5)));
        assertTrue(Double.isNaN(WcsMath.acosd(-1.5)));
    }

    @Test
    void acosdReturnsNaNForNaN() {
        assertTrue(Double.isNaN(WcsMath.acosd(Double.NaN)));
    }

    @Test
    void atan2dGeneralQuadrants() {
        assertEquals(45.0, WcsMath.atan2d(1.0, 1.0), 1e-15);
        assertEquals(-135.0, WcsMath.atan2d(-1.0, -1.0), 1e-15);
        assertEquals(135.0, WcsMath.atan2d(1.0, -1.0), 1e-15);
        assertEquals(-45.0, WcsMath.atan2d(-1.0, 1.0), 1e-15);
    }

    @Test
    void tandValuesGreaterThan360() {
        assertEquals(1.0, WcsMath.tand(405.0));
        assertEquals(0.0, WcsMath.tand(540.0));
    }

    @Test
    void asindClampsJustBelowToleranceBoundary() {
        assertEquals(90.0, WcsMath.asind(1.0 + 9e-11));
    }

    @Test
    void asindReturnsNaNAtExactToleranceBoundary() {
        assertTrue(Double.isNaN(WcsMath.asind(1.0 + 1e-10)));
    }

    @Test
    void asindReturnsNaNBeyondToleranceBoundary() {
        assertTrue(Double.isNaN(WcsMath.asind(1.0 + 2e-10)));
    }

    @Test
    void sindNegativeLargeMultipleOf90Snaps() {
        assertEquals(0.0, WcsMath.sind(-1.8e11));
    }

    @Test
    void cosdNegativeLargeMultipleOf90Snaps() {
        assertEquals(1.0, WcsMath.cosd(-1.8e11));
    }
}
