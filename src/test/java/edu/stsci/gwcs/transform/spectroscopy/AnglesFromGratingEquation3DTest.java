package edu.stsci.gwcs.transform.spectroscopy;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class AnglesFromGratingEquation3DTest {
    @Test
    void testEvaluate() {
        final AnglesFromGratingEquation3D transform = new AnglesFromGratingEquation3D(20000, -1);

        final double[] outputs = transform.evaluate(2e-6, 1.745e-6, 1.745e-6);

        assertEquals(0.040001745, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(-1.745e-6, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(0.9991996098847867, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testTotalInternalReflection() {
        final AnglesFromGratingEquation3D transform = new AnglesFromGratingEquation3D(1000000, 1);
        final double[] outputs = transform.evaluate(1.0, 0.0, 0.0);
        assertTrue(Double.isNaN(outputs[2]));
    }

    @Test
    void testInputOutputCount() {
        final AnglesFromGratingEquation3D transform = new AnglesFromGratingEquation3D(20000, -1);
        assertEquals(3, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void testZeroSpectralOrderThrows() {
        assertThrows(IllegalArgumentException.class, () -> new AnglesFromGratingEquation3D(20000, 0));
    }

    @Test
    void testHasNoInverse() {
        final AnglesFromGratingEquation3D transform = new AnglesFromGratingEquation3D(20000, -1);
        assertFalse(transform.hasInverse());
        assertThrows(UnsupportedOperationException.class, transform::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final AnglesFromGratingEquation3D transform = new AnglesFromGratingEquation3D(20000, -1);
        final double[] sampleInputs = {2e-6, 1.745e-6, 1.745e-6};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, sampleInputs[0], sampleInputs[1], sampleInputs[2], 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(expected[2], outputs[3], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[4]);
        assertEquals(77.0, outputs[5]);
    }
}
