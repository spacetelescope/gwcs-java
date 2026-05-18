package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.SlantZenithalPerspective
 * (Pix2Sky_SlantZenithalPerspective / Sky2Pix_SlantZenithalPerspective). Regenerate via the project tooling if tolerances drift.
 */
class SlantZenithalPerspectiveTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new SlantZenithalPerspective();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 60.0},
                new double[]{45.0, 80.0},
                new double[]{-30.0, 70.0}
        );
    }

    @Test
    void constructorRejectsMuSinThetaZeroPlusOneEqualsZero() {
        // mu*sin(theta0) + 1 == 0 makes the projection undefined.
        // mu = 1, theta0 = -90 gives 1*(-1) + 1 == 0.
        assertThrows(IllegalArgumentException.class,
                () -> new SlantZenithalPerspective(1.0, 0.0, -90.0));
    }

    @Test
    void testReferencePoint() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testDefaultParamsMatchGnomonic() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(68.68091512616529, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripDefaultParams() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonDefaultParams() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(1.0, 30.0, 45.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(45.0, 60.0);
        assertEquals(23.7074808041452, pix[0], DOUBLE_TOLERANCE);
        assertEquals(-24.970401567204007, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripNonDefaultParams() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(1.0, 30.0, 45.0);
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(15.0, 10.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(15.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective();
        assertTrue(projection.hasInverse());
    }

    @Test
    void testNearPoleRoundTrip() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(1.0, 30.0, 45.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(10.0, 70.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(10.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(70.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testPix2SkyReturnsNaNForNegativeDiscriminant() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(2.0, 0.0, 0.0);
        final double[] result = projection.evaluate(100.0, 0.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void testPoleInput() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(1.0, 30.0, 45.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(90.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(2.0, 30.0, 45.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(159.393175788713, result[0], DOUBLE_TOLERANCE);
        assertEquals(66.0233313874471, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(2.0, 30.0, 45.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-152.653389093367, result[0], DOUBLE_TOLERANCE);
        assertEquals(47.5236038396157, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(2.0, 30.0, 45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(23.8277153614244, result[0], DOUBLE_TOLERANCE);
        assertEquals(-25.5702882486721, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(2.0, 30.0, 45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-17.4601928991995, result[0], DOUBLE_TOLERANCE);
        assertEquals(-49.6196005879613, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testPix2SkySmallAngleBranch() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective(2.0, 30.0, 45.0);
        final double[] result = projection.evaluate(1e-6, 1e-6);
        assertFalse(Double.isNaN(result[0]));
        assertFalse(Double.isNaN(result[1]));
        final Transform inverse = projection.getInverse();
        final double[] recovered = inverse.evaluate(result);
        assertEquals(1e-6, recovered[0], 1e-8);
        assertEquals(1e-6, recovered[1], 1e-8);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final SlantZenithalPerspective projection = new SlantZenithalPerspective();
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
