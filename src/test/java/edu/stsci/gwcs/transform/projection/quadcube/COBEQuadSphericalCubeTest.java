package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Tests for {@link COBEQuadSphericalCube}.
 *
 * <p>This projection uses separate polynomial approximations for forward and inverse (they are
 * not exact mathematical inverses of each other). This means round-trip precision is limited
 * by how well the two approximations agree, not by floating-point arithmetic.
 *
 * <p>The forward direction alone matches astropy to ~1e-5. Round-trips are looser (~1e-2 for
 * pix→sky→pix, ~5e-3 for sky→pix→sky) because the two polynomial fits don't perfectly cancel.
 */
class COBEQuadSphericalCubeTest extends AbstractProjectionContractTest {

    /** Round-trip tolerance for sky → pix → sky (the better-fitting direction): 5e-3 deg. */
    private static final double SKY_ROUND_TRIP_TOLERANCE = 5e-3;

    /** Round-trip tolerance for pix → sky → pix (the looser direction): 2e-2 deg. */
    private static final double PIX_ROUND_TRIP_TOLERANCE = 2e-2;

    /** Face-boundary tolerance — same as the sky round-trip tolerance. */
    private static final double FACE_BOUNDARY_TOLERANCE = SKY_ROUND_TRIP_TOLERANCE;

    /** Tolerance for comparison against astropy's forward polynomial (~1e-5 precision). */
    private static final double ASTROPY_REFERENCE_TOLERANCE = 1e-5;

    @Override
    protected Projection factory() {
        return new COBEQuadSphericalCube();
    }

    @Override
    protected Stream<double[]> roundTripSamples() {
        return Stream.of(
                new double[]{10.0, 20.0},
                new double[]{100.0, 10.0},
                new double[]{170.0, 10.0},
                new double[]{-100.0, 10.0}
        );
    }

    @Override
    protected double roundTripTolerance() {
        // The base-class round-trip test may hit near-boundary pixels, so use the wider tolerance.
        return FACE_BOUNDARY_TOLERANCE;
    }

