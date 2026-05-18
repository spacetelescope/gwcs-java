package edu.stsci.gwcs.transform.projection.conic;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.ConicOrthomorphic
 * (Pix2Sky_ConicOrthomorphic / Sky2Pix_ConicOrthomorphic). Regenerate via the project tooling if tolerances drift.
 */
class ConicOrthomorphicTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new ConicOrthomorphic(45.0, 15.0);
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
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] output = inverse.evaluate(0.0, 45.0);
        assertEquals(0.0, output[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, output[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSigma45Delta15() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
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
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
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
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 0.0);
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
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(30.0, 60.0);
        assertEquals(14.651511133449878, pix[0], DOUBLE_TOLERANCE);
        assertEquals(17.419426998776380, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNorthPoleRoundTripPositiveC() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(0.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testConstructorRejectsCZero() {
        assertThrows(IllegalArgumentException.class, () -> new ConicOrthomorphic(0.0, 0.0));
    }

    @Test
    void testHasInverse() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        assertTrue(projection.hasInverse());
    }

    @Test
    void testInputOutputCount() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testNegativeCPix2SkyReference() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(-45.0, 5.0);
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(-45.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testDelta0Pix2SkyReference() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 0.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(21.2266341010183, result[0], DOUBLE_TOLERANCE);
        assertEquals(63.3280296451223, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testDelta0Sky2PixReference() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 0.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(45.0, 30.0);
        assertEquals(38.2044796792992, result[0], DOUBLE_TOLERANCE);
        assertEquals(-4.27420703108096, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(22.4802353094921, result[0], DOUBLE_TOLERANCE);
        assertEquals(63.8997224345278, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-43.7319030490337, result[0], DOUBLE_TOLERANCE);
        assertEquals(70.7324096686748, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(45.0, 30.0);
        assertEquals(36.9517962574562, result[0], DOUBLE_TOLERANCE);
        assertEquals(-3.99994244002156, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(-30.0, 45.0);
        assertEquals(-20.0100204484126, result[0], DOUBLE_TOLERANCE);
        assertEquals(3.79305220745344, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ConicOrthomorphic projection = new ConicOrthomorphic(45.0, 15.0);
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
