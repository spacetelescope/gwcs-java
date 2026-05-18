package edu.stsci.gwcs.transform.spectroscopy;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class SellmeierZemaxTest {
    private static final double[] B = {0.58339748, 0.46085267, 3.8915394};
    private static final double[] C = {0.00252643, 0.010078333, 1200.556};
    private static final double[] D = {-2.66e-05, 0.0, 0.0};
    private static final double[] E = {-1.08e-07, 0.0, 0.17};

    @Test
    void testReferenceConditionsReduceToSellmeier() {
        final double tempK = 296.15;
        final double pressure = 1.0;
        final SellmeierZemax zemax = new SellmeierZemax(tempK, tempK, pressure, pressure, B, C, D, E);
        final SellmeierGlass glass = new SellmeierGlass(B, C);

        final double wavelength = 2.0;
        final double[] zemaxResult = zemax.evaluate(wavelength);
        final double[] glassResult = glass.evaluate(wavelength);

        // When temp=refTemp and pressure=refPressure, delta=0 so delnabs=0.
        // The air correction factors are also equal so they cancel.
        assertEquals(glassResult[0], zemaxResult[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testTemperatureDependence() {
        final double refTempK = 296.15;
        final double pressure = 1.0;
        final SellmeierZemax atRef = new SellmeierZemax(refTempK, refTempK, pressure, pressure, B, C, D, E);
        final SellmeierZemax atHigher = new SellmeierZemax(refTempK + 10.0, refTempK, pressure, pressure, B, C, D, E);

        final double wavelength = 2.0;
        final double[] refResult = atRef.evaluate(wavelength);
        final double[] higherResult = atHigher.evaluate(wavelength);

        assertNotEquals(refResult[0], higherResult[0], 1e-15);
    }

    @Test
    void testNonZeroDeltaSpecificValue() {
        final double tempK = 306.15;
        final double refTempK = 296.15;
        final double pressure = 1.0;
        final SellmeierZemax zemax = new SellmeierZemax(tempK, refTempK, pressure, pressure, B, C, D, E);
        final double[] result = zemax.evaluate(2.0);
        assertEquals(1.425669613210338, result[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final SellmeierZemax transform = new SellmeierZemax(296.15, 296.15, 1.0, 1.0, B, C, D, E);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final SellmeierZemax transform = new SellmeierZemax(296.15, 296.15, 1.0, 1.0, B, C, D, E);
        assertFalse(transform.hasInverse());
        assertThrows(UnsupportedOperationException.class, transform::getInverse);
    }

    @Test
    void testInvalidCoefficients() {
        assertThrows(IllegalArgumentException.class, () ->
                new SellmeierZemax(296.15, 296.15, 1.0, 1.0, new double[]{1.0, 2.0}, C, D, E));
        assertThrows(IllegalArgumentException.class, () ->
                new SellmeierZemax(296.15, 296.15, 1.0, 1.0, B, new double[]{1.0}, D, E));
        assertThrows(IllegalArgumentException.class, () ->
                new SellmeierZemax(296.15, 296.15, 1.0, 1.0, B, C, null, E));
        assertThrows(IllegalArgumentException.class, () ->
                new SellmeierZemax(296.15, 296.15, 1.0, 1.0, B, C, D, new double[]{1.0, 2.0, 3.0, 4.0}));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final SellmeierZemax transform = new SellmeierZemax(296.15, 296.15, 1.0, 1.0, B, C, D, E);
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
