package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class Snell3DTest {
    @Test
    void testRefraction() {
        final Snell3D transform = new Snell3D();
        final double[] outputs = transform.evaluate(1.5, 0.3, 0.4, 0.0);

        assertEquals(0.2, outputs[0], DOUBLE_TOLERANCE);
        final double expectedBeta = 0.4 / 1.5;
        assertEquals(expectedBeta, outputs[1], DOUBLE_TOLERANCE);

        final double expectedGamma = Math.sqrt(1.0 - outputs[0] * outputs[0] - outputs[1] * outputs[1]);
        assertEquals(expectedGamma, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testNoRefraction() {
        final Snell3D transform = new Snell3D();
        final double alphaIn = 0.3;
        final double betaIn = 0.4;
        final double[] outputs = transform.evaluate(1.0, alphaIn, betaIn, 0.0);

        assertEquals(alphaIn, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(betaIn, outputs[1], DOUBLE_TOLERANCE);

        final double expectedGamma = Math.sqrt(1.0 - alphaIn * alphaIn - betaIn * betaIn);
        assertEquals(expectedGamma, outputs[2], DOUBLE_TOLERANCE);
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
}
