package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.QuadSphericalCube
 * (Pix2Sky_QuadSphericalCube / Sky2Pix_QuadSphericalCube). Regenerate via the project tooling if tolerances drift.
 */
class QuadSphericalCubeTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new QuadSphericalCube();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{10.0, 20.0},
                new double[]{100.0, 10.0},
                new double[]{-100.0, 10.0}
        );
    }

    @Test
    void pix2SkyOrigin() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyNorthPole() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace1() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(10.0, 20.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace2() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(100.0, 10.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(100.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNorthPole() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 90.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void roundTripFace3() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(170.0, 10.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(170.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace4() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(-100.0, 10.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(-100.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripSouthPole() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, -90.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkySouthPole() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(0.0, -90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(8.96794215677953, result[0], DOUBLE_TOLERANCE);
        assertEquals(18.7560864718591, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-14.2325861964077, result[0], DOUBLE_TOLERANCE);
        assertEquals(28.1725344445446, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final QuadSphericalCube projection = new QuadSphericalCube(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(25.3357312637361, result[0], DOUBLE_TOLERANCE);
        assertEquals(64.6642687362639, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final QuadSphericalCube projection = new QuadSphericalCube(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-25.6806914696327, result[0], DOUBLE_TOLERANCE);
        assertEquals(48.5580912546601, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace5NonPole() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, -60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-60.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace5OffAxis() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, -70.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-70.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyXTooLargeReturnsNaN() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(8.0 * 45.0, 0.5 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyOffFaceYReturnsNaN() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(2.0 * 45.0, 4.0 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyTallStripeOutsidePolarFacesReturnsNaN() {
        final QuadSphericalCube projection = new QuadSphericalCube();
        final double[] result = projection.evaluate(0.0, 4.0 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void faceBoundaryContinuity() {
        final QuadSphericalCube forward = new QuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double eps = 1e-8;
        for (final double phi : new double[]{45.0, 135.0}) {
            final double[] pixA = inverse.evaluate(phi - eps, 0.0);
            final double[] pixB = inverse.evaluate(phi + eps, 0.0);
            assertTrue(Math.abs(pixA[0] - pixB[0]) < 1e-3,
                    "x discontinuity at phi=" + phi + ": " + pixA[0] + " vs " + pixB[0]);
            assertTrue(Math.abs(pixA[1] - pixB[1]) < 1e-3,
                    "y discontinuity at phi=" + phi + ": " + pixA[1] + " vs " + pixB[1]);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final QuadSphericalCube projection = new QuadSphericalCube();
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
