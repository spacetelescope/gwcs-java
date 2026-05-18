package edu.stsci.gwcs.transform.spectroscopy;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class Snell3DTest {
    @Test
    void testRefraction() {
        final Snell3D transform = new Snell3D();
        final double[] outputs = transform.evaluate(1.5, 0.3, 0.4, 0.0);

        assertEquals(0.2, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.26666666666666666, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(0.9428090415820634, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testNoRefraction() {
        final Snell3D transform = new Snell3D();
        final double[] outputs = transform.evaluate(1.0, 0.3, 0.4, 0.0);

        assertEquals(0.3, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.4, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(0.8660254037844386, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testTotalInternalReflection() {
        final Snell3D transform = new Snell3D();
        final double[] outputs = transform.evaluate(0.5, 0.9, 0.9, 0.0);
        assertTrue(Double.isNaN(outputs[2]));
    }

    @Test
    void testZeroRefractiveIndexThrows() {
        final Snell3D transform = new Snell3D();
        assertThrows(IllegalArgumentException.class, () -> transform.evaluate(0.0, 0.3, 0.4, 0.0));
    }

    @Test
    void testInputOutputCount() {
        final Snell3D transform = new Snell3D();
        assertEquals(4, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final Snell3D transform = new Snell3D();
        assertFalse(transform.hasInverse());
        assertThrows(UnsupportedOperationException.class, transform::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Snell3D transform = new Snell3D();
        final double[] sampleInputs = {1.5, 0.3, 0.4, 0.0};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 1.5, 0.3, 0.4, 0.0, 99.0};
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
