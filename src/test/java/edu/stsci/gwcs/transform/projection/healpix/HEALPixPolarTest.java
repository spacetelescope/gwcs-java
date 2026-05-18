package edu.stsci.gwcs.transform.projection.healpix;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.HEALPixPolar
 * (Pix2Sky_HEALPixPolar / Sky2Pix_HEALPixPolar). Regenerate via the project tooling if tolerances drift.
 */
class HEALPixPolarTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new HEALPixPolar();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{30.0, 20.0},
                new double[]{60.0, 70.0},
                new double[]{-45.0, -60.0}
        );
    }

    @Test
    void pix2SkyOrigin() {
        final HEALPixPolar projection = new HEALPixPolar();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNorthPole() {
        final HEALPixPolar forward = new HEALPixPolar();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 90.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNonTrivial() {
        final HEALPixPolar forward = new HEALPixPolar();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 20.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNonTrivial2() {
        final HEALPixPolar forward = new HEALPixPolar();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(60.0, 70.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(60.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(70.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripSouth() {
        final HEALPixPolar forward = new HEALPixPolar();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, -50.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-50.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyReturnsNaNForOutOfBounds() {
        final HEALPixPolar projection = new HEALPixPolar();
        final double[] result = projection.evaluate(1000.0, 1000.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void hasInverse() {
        final HEALPixPolar projection = new HEALPixPolar();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final HEALPixPolar projection = new HEALPixPolar();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void roundTripQuadrant1() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(-5.0, 5.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(-5.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripQuadrant2() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(-5.0, -5.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(-5.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripQuadrant3() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(5.0, -5.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(5.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripQuadrant4() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(5.0, 5.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(5.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripAxisAlignedPositiveY() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(0.0, 5.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripAxisAlignedNegativeY() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(0.0, -5.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripAxisAlignedNegativeX() {
        final HEALPixPolar forward = new HEALPixPolar();
        final double[] sky = forward.evaluate(-5.0, 0.0);
        final Transform inverse = forward.getInverse();
        final double[] recovered = inverse.evaluate(sky);
        assertEquals(-5.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, recovered[1], DOUBLE_TOLERANCE);
    }

    // Reference values from astropy 7.2.0 Pix2Sky_HEALPixPolar / Sky2Pix_HEALPixPolar.
    private static final double ASTROPY_TOLERANCE = 1.0e-10;

    @Test
    void astropyPix2SkyOrigin() {
        final HEALPixPolar p = new HEALPixPolar();
        final double[] sky = p.evaluate(0.0, 0.0);
        assertEquals(0.0, sky[0], ASTROPY_TOLERANCE);
        assertEquals(90.0, sky[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyPix2SkyNearOrigin() {
        final HEALPixPolar p = new HEALPixPolar();
        final double[] sky = p.evaluate(10.0, 20.0);
        assertEquals(150.0, sky[0], ASTROPY_TOLERANCE);
        assertEquals(67.80839343372722, sky[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyPix2SkyNegativeY() {
        final HEALPixPolar p = new HEALPixPolar();
        final double[] sky = p.evaluate(45.0, -60.0);
        assertEquals(34.39339828220179, sky[0], ASTROPY_TOLERANCE);
        assertEquals(13.496705450724436, sky[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyPix2SkyNegativeX() {
        final HEALPixPolar p = new HEALPixPolar();
        final double[] sky = p.evaluate(-30.0, 45.0);
        assertEquals(-145.6066017177982, sky[0], ASTROPY_TOLERANCE);
        assertEquals(33.20656739248005, sky[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropyPix2SkyOutsideEquatorialBand() {
        final HEALPixPolar p = new HEALPixPolar();
        final double[] sky = p.evaluate(60.0, 80.0);
        assertEquals(149.14213562373095, sky[0], ASTROPY_TOLERANCE);
        assertEquals(-7.657929948135882, sky[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropySky2PixNearPole() {
        final HEALPixPolar p = new HEALPixPolar(Direction.SKY2PIX);
        final double[] pix = p.evaluate(45.0, 60.0);
        assertEquals(20.17294812378121, pix[0], ASTROPY_TOLERANCE);
        assertEquals(-20.17294812378121, pix[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropySky2PixSouthernHemisphere() {
        final HEALPixPolar p = new HEALPixPolar(Direction.SKY2PIX);
        final double[] pix = p.evaluate(60.0, -80.0);
        assertEquals(122.75047537526812, pix[0], ASTROPY_TOLERANCE);
        assertEquals(-118.22173013695773, pix[1], ASTROPY_TOLERANCE);
    }

    @Test
    void astropySky2PixEquatorialBand() {
        final HEALPixPolar p = new HEALPixPolar(Direction.SKY2PIX);
        final double[] pix = p.evaluate(15.0, 30.0);
        assertEquals(18.561553006146877, pix[0], ASTROPY_TOLERANCE);
        assertEquals(-60.98795987733972, pix[1], ASTROPY_TOLERANCE);
    }

    @Test
    void equatorialPolarBoundaryRoundTrips() {
        final HEALPixPolar forward = new HEALPixPolar();
        final Transform inverse = forward.getInverse();
        for (final double eta : new double[]{45.0, 45.0 + 1e-8, 45.0 - 1e-8, -45.0, -45.0 + 1e-8, -45.0 - 1e-8}) {
            final double[] sky = forward.evaluate(10.0, eta);
            assertFalse(Double.isNaN(sky[0]), "pix2sky NaN at eta=" + eta);
            final double[] recovered = inverse.evaluate(sky);
            final double[] repix = forward.evaluate(recovered);
            assertEquals(sky[0], repix[0], 1e-6, "phi mismatch at eta=" + eta);
            assertEquals(sky[1], repix[1], 1e-6, "theta mismatch at eta=" + eta);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final HEALPixPolar projection = new HEALPixPolar();
        final double[] sampleInputs = {10.0, 45.0};
        final double[] expected = projection.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 10.0, 45.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        projection.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
