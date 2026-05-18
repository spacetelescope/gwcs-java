package edu.stsci.gwcs.transform.projection.conic;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.ConicPerspective
 * (Pix2Sky_ConicPerspective / Sky2Pix_ConicPerspective). Regenerate via the project tooling if tolerances drift.
 */
class ConicPerspectiveTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new ConicPerspective(45.0, 15.0);
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 45.0},
                new double[]{10.0, 50.0},
                new double[]{-20.0, 40.0}
        );
    }

    @Test
    void testReferencePointSky2Pix() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] output = inverse.evaluate(0.0, 45.0);
        assertEquals(0.0, output[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, output[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma45Delta15() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();

        final double phi = 10.0;
        final double theta = 50.0;
        final double[] pix = inverse.evaluate(phi, theta);
        final double[] sky = projection.evaluate(pix);

        assertEquals(phi, sky[0], DOUBLE_TOLERANCE);
        assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripNonTrivialPoint() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();

        final double phi = -30.0;
        final double theta = 60.0;
        final double[] pix = inverse.evaluate(phi, theta);
        final double[] sky = projection.evaluate(pix);

        assertEquals(phi, sky[0], DOUBLE_TOLERANCE);
        assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma45Delta0() {
        final ConicPerspective projection = new ConicPerspective(45.0, 0.0);
        final Transform inverse = projection.getInverse();

        final double phi = 15.0;
        final double theta = 55.0;
        final double[] pix = inverse.evaluate(phi, theta);
        final double[] sky = projection.evaluate(pix);

        assertEquals(phi, sky[0], DOUBLE_TOLERANCE);
        assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSky2PixNearSingularity() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(0.0, 135.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testHasInverse() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        assertTrue(projection.hasInverse());
    }

    @Test
    void testInputOutputCount() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testRoundTripSigma10Delta5() {
        final ConicPerspective projection = new ConicPerspective(10.0, 5.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(20.0, 15.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(20.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(15.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSky2PixWcslibReferenceValues() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(30.0, 60.0);
        assertEquals(14.659646543168380, pix[0], DOUBLE_TOLERANCE);
        assertEquals(17.574465527397301, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma80Delta10() {
        final ConicPerspective projection = new ConicPerspective(80.0, 10.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(5.0, 85.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(5.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(85.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(22.3420381112162, result[0], DOUBLE_TOLERANCE);
        assertEquals(63.5882895847677, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-43.3031450423929, result[0], DOUBLE_TOLERANCE);
        assertEquals(70.0735743272382, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(45.0, 30.0);
        assertEquals(36.9985303412485, result[0], DOUBLE_TOLERANCE);
        assertEquals(-4.28301656165015, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(-30.0, 45.0);
        assertEquals(-20.0254495884687, result[0], DOUBLE_TOLERANCE);
        assertEquals(3.75004925482371, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNorthPoleRoundTrip() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(0.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsDeltaPositive90() {
        assertThrows(IllegalArgumentException.class, () -> new ConicPerspective(45.0, 90.0));
    }

    @Test
    void testRejectsDeltaNegative90() {
        assertThrows(IllegalArgumentException.class, () -> new ConicPerspective(45.0, -90.0));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ConicPerspective projection = new ConicPerspective(45.0, 15.0);
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
