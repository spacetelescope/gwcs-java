package edu.stsci.gwcs.transform.projection.healpix;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.HEALPix
 * (Pix2Sky_HEALPix / Sky2Pix_HEALPix). Regenerate via the project tooling if tolerances drift.
 */
class HEALPixTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new HEALPix();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{30.0, 20.0},
                new double[]{45.0, 80.0},
                new double[]{-60.0, -30.0}
        );
    }

    @Test
    void pix2SkyOrigin() {
        final HEALPix projection = new HEALPix();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripOrigin() {
        final HEALPix forward = new HEALPix();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 0.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripEquatorial() {
        final HEALPix forward = new HEALPix();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 20.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripPolar() {
        final HEALPix forward = new HEALPix();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, 80.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(80.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripSouthPolar() {
        final HEALPix forward = new HEALPix();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, -70.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-70.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final HEALPix projection = new HEALPix();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final HEALPix projection = new HEALPix();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void roundTripH6K5() {
        final HEALPix forward = new HEALPix(6.0, 5.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 40.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(40.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripH6K5PolarRegion() {
        final HEALPix forward = new HEALPix(6.0, 5.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(60.0, 80.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(60.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(80.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testEvenKSouthPolarReference() {
        final HEALPix projection = new HEALPix(4.0, 4.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, -60.0);
        assertEquals(57.0577136594005, result[0], DOUBLE_TOLERANCE);
        assertEquals(-79.5577136594005, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testEvenKRoundTrip() {
        final HEALPix forward = new HEALPix(4.0, 4.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, -60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-60.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final HEALPix projection = new HEALPix(4.0, 3.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(17.2352852570879, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final HEALPix projection = new HEALPix(4.0, 3.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-15.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(26.387799961243, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final HEALPix projection = new HEALPix(4.0, 3.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(61.4711431702997, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final HEALPix projection = new HEALPix(4.0, 3.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-30.9393128653298, result[0], DOUBLE_TOLERANCE);
        assertEquals(47.8179385959894, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyOutOfRangeYReturnsNaN() {
        final HEALPix projection = new HEALPix();
        final double ylim = (3.0 + 1.0) / 2.0 * (180.0 / 4.0);
        final double[] result = projection.evaluate(0.0, ylim + 1.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyOutOfRangeYNegativeReturnsNaN() {
        final HEALPix projection = new HEALPix();
        final double ylim = (3.0 + 1.0) / 2.0 * (180.0 / 4.0);
        final double[] result = projection.evaluate(0.0, -(ylim + 1.0));
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyPolarFacetBoundsViolationReturnsNaN() {
        // Default h=4, k=3 → facetWidth=45°, polarBoundary=45°, halfKPlus1=2.
        // In the polar region (|y|>45 and |y|<=90), pixels that fall outside
        // the valid facet boundary are rejected. At y=80 the valid region is
        // narrow, so x=70 lands well outside a facet and must return NaN.
        final HEALPix projection = new HEALPix();
        final double[] result = projection.evaluate(70.0, 80.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]),
                "Expected NaN for pixel outside facet boundary; got ("
                        + result[0] + ", " + result[1] + ")");
    }

    @Test
    void constructorRejectsNonPositiveH() {
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(0.0, 3.0));
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(-1.0, 3.0));
    }

    @Test
    void constructorRejectsNonPositiveK() {
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(4.0, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(4.0, -1.0));
    }

    @Test
    void constructorRejectsNonFiniteParams() {
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(Double.NaN, 3.0));
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(4.0, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(Double.POSITIVE_INFINITY, 3.0));
        assertThrows(IllegalArgumentException.class, () -> new HEALPix(4.0, Double.NEGATIVE_INFINITY));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final HEALPix projection = new HEALPix();
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

    @Test
    void equatorialPolarBoundaryRoundTrips() {
        final HEALPix forward = new HEALPix();
        final Transform inverse = forward.getInverse();
        final double boundaryTheta = Math.toDegrees(Math.asin(2.0 / 3.0));
        for (final double offset : new double[]{0.0, 1e-8, -1e-8}) {
            final double theta = boundaryTheta + offset;
            final double[] intermediate = inverse.evaluate(30.0, theta);
            assertFalse(Double.isNaN(intermediate[0]), "sky2pix NaN at theta=" + theta);
            final double[] recovered = forward.evaluate(intermediate);
            assertEquals(30.0, recovered[0], 1e-6);
            assertEquals(theta, recovered[1], 1e-6);
        }
    }

    @Test
    void sky2PixPhiNear180MeridianRoundTrips() {
        final HEALPix forward = new HEALPix();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(170.0, 80.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(170.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(80.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripOddHEvenK() {
        final HEALPix forward = new HEALPix(5.0, 4.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(60.0, 75.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(60.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(75.0, recovered[1], DOUBLE_TOLERANCE);
    }
}
