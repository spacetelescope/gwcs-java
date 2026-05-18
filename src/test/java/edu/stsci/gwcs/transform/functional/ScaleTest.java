package edu.stsci.gwcs.transform.functional;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

class ScaleTest {
    @Test
    void testEvaluate() {
        final Scale scale = new Scale(3.0);
        final double[] outputs = scale.evaluate(5.0);
        assertEquals(15.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testNegativeFactor() {
        final Scale scale = new Scale(-2.0);
        final double[] outputs = scale.evaluate(4.0);
        assertEquals(-8.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void nonFiniteFactorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Scale(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Scale(Double.POSITIVE_INFINITY));
    }

    @Test
    void testNegativeZeroHasNoInverse() {
        final Scale scale = new Scale(-0.0);
        assertFalse(scale.hasInverse());
    }

    @Test
    void testMinValueHasNoInverse() {
        final Scale scale = new Scale(Double.MIN_VALUE);
        assertFalse(scale.hasInverse());
    }

    @Test
    void testInverseIsScale() {
        final Scale scale = new Scale(5.0);
        assertInstanceOf(Scale.class, scale.getInverse());
    }

    @Test
    void testZeroFactor() {
        final Scale scale = new Scale(0.0);
        assertEquals(1, scale.getInputCount());
        assertEquals(1, scale.getOutputCount());
        assertFalse(scale.hasInverse());
        assertThrows(UnsupportedOperationException.class, scale::getInverse);
    }

    @Test
    void testRoundTrip() {
        final Scale scale = new Scale(7.5);
        assertTrue(scale.hasInverse());

        final Transform inverse = scale.getInverse();
        final double[] intermediate = scale.evaluate(12.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(12.0, recovered[0], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Scale scale = new Scale(3.0);
        final double[] sampleInputs = {5.0};
        final double[] expected = scale.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 5.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        scale.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
