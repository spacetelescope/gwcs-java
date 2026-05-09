package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

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
}
