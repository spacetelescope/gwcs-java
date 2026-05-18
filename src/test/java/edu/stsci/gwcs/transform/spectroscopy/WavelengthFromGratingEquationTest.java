package edu.stsci.gwcs.transform.spectroscopy;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class WavelengthFromGratingEquationTest {
    @Test
    void testEvaluate() {
        final WavelengthFromGratingEquation transform = new WavelengthFromGratingEquation(20000, -1);
        final double[] outputs = transform.evaluate(0.001, 0.001);
        assertEquals(-1e-7, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testEvaluatePositiveOrder() {
        final WavelengthFromGratingEquation transform = new WavelengthFromGratingEquation(1000, 1);
        final double[] outputs = transform.evaluate(0.5, 0.3);
        assertEquals(8e-4, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testZeroSpectralOrderThrows() {
        assertThrows(IllegalArgumentException.class, () -> new WavelengthFromGratingEquation(20000, 0));
    }

    @Test
    void testZeroGrooveDensityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new WavelengthFromGratingEquation(0, 1));
    }

    @Test
    void testInputOutputCount() {
        final WavelengthFromGratingEquation transform = new WavelengthFromGratingEquation(1000, 1);
        assertEquals(2, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final WavelengthFromGratingEquation transform = new WavelengthFromGratingEquation(1000, 1);
        assertFalse(transform.hasInverse());
        assertThrows(UnsupportedOperationException.class, transform::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final WavelengthFromGratingEquation transform = new WavelengthFromGratingEquation(1000, 1);
        final double[] sampleInputs = {0.5, 0.3};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 0.5, 0.3, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
