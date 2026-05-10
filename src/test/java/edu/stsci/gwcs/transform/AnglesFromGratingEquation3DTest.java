package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class AnglesFromGratingEquation3DTest {
    @Test
    void testEvaluate() {
        final AnglesFromGratingEquation3D transform = new AnglesFromGratingEquation3D(20000, -1);
        final double wavelength = 2e-6;
        final double alphaIn = 1.745e-6;
        final double betaIn = 1.745e-6;

        final double[] outputs = transform.evaluate(wavelength, alphaIn, betaIn);

        final double expectedAlphaOut = -20000 * (-1) * wavelength + alphaIn;
        final double expectedBetaOut = -betaIn;
        final double expectedGammaOut = Math.sqrt(1.0 - expectedAlphaOut * expectedAlphaOut - expectedBetaOut * expectedBetaOut);

        assertEquals(expectedAlphaOut, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(expectedBetaOut, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expectedGammaOut, outputs[2], DOUBLE_TOLERANCE);
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
}
