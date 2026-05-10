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
    void zeroInputCount() {
        assertThrows(IllegalArgumentException.class, () -> new Constant(0, 1.0));
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
}
