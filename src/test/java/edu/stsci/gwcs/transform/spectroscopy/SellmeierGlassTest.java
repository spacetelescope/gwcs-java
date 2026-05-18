package edu.stsci.gwcs.transform.spectroscopy;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class SellmeierGlassTest {
    @Test
    void testKnownGlass() {
        final double[] b = {0.58339748, 0.46085267, 3.8915394};
        final double[] c = {0.00252643, 0.010078333, 1200.556};
        final SellmeierGlass transform = new SellmeierGlass(b, c);

        final double[] outputs = transform.evaluate(2.0);
        assertEquals(1.425753771370878, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testVacuum() {
        final double[] b = {0.0, 0.0, 0.0};
        final double[] c = {0.00252643, 0.010078333, 1200.556};
        final SellmeierGlass transform = new SellmeierGlass(b, c);

        final double[] outputs = transform.evaluate(2.0);
        assertEquals(1.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testResonanceWavelength() {
        final double[] b = {0.58339748, 0.46085267, 3.8915394};
        final double[] c = {0.00252643, 0.010078333, 1200.556};
        final SellmeierGlass transform = new SellmeierGlass(b, c);
        final double[] outputs = transform.evaluate(Math.sqrt(c[0]));
        assertTrue(Double.isInfinite(outputs[0]));
    }

    @Test
    void testInputOutputCount() {
        final double[] b = {0.58339748, 0.46085267, 3.8915394};
        final double[] c = {0.00252643, 0.010078333, 1200.556};
        final SellmeierGlass transform = new SellmeierGlass(b, c);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final double[] b = {0.58339748, 0.46085267, 3.8915394};
        final double[] c = {0.00252643, 0.010078333, 1200.556};
        final SellmeierGlass transform = new SellmeierGlass(b, c);
        assertFalse(transform.hasInverse());
        assertThrows(UnsupportedOperationException.class, transform::getInverse);
    }

    @Test
    void testInvalidBCoefficientsLength() {
        assertThrows(IllegalArgumentException.class, () -> new SellmeierGlass(new double[]{1.0, 2.0}, new double[]{1.0, 2.0, 3.0}));
    }

    @Test
    void testInvalidCCoefficientsLength() {
        assertThrows(IllegalArgumentException.class, () -> new SellmeierGlass(new double[]{1.0, 2.0, 3.0}, new double[]{1.0, 2.0}));
    }

    @Test
    void testNullCoefficients() {
        assertThrows(IllegalArgumentException.class, () -> new SellmeierGlass(null, new double[]{1.0, 2.0, 3.0}));
        assertThrows(IllegalArgumentException.class, () -> new SellmeierGlass(new double[]{1.0, 2.0, 3.0}, null));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final double[] b = {0.58339748, 0.46085267, 3.8915394};
        final double[] c = {0.00252643, 0.010078333, 1200.556};
        final SellmeierGlass transform = new SellmeierGlass(b, c);
        final double[] sampleInputs = {2.0};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 2.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
