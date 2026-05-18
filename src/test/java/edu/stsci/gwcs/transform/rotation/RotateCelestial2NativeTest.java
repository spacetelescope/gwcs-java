package edu.stsci.gwcs.transform.rotation;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.RotateCelestial2Native
 * (Pix2Sky_RotateCelestial2Native / Sky2Pix_RotateCelestial2Native). Regenerate via the project tooling if tolerances drift.
 */
class RotateCelestial2NativeTest {

    @Test
    void testInputOutputCount() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(80.0, -30.0, 180.0);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(80.0, -30.0, 180.0);
        assertTrue(transform.hasInverse());
    }

    @Test
    void testInverseType() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(80.0, -30.0, 180.0);
        assertInstanceOf(RotateNative2Celestial.class, transform.getInverse());
    }

    @Test
    void testNorthPoleIdentity() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(0.0, 90.0, 180.0);
        final double[] result = transform.evaluate(45.0, 30.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testKnownValues() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(80.0, -30.0, 180.0);

        final double[] result = transform.evaluate(45.0, 30.0);
        assertEquals(212.2354315938901, result[0], DOUBLE_TOLERANCE);
        assertEquals(21.368449467200, result[1], 1e-8);
    }

    @Test
    void testRoundTripC2NThenN2C() {
        final RotateCelestial2Native c2n = new RotateCelestial2Native(80.0, -30.0, 180.0);
        final RotateNative2Celestial n2c = new RotateNative2Celestial(80.0, -30.0, 180.0);

        final double[] nativeCoords = c2n.evaluate(120.0, -45.0);
        final double[] recovered = n2c.evaluate(nativeCoords);

        assertEquals(120.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-45.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripViaInverse() {
        final RotateCelestial2Native c2n = new RotateCelestial2Native(80.0, -30.0, 180.0);
        final Transform inverse = c2n.getInverse();

        final double[] nativeCoords = c2n.evaluate(200.0, 15.0);
        final double[] recovered = inverse.evaluate(nativeCoords);

        assertEquals(200.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(15.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testOutputNormalization() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(80.0, -30.0, 180.0);
        final double[] result = transform.evaluate(45.0, 30.0);
        assertTrue(result[0] >= 0.0 && result[0] < 360.0);
    }

    // Reference values from astropy.modeling.rotations.RotateCelestial2Native (astropy 7.2.0).
    private static final double ASTROPY_TOLERANCE = 1.0e-10;

    @Test
    void astropyReferenceAtCrval() {
        // At sky = (lon, lat), native theta must equal 90° exactly.
        // phi is degenerate at the pole; astropy and Java may pick different
        // representative longitudes for the same point.
        final RotateCelestial2Native t = new RotateCelestial2Native(45.0, 30.0, 180.0);
        final double[] out = t.evaluate(45.0, 30.0);
        assertEquals(90.0, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyReferenceNearCrval() {
        final RotateCelestial2Native t = new RotateCelestial2Native(45.0, 30.0, 180.0);
        final double[] out = t.evaluate(50.0, 35.0);
        assertEquals(141.17424771182152, out[0], ASTROPY_TOLERANCE);
        assertEquals(83.46131281784412, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyReferenceHighLatitude() {
        final RotateCelestial2Native t = new RotateCelestial2Native(180.0, 60.0, 180.0);
        final double[] out = t.evaluate(175.0, 55.0);
        assertEquals(329.6172535478263, out[0], ASTROPY_TOLERANCE);
        assertEquals(84.32764893913999, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyReferencePoleCrval() {
        // crval at north pole: the rotation collapses to identity in latitude.
        final RotateCelestial2Native t = new RotateCelestial2Native(0.0, 90.0, 180.0);
        final double[] out = t.evaluate(100.0, 40.0);
        assertEquals(100.0, out[0], ASTROPY_TOLERANCE);
        assertEquals(40.0, out[1], ASTROPY_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final RotateCelestial2Native transform = new RotateCelestial2Native(80.0, -30.0, 180.0);
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
