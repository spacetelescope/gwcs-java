package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class ShiftTest {
    @Test
    void testEvaluate() {
        final Shift shift = new Shift(10.0);
        final double[] outputs = shift.evaluate(5.0);
        assertEquals(15.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void nonFiniteOffsetThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Shift(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Shift(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testNegativeOffset() {
        final Shift shift = new Shift(-3.0);
        final double[] outputs = shift.evaluate(5.0);
        assertEquals(2.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Shift shift = new Shift(1.0);
        assertEquals(1, shift.getInputCount());
        assertEquals(1, shift.getOutputCount());
    }

    @Test
    void testRoundTrip() {
        final Shift shift = new Shift(42.0);
        assertTrue(shift.hasInverse());

        final Transform inverse = shift.getInverse();
        final double[] intermediate = shift.evaluate(7.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(7.0, recovered[0], DOUBLE_TOLERANCE);
    }
}
