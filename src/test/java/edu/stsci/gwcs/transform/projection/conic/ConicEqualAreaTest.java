package edu.stsci.gwcs.transform.projection.conic;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.ConicEqualArea
 * (Pix2Sky_ConicEqualArea / Sky2Pix_ConicEqualArea). Regenerate via the project tooling if tolerances drift.
 */
class ConicEqualAreaTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new ConicEqualArea(45.0, 15.0);
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
    void constructorRejectsSigmaZero() {
        // ConicProjection requires c = sin(sigma)*cos(delta) != 0;
        // sigma=0 makes c=0 and must be rejected.
        assertThrows(IllegalArgumentException.class, () -> new ConicEqualArea(0.0, 15.0));
    }

    @Test
    void testReferencePointSky2Pix() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] output = inverse.evaluate(0.0, 45.0);
        assertEquals(0.0, output[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, output[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma45Delta15() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
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
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
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
        final ConicEqualArea projection = new ConicEqualArea(45.0, 0.0);
        final Transform inverse = projection.getInverse();

        final double phi = 15.0;
        final double theta = 55.0;
        final double[] pix = inverse.evaluate(phi, theta);
        final double[] sky = projection.evaluate(pix);

        assertEquals(phi, sky[0], DOUBLE_TOLERANCE);
        assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testPix2SkyReturnsNaNForOutOfDomainRadius() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final double[] result = projection.evaluate(0.0, -1e6);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void testHasInverse() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        assertTrue(projection.hasInverse());
    }

    @Test
    void testInputOutputCount() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testRoundTripSigma10Delta5() {
        final ConicEqualArea projection = new ConicEqualArea(10.0, 5.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(20.0, 15.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(20.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(15.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSky2PixWcslibReferenceValues() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(30.0, 60.0);
        assertEquals(14.682300025747418, pix[0], DOUBLE_TOLERANCE);
        assertEquals(18.041716073944663, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma80Delta10() {
        final ConicEqualArea projection = new ConicEqualArea(80.0, 10.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(5.0, 85.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(5.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(85.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripNegativeSigma() {
        final ConicEqualArea projection = new ConicEqualArea(-45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(45.0, 30.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(45.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyNegSigmaReference() {
        final ConicEqualArea projection = new ConicEqualArea(-45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(45.0, 30.0);
        assertEquals(62.3649145004764, pix[0], DOUBLE_TOLERANCE);
        assertEquals(47.5546794359737, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(21.9553951606927, result[0], DOUBLE_TOLERANCE);
        assertEquals(63.3364557172882, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-42.1058263441848, result[0], DOUBLE_TOLERANCE);
        assertEquals(71.4275126546324, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(45.0, 30.0);
        assertEquals(37.1287667646707, result[0], DOUBLE_TOLERANCE);
        assertEquals(-5.11220878537005, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(-30.0, 45.0);
        assertEquals(-20.0688701194215, result[0], DOUBLE_TOLERANCE);
        assertEquals(3.62730323325856, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ConicEqualArea projection = new ConicEqualArea(45.0, 15.0);
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
