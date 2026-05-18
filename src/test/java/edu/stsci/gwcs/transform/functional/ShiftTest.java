package edu.stsci.gwcs.transform.functional;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

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
    void testZeroOffset() {
        final Shift shift = new Shift(0.0);
        final double[] outputs = shift.evaluate(42.0);
        assertEquals(42.0, outputs[0], DOUBLE_TOLERANCE);
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

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Shift shift = new Shift(10.0);
        final double[] sampleInputs = {5.0};
        final double[] expected = shift.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 5.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        shift.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
