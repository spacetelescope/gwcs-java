package edu.stsci.gwcs.transform.rotation;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.RotateNative2Celestial
 * (Pix2Sky_RotateNative2Celestial / Sky2Pix_RotateNative2Celestial). Regenerate via the project tooling if tolerances drift.
 */
class RotateNative2CelestialTest {

    @Test
    void testInputOutputCount() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(80.0, -30.0, 180.0);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(80.0, -30.0, 180.0);
        assertTrue(transform.hasInverse());
    }

    @Test
    void testInverseType() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(80.0, -30.0, 180.0);
        assertInstanceOf(RotateCelestial2Native.class, transform.getInverse());
    }

    @Test
    void testNorthPoleIdentity() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(0.0, 90.0, 180.0);
        final double[] result = transform.evaluate(45.0, 30.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNorthPoleOrigin() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(0.0, 90.0, 180.0);
        final double[] result = transform.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testKnownValues() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(80.0, -30.0, 180.0);

        final double[] result1 = transform.evaluate(0.0, 0.0);
        assertEquals(260.0, result1[0], DOUBLE_TOLERANCE);
        assertEquals(-60.0, result1[1], DOUBLE_TOLERANCE);

        final double[] result2 = transform.evaluate(45.0, 30.0);
        assertEquals(158.29908049184620, result2[0], DOUBLE_TOLERANCE);
        assertEquals(-51.29080769669910, result2[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testOutputNormalization() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(80.0, -30.0, 180.0);
        final double[] result = transform.evaluate(-10.0, 5.0);
        assertTrue(result[0] >= 0.0 && result[0] < 360.0);
        assertEquals(282.6256822817, result[0], 1e-8);
    }

    @Test
    void testRoundTripN2CThenC2N() {
        final RotateNative2Celestial n2c = new RotateNative2Celestial(80.0, -30.0, 180.0);
        final RotateCelestial2Native c2n = new RotateCelestial2Native(80.0, -30.0, 180.0);

        final double[] celestial = n2c.evaluate(45.0, 30.0);
        final double[] recovered = c2n.evaluate(celestial);

        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripViaInverse() {
        final RotateNative2Celestial n2c = new RotateNative2Celestial(80.0, -30.0, 180.0);
        final Transform inverse = n2c.getInverse();

        final double[] celestial = n2c.evaluate(120.0, -45.0);
        final double[] recovered = inverse.evaluate(celestial);

        assertEquals(120.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-45.0, recovered[1], DOUBLE_TOLERANCE);
    }

    // Reference values from astropy.modeling.rotations.RotateNative2Celestial (astropy 7.2.0).
    private static final double ASTROPY_TOLERANCE = 1.0e-10;

    @Test
    void astropyReferenceAtNativePole() {
        final RotateNative2Celestial t = new RotateNative2Celestial(45.0, 30.0, 180.0);
        final double[] out = t.evaluate(0.0, 90.0);
        assertEquals(45.0, out[0], ASTROPY_TOLERANCE);
        assertEquals(30.0, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyReferenceOffPole() {
        final RotateNative2Celestial t = new RotateNative2Celestial(45.0, 30.0, 180.0);
        final double[] out = t.evaluate(10.0, 20.0);
        assertEquals(57.134676149151844, out[0], ASTROPY_TOLERANCE);
        assertEquals(-39.081425761316765, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyReferenceHighLatitude() {
        final RotateNative2Celestial t = new RotateNative2Celestial(180.0, 60.0, 180.0);
        final double[] out = t.evaluate(90.0, 45.0);
        assertEquals(243.43494882292202, out[0], ASTROPY_TOLERANCE);
        assertEquals(37.76124390703503, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyReferenceSouthPoleCrval() {
        final RotateNative2Celestial t = new RotateNative2Celestial(0.0, -90.0, 180.0);
        final double[] out = t.evaluate(0.0, 0.0);
        assertEquals(180.0, out[0], ASTROPY_TOLERANCE);
        assertEquals(0.0, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void testNonDefaultLonPoleRoundTrip() {
        final RotateNative2Celestial n2c = new RotateNative2Celestial(45.0, 30.0, 150.0);
        final Transform inverse = n2c.getInverse();

        final double[] celestial = n2c.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(celestial);

        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNearLonZeroBoundary() {
        final RotateNative2Celestial n2c = new RotateNative2Celestial(0.5, 45.0, 180.0);
        final Transform inverse = n2c.getInverse();
        final double[] celestial = n2c.evaluate(179.5, 10.0);
        final double[] recovered = inverse.evaluate(celestial);
        assertEquals(179.5, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNearLon360Boundary() {
        final RotateNative2Celestial n2c = new RotateNative2Celestial(359.5, 45.0, 180.0);
        final Transform inverse = n2c.getInverse();
        final double[] celestial = n2c.evaluate(180.0, 10.0);
        final double[] recovered = inverse.evaluate(celestial);
        assertEquals(180.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final RotateNative2Celestial transform = new RotateNative2Celestial(80.0, -30.0, 180.0);
        final double[] sampleInputs = {45.0, 30.0};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 45.0, 30.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
