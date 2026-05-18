package edu.stsci.gwcs.transform.projection.pseudoconic;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Polyconic
 * (Pix2Sky_Polyconic / Sky2Pix_Polyconic). Regenerate via the project tooling if tolerances drift.
 */
class PolyconicTest extends AbstractProjectionContractTest {
    private static final double TOLERANCE = 1e-10;

    @Override
    protected Projection factory() {
        return new Polyconic();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{45.0, 30.0},
                new double[]{10.0, 60.0},
                new double[]{-20.0, -40.0}
        );
    }

    @Test
    void pix2SkyOrigin() {
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], TOLERANCE);
        assertEquals(0.0, result[1], TOLERANCE);
    }

    @Test
    void roundTripOrigin() {
        final Polyconic forward = new Polyconic();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 0.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], TOLERANCE);
        assertEquals(0.0, recovered[1], TOLERANCE);
    }

    @Test
    void roundTripNonTrivial() {
        final Polyconic forward = new Polyconic();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, 30.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(45.0, recovered[0], TOLERANCE);
        assertEquals(30.0, recovered[1], TOLERANCE);
    }

    @Test
    void roundTripNonTrivial2() {
        final Polyconic forward = new Polyconic();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(10.0, 60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(10.0, recovered[0], TOLERANCE);
        assertEquals(60.0, recovered[1], TOLERANCE);
    }

    @Test
    void roundTripNegativeTheta() {
        final Polyconic forward = new Polyconic();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(20.0, -45.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(20.0, recovered[0], TOLERANCE);
        assertEquals(-45.0, recovered[1], TOLERANCE);
    }

    @Test
    void roundTripSmallAngle() {
        final Polyconic forward = new Polyconic();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 0.00005);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], TOLERANCE);
        assertEquals(0.00005, recovered[1], TOLERANCE);
    }

    @Test
    void pix2SkySmallAngleBranch() {
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(0.0, 1e-5);
        assertEquals(1e-5, result[1], TOLERANCE);
        assertFalse(Double.isNaN(result[0]));
    }

    @Test
    void pix2SkyAt90Degrees() {
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], TOLERANCE);
        assertEquals(90.0, result[1], TOLERANCE);
    }

    @Test
    void pix2SkyOnCentralMeridianMidLatitude() {
        final Polyconic projection = new Polyconic();
        final double[] resultNorth = projection.evaluate(0.0, 45.0);
        assertEquals(0.0, resultNorth[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, resultNorth[1], DOUBLE_TOLERANCE);

        final double[] resultSouth = projection.evaluate(0.0, -67.5);
        assertEquals(0.0, resultSouth[0], DOUBLE_TOLERANCE);
        assertEquals(-67.5, resultSouth[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final Polyconic projection = new Polyconic();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final Polyconic projection = new Polyconic();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.6277640138418, result[0], DOUBLE_TOLERANCE);
        assertEquals(19.6874514185298, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-17.1956660985049, result[0], DOUBLE_TOLERANCE);
        assertEquals(28.909921945638, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final Polyconic projection = new Polyconic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(20.8048006850234, result[0], DOUBLE_TOLERANCE);
        assertEquals(67.3614707242085, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final Polyconic projection = new Polyconic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-20.7318709609446, result[0], DOUBLE_TOLERANCE);
        assertEquals(48.8823366688836, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyAboveNinetyAlongXZeroReturnsNaN() {
        // Along the x==0 axis with |y| > 90 the projection has no valid theta and must NaN.
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(0.0, 95.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyAboveNinetyOffAxisReturnsNaN() {
        // Off the x==0 axis with |y| > 90 is also out-of-domain; expect NaN.
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(10.0, 95.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void pix2SkySubnormalXOnAxisReturnsZeroPhi() {
        // Extremely small x (squares to 0) must take the x==0 branch and
        // return (0, y) instead of failing.
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(1e-300, 30.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyAtNaNInputReturnsNaN() {
        final Polyconic projection = new Polyconic();
        final double[] result = projection.evaluate(Double.NaN, 10.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void sky2PixAtNaNInputReturnsNaN() {
        final Polyconic projection = new Polyconic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(Double.NaN, 0.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyNearBoundaryConverges() {
        final Polyconic pix2sky = new Polyconic();
        final Polyconic sky2pix = new Polyconic(Direction.SKY2PIX);
        final double[] pix = sky2pix.evaluate(0.001, 89.999);
        final double[] sky = pix2sky.evaluate(pix);
        assertEquals(0.001, sky[0], 1e-6);
        assertEquals(89.999, sky[1], 1e-6);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Polyconic projection = new Polyconic();
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
