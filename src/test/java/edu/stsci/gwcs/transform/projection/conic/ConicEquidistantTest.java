package edu.stsci.gwcs.transform.projection.conic;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.ConicEquidistant
 * (Pix2Sky_ConicEquidistant / Sky2Pix_ConicEquidistant). Regenerate via the project tooling if tolerances drift.
 */
class ConicEquidistantTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new ConicEquidistant(45.0, 15.0);
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 30.0},
                new double[]{30.0, 60.0},
                new double[]{-20.0, 45.0}
        );
    }

    @Test
    void testReferencePointSky2Pix() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] output = inverse.evaluate(0.0, 45.0);
        assertEquals(0.0, output[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, output[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma45Delta15() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
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
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
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
        final ConicEquidistant projection = new ConicEquidistant(45.0, 0.0);
        final Transform inverse = projection.getInverse();

        final double phi = 15.0;
        final double theta = 55.0;
        final double[] pix = inverse.evaluate(phi, theta);
        final double[] sky = projection.evaluate(pix);

        assertEquals(phi, sky[0], DOUBLE_TOLERANCE);
        assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSky2PixWcslibReferenceValues() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(30.0, 60.0);
        assertEquals(14.667300014864331, pix[0], DOUBLE_TOLERANCE);
        assertEquals(17.714678257460882, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void constructorRejectsSigmaZeroDeltaZero() {
        assertThrows(IllegalArgumentException.class, () -> new ConicEquidistant(0.0, 0.0));
    }

    @Test
    void testNegativeThetaRoundTrip() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(10.0, -10.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(10.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(-10.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testHasInverse() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        assertTrue(projection.hasInverse());
    }

    @Test
    void testInputOutputCount() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testRoundTripNegativeSigma() {
        final ConicEquidistant projection = new ConicEquidistant(-45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(45.0, 30.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(45.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyNegSigmaReference() {
        final ConicEquidistant projection = new ConicEquidistant(-45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(45.0, 30.0);
        assertEquals(68.3545306695463, pix[0], DOUBLE_TOLERANCE);
        assertEquals(55.7493510198027, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(22.2185173333833, result[0], DOUBLE_TOLERANCE);
        assertEquals(63.6362144221946, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-42.9149537314622, result[0], DOUBLE_TOLERANCE);
        assertEquals(70.9807621135332, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(45.0, 30.0);
        assertEquals(37.0425137443598, result[0], DOUBLE_TOLERANCE);
        assertEquals(-4.5677371718901, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(-30.0, 45.0);
        assertEquals(-20.0359044252325, result[0], DOUBLE_TOLERANCE);
        assertEquals(3.70831946279283, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ConicEquidistant projection = new ConicEquidistant(45.0, 15.0);
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
