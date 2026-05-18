package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class ConstantTest {
    @Test
    void evaluateWithTwoInputs() {
        final Constant constant = new Constant(2, 42.0);
        final double[] outputs = constant.evaluate(1.0, 2.0);
        assertEquals(42.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void evaluateWithOneInputReturnsZero() {
        final Constant constant = new Constant(1, 0.0);
        final double[] outputs = constant.evaluate(999.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void inputAndOutputCounts() {
        final Constant constant = new Constant(3, 1.0);
        assertEquals(3, constant.getInputCount());
        assertEquals(1, constant.getOutputCount());
    }

    @Test
    void negativeInputCount() {
        assertThrows(IllegalArgumentException.class, () -> new Constant(-1, 1.0));
    }

    @Test
    void zeroInputCount() {
        assertThrows(IllegalArgumentException.class, () -> new Constant(0, 1.0));
    }

    @Test
    void nanValueIsAccepted() {
        final Constant constant = new Constant(1, Double.NaN);
        final double[] outputs = constant.evaluate(5.0);
        assertTrue(Double.isNaN(outputs[0]));
    }

    @Test
    void infinityValueIsAccepted() {
        final Constant constant = new Constant(1, Double.POSITIVE_INFINITY);
        final double[] outputs = constant.evaluate(5.0);
        assertEquals(Double.POSITIVE_INFINITY, outputs[0]);
    }

    @Test
    void hasInverseReturnsFalse() {
        final Constant constant = new Constant(1, 1.0);
        assertFalse(constant.hasInverse());
    }

    @Test
    void getInverseThrows() {
        final Constant constant = new Constant(1, 1.0);
        assertThrows(UnsupportedOperationException.class, constant::getInverse);
    }

    @Test
    void evaluateWithWrongInputCount() {
        final Constant constant = new Constant(2, 42.0);
        assertThrows(IllegalArgumentException.class, () -> constant.evaluate(1.0));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Constant constant = new Constant(2, 42.0);
        final double[] sampleInputs = {1.0, 2.0};
        final double[] expected = constant.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 1.0, 2.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        constant.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
