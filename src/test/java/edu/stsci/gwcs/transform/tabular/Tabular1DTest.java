package edu.stsci.gwcs.transform.tabular;

import edu.stsci.gwcs.transform.tabular.Tabular1D.InterpolationMethod;
import edu.stsci.gwcs.transform.tabular.Tabular1D.OutOfBoundsMode;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class Tabular1DTest {
    @Test
    void testLinearInterpolation() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        assertEquals(3.0, tabular.evaluate(1.5)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonLinearInterpolation() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2}, new double[]{0, 1, 4});
        assertEquals(2.5, tabular.evaluate(1.5)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testExactPointInput() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        assertEquals(2.0, tabular.evaluate(1.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testBelowRangeErrorModeThrows() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.ERROR, Double.NaN);
        assertThrows(IllegalArgumentException.class, () -> tabular.evaluate(-1.0));
    }

    @Test
    void testAboveRangeErrorModeThrows() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.ERROR, Double.NaN);
        assertThrows(IllegalArgumentException.class, () -> tabular.evaluate(4.0));
    }

    @Test
    void twoArgConstructorDefaultsToErrorMode() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        assertEquals(OutOfBoundsMode.ERROR, tabular.getMode());
        assertThrows(IllegalArgumentException.class, () -> tabular.evaluate(-1.0));
        assertThrows(IllegalArgumentException.class, () -> tabular.evaluate(4.0));
    }

    @Test
    void twoArgConstructorDefaultsToLinearMethodAndNaNFill() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        assertEquals(InterpolationMethod.LINEAR, tabular.getMethod());
        assertTrue(Double.isNaN(tabular.getFillValue()));
    }

    @Test
    void constructorRejectsInfiniteFillValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(
                        new double[]{0, 1, 2}, new double[]{0, 1, 4},
                        OutOfBoundsMode.FILL, Double.POSITIVE_INFINITY));
    }

    @Test
    void testBelowRangeFillModeReturnsNaN() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.FILL, Double.NaN);
        assertTrue(Double.isNaN(tabular.evaluate(-1.0)[0]));
    }

    @Test
    void testBelowRangeFillModeReturnsFillValue() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.FILL, -999);
        assertEquals(-999, tabular.evaluate(-1.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testBelowRangeExtrapolatesUsingFirstTwoPoints() {
        // points: 0,1,2,3; values: 0,2,4,6 -> linear y = 2x
        // at x=-1, extrapolate using (0,0) and (1,2) -> -2
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.EXTRAPOLATE, Double.NaN);
        assertEquals(-2.0, tabular.evaluate(-1.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testAboveRangeExtrapolatesUsingLastTwoPoints() {
        // points: 0,1,2,3; values: 0,2,4,6 -> linear y = 2x
        // at x=5, extrapolate using (2,4) and (3,6) -> 10
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.EXTRAPOLATE, Double.NaN);
        assertEquals(10.0, tabular.evaluate(5.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testExtrapolateUsesPiecewiseSlopeAtEnds() {
        // points 0,1,2; values 0,1,4 -> slope at left (0->1) is 1, slope at right (1->2) is 3
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2}, new double[]{0, 1, 4},
                OutOfBoundsMode.EXTRAPOLATE, Double.NaN);
        assertEquals(-1.0, tabular.evaluate(-1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(7.0, tabular.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testUnsortedPointsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(new double[]{0, 2, 1, 3}, new double[]{0, 2, 4, 6}));
    }

    @Test
    void testMismatchedLengthsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(new double[]{0, 1, 2}, new double[]{0, 2, 4, 6}));
    }

    @Test
    void testSingleElementThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(new double[]{0}, new double[]{0}));
    }

    @Test
    void testInputOutputCount() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2}, new double[]{0, 1, 4});
        assertEquals(1, tabular.getInputCount());
        assertEquals(1, tabular.getOutputCount());
    }

    @Test
    void testInverseAvailableWhenValuesStrictlyIncreasing() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        assertTrue(tabular.hasInverse());
        final Tabular1D inverse = tabular.getInverse();
        assertEquals(1.5, inverse.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverseAvailableWhenValuesStrictlyDecreasing() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{6, 4, 2, 0});
        assertTrue(tabular.hasInverse());
        final Tabular1D inverse = tabular.getInverse();
        // At v=3, x should be 1.5
        assertEquals(1.5, inverse.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverseRejectedWhenValuesNonMonotonic() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 4, 2, 6});
        assertFalse(tabular.hasInverse());
        assertThrows(UnsupportedOperationException.class, tabular::getInverse);
    }

    @Test
    void testInverseRoundTrip() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        final Tabular1D inverse = tabular.getInverse();
        for (final double x : new double[]{0.0, 0.5, 1.25, 2.7, 3.0}) {
            final double v = tabular.evaluate(x)[0];
            assertEquals(x, inverse.evaluate(v)[0], DOUBLE_TOLERANCE);
        }
    }

    @Test
    void testInverseIsCached() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        assertSame(tabular.getInverse(), tabular.getInverse());
    }

    @Test
    void testNaNInPointsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(new double[]{0, Double.NaN, 2}, new double[]{0, 1, 4}));
    }

    @Test
    void testInfinityInPointsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(
                        new double[]{0, 1, Double.POSITIVE_INFINITY}, new double[]{0, 1, 4}));
    }

    @Test
    void constructorRejectsNaNValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(new double[]{0, 1, 2}, new double[]{0, Double.NaN, 4}));
    }

    @Test
    void constructorRejectsInfinityValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tabular1D(
                        new double[]{0, 1, 2}, new double[]{0, 1, Double.POSITIVE_INFINITY}));
    }

    @Test
    void testNaNInputReturnsNaN() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2}, new double[]{0, 1, 4});
        final double[] result = tabular.evaluate(Double.NaN);
        assertTrue(Double.isNaN(result[0]));
    }

    @Test
    void testInverseWithExtrapolateModeHandlesOutOfRange() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.EXTRAPOLATE, Double.NaN);
        final Tabular1D inverse = tabular.getInverse();
        assertEquals(-0.5, inverse.evaluate(-1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(4.0, inverse.evaluate(8.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverseWithFillModeReturnsFillValue() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.FILL, -999.0);
        final Tabular1D inverse = tabular.getInverse();
        assertEquals(-999.0, inverse.evaluate(-1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(-999.0, inverse.evaluate(7.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverseWithErrorModeThrows() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6},
                OutOfBoundsMode.ERROR, Double.NaN);
        final Tabular1D inverse = tabular.getInverse();
        assertThrows(IllegalArgumentException.class, () -> inverse.evaluate(-1.0));
        assertThrows(IllegalArgumentException.class, () -> inverse.evaluate(7.0));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Tabular1D tabular = new Tabular1D(
                new double[]{0, 1, 2, 3}, new double[]{0, 2, 4, 6});
        final double[] inputs = new double[]{99.0, 99.0, 1.5, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0};
        tabular.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(3.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
    }
}