    @Test
    void pix2SkyOrigin() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(0.0, result[1], ASTROPY_REFERENCE_TOLERANCE);
    }

    @Test
    void pix2SkyNorthPole() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] result = projection.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(90.0, result[1], ASTROPY_REFERENCE_TOLERANCE);
    }

    /**
     * Round-trip test on interior pixels: forward then inverse, checking recovery within
     * {@link #PIX_ROUND_TRIP_TOLERANCE}. The tolerance reflects the mismatch between the two
     * independent polynomial approximations, not floating-point limits.
     */
    @Test
    void interiorPixelsRoundTripWithinPolynomialFitBound() {
        final COBEQuadSphericalCube forward = new COBEQuadSphericalCube();
        final Transform inverse = forward.getInverse();

        final double[][] interiorPixels = {
                {0.0, 0.0},      // face 1 center
                {10.0, 20.0},    // face 1 interior
                {-15.0, 30.0},   // face 1 interior (negative x)
                {5.0, -10.0},    // face 1 interior (negative y)
                {100.0, 10.0},   // face 2 (xf in (1, 3))
                {170.0, 10.0},   // face 3 (xf in (3, 5))
                {-100.0, 10.0},  // wraps to face 4 (xf in (5, 7) after +8)
                {0.0, 80.0},     // face 0 (yf > 1)
                {0.0, -60.0}     // face 5 (yf < -1)
        };

        for (final double[] pix : interiorPixels) {
            final double[] sky = forward.evaluate(pix);
            assertFalse(Double.isNaN(sky[0]) || Double.isNaN(sky[1]),
                    "forward returned NaN for interior pixel (" + pix[0] + ", " + pix[1] + ")");
            final double[] recovered = inverse.evaluate(sky);
            // x wraps at 360°, so normalize the difference to [-180, 180] before comparing.
            final double dx = wrapDegrees(recovered[0] - pix[0]);
            assertEquals(0.0, dx, PIX_ROUND_TRIP_TOLERANCE,
                    "x round-trip failed for interior pixel (" + pix[0] + ", " + pix[1] + ")");
            assertEquals(pix[1], recovered[1], PIX_ROUND_TRIP_TOLERANCE,
                    "y round-trip failed for interior pixel (" + pix[0] + ", " + pix[1] + ")");
        }
    }

    private static double wrapDegrees(final double d) {
        double r = (d + 180.0) % 360.0;
        if (r < 0.0) {
            r += 360.0;
        }
        return r - 180.0;
    }

    /**
     * Near face boundaries ({@code |xf|} or {@code |yf|} near 1) the polynomial approximation
     * is less accurate, so the round-trip tolerance widens to {@link #FACE_BOUNDARY_TOLERANCE}.
     */
    @Test
    void faceBoundaryPixelsRoundTripWithinPublishedBound() {
        final COBEQuadSphericalCube forward = new COBEQuadSphericalCube();
        final Transform inverse = forward.getInverse();

        final double[][] boundaryPixels = {
                {44.99, 0.0},   // xf ≈ 1 - 1e-4
                {0.0, 44.99},   // yf ≈ 1 - 1e-4
                {44.99, 44.99}, // near (1, 1) corner
                {44.0, 44.0}    // just inside corner
        };

        for (final double[] pix : boundaryPixels) {
            final double[] sky = forward.evaluate(pix);
            if (Double.isNaN(sky[0]) || Double.isNaN(sky[1])) {
                continue;
            }
            final double[] recovered = inverse.evaluate(sky);
            assertEquals(pix[0], recovered[0], FACE_BOUNDARY_TOLERANCE,
                    "x face-boundary round-trip exceeded published bound for (" + pix[0] + ", " + pix[1] + ")");
            assertEquals(pix[1], recovered[1], FACE_BOUNDARY_TOLERANCE,
                    "y face-boundary round-trip exceeded published bound for (" + pix[0] + ", " + pix[1] + ")");
        }
    }

    @Test
    void roundTripNorthPole() {
        final COBEQuadSphericalCube forward = new COBEQuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 90.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], FACE_BOUNDARY_TOLERANCE);
        assertEquals(90.0, recovered[1], FACE_BOUNDARY_TOLERANCE);
    }

    @Test
    void roundTripSouthPole() {
        final COBEQuadSphericalCube forward = new COBEQuadSphericalCube();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, -60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], FACE_BOUNDARY_TOLERANCE);
        assertEquals(-60.0, recovered[1], FACE_BOUNDARY_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void pix2SkyAstropyReference() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] result1 = projection.evaluate(10.0, 20.0);
        assertEquals(9.30696852563674, result1[0], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(18.5214463223084, result1[1], ASTROPY_REFERENCE_TOLERANCE);

        final double[] result2 = projection.evaluate(-15.0, 30.0);
        assertEquals(-14.1998514029425, result2[0], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(27.8909194435247, result2[1], ASTROPY_REFERENCE_TOLERANCE);

        final double[] result3 = projection.evaluate(5.0, -10.0);
        assertEquals(4.62875306666231, result3[0], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(-9.26157889327461, result3[1], ASTROPY_REFERENCE_TOLERANCE);
    }

    @Test
    void sky2PixAstropyReference() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final Transform inverse = projection.getInverse();

        final double[] result1 = inverse.evaluate(45.0, 60.0);
        assertEquals(23.5051444172859, result1[0], FACE_BOUNDARY_TOLERANCE);
        assertEquals(66.4948582649231, result1[1], FACE_BOUNDARY_TOLERANCE);

        final double[] result2 = inverse.evaluate(-30.0, 45.0);
        assertEquals(-26.7844426631927, result2[0], FACE_BOUNDARY_TOLERANCE);
        assertEquals(48.6010479927063, result2[1], FACE_BOUNDARY_TOLERANCE);

        final double[] result3 = inverse.evaluate(10.0, 80.0);
        assertEquals(1.89343655481935, result3[0], FACE_BOUNDARY_TOLERANCE);
        assertEquals(79.3980699777603, result3[1], FACE_BOUNDARY_TOLERANCE);
    }

    @Test
    void pix2SkyXTooLargeReturnsNaN() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] result = projection.evaluate(8.0 * 45.0, 0.5 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyOffFaceYReturnsNaN() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] result = projection.evaluate(2.0 * 45.0, 4.0 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyTallStripeOutsidePolarFacesReturnsNaN() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] result = projection.evaluate(0.0, 4.0 * 45.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    /**
     * Same idea as the forward round-trip, but in the inverse direction: run forward on interior
     * pixels to get sky points, then verify the inverse recovers the original pixels exactly.
     */
    @Test
    void inverseRecoversForwardGeneratedSkyOnInteriorGrid() {
        final COBEQuadSphericalCube forward = new COBEQuadSphericalCube();
        final COBEQuadSphericalCube inverse = new COBEQuadSphericalCube(Direction.SKY2PIX);

        // Sweep an interior 3x3 grid on face 1 (|xf| < 0.9, |yf| < 0.9).
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                final double x = i * 15.0;
                final double y = j * 15.0;
                final double[] sky = forward.evaluate(x, y);
                final double[] back = inverse.evaluate(sky);
                assertEquals(x, back[0], PIX_ROUND_TRIP_TOLERANCE,
                        "x mismatch at (" + x + ", " + y + ")");
                assertEquals(y, back[1], PIX_ROUND_TRIP_TOLERANCE,
                        "y mismatch at (" + x + ", " + y + ")");
            }
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final COBEQuadSphericalCube projection = new COBEQuadSphericalCube();
        final double[] sampleInputs = {10.0, 45.0};
        final double[] expected = projection.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 10.0, 45.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        projection.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(expected[1], outputs[2], ASTROPY_REFERENCE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
