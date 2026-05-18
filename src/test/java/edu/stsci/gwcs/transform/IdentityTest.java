package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class IdentityTest {
    @Test
    void testEvaluate1D() {
        final Identity identity = new Identity(1);
        final double[] outputs = identity.evaluate(42.0);
        assertEquals(42.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testEvaluate3D() {
        final Identity identity = new Identity(3);
        assertEquals(3, identity.getInputCount());
        assertEquals(3, identity.getOutputCount());

        final double[] inputs = {1.0, 2.0, 3.0};
        final double[] outputs = new double[3];
        identity.evaluate(inputs, 0, outputs, 0);

        assertEquals(1.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(2.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(3.0, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithOffset() {
        final Identity identity = new Identity(2);
        final double[] inputs = {99.0, 1.0, 2.0, 99.0};
        final double[] outputs = new double[4];
        identity.evaluate(inputs, 1, outputs, 2);

        assertEquals(1.0, outputs[2], DOUBLE_TOLERANCE);
        assertEquals(2.0, outputs[3], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverseIsSelf() {
        final Identity identity = new Identity(2);
        assertTrue(identity.hasInverse());
        assertSame(identity, identity.getInverse());
    }

    @Test
    void testNaNPassThrough() {
        final Identity identity = new Identity(2);
        final double[] outputs = identity.evaluate(Double.NaN, 3.0);
        assertTrue(Double.isNaN(outputs[0]));
        assertEquals(3.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsZeroDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Identity(0));
        assertThrows(IllegalArgumentException.class, () -> new Identity(-1));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Identity identity = new Identity(2);
        final double[] sampleInputs = {3.0, 4.0};
        final double[] expected = identity.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 3.0, 4.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        identity.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
