package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.TangentialSphericalCube
 * (Pix2Sky_TangentialSphericalCube / Sky2Pix_TangentialSphericalCube). Regenerate via the project tooling if tolerances drift.
 */
class TangentialSphericalCubeTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new TangentialSphericalCube();
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
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyNorthPole() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace1() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(10.0, 20.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace2() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(100.0, 10.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(100.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNorthPole() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 90.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void roundTripFace3() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(170.0, 10.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(170.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace4() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(-100.0, 10.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(-100.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripSouthPole() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, -90.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(12.5288077091515, result[0], DOUBLE_TOLERANCE);
        assertEquals(23.4541373159961, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-18.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(32.3115332374239, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final TangentialSphericalCube projection = new TangentialSphericalCube(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(18.3711730708738, result[0], DOUBLE_TOLERANCE);
        assertEquals(71.6288269291262, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final TangentialSphericalCube projection = new TangentialSphericalCube(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-22.5, result[0], DOUBLE_TOLERANCE);
        assertEquals(51.0288568297003, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace5NonPole() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, -60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-60.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripFace5OffAxis() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, -70.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-70.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyXTooLargeReturnsNaN() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(8.0 * 45.0, 0.5 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyOffFaceYReturnsNaN() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(2.0 * 45.0, 4.0 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyTallStripeOutsidePolarFacesReturnsNaN() {
        final TangentialSphericalCube projection = new TangentialSphericalCube();
        final double[] result = projection.evaluate(0.0, 4.0 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void faceBoundaryContinuity() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
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
        final TangentialSphericalCube projection = new TangentialSphericalCube();
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
    void roundTripAtFaceBoundary() {
        final TangentialSphericalCube forward = new TangentialSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, 0.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, recovered[1], DOUBLE_TOLERANCE);
    }
}
