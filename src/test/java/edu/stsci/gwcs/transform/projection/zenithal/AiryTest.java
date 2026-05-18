package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Airy
 * (Pix2Sky_Airy / Sky2Pix_Airy). Regenerate via the project tooling if tolerances drift.
 */
class AiryTest extends AbstractProjectionContractTest {
    private static final double AIRY_TOLERANCE = 1e-10;

    @Override
    protected Projection factory() {
        return new Airy();
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
    void constructorRejectsThetaBAtNegativeNinety() {
        assertThrows(IllegalArgumentException.class, () -> new Airy(-90.0));
    }

    @Test
    void constructorRejectsThetaBBelowNegativeNinety() {
        assertThrows(IllegalArgumentException.class, () -> new Airy(-100.0));
    }

    @Test
    void constructorAcceptsThetaBJustAboveNegativeNinety() {
        assertDoesNotThrow(() -> new Airy(-89.999));
    }

    @Test
    void testReferencePoint() {
        final Airy projection = new Airy();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], AIRY_TOLERANCE);
        assertEquals(90.0, result[1], AIRY_TOLERANCE);
    }

    @Test
    void testSky2PixKnownValue() {
        final Airy projection = new Airy();
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(0.0, 45.0);
        assertEquals(0.0, result[0], AIRY_TOLERANCE);
        assertEquals(-45.63594406073804, result[1], AIRY_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final Airy projection = new Airy();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = inverse.evaluate(45.0, 60.0);
        final double[] recovered = projection.evaluate(intermediate);
        assertEquals(45.0, recovered[0], AIRY_TOLERANCE);
        assertEquals(60.0, recovered[1], AIRY_TOLERANCE);
    }

    @Test
    void testRoundTripFromPixel() {
        final Airy projection = new Airy();
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], AIRY_TOLERANCE);
        assertEquals(20.0, recovered[1], AIRY_TOLERANCE);
    }

    @Test
    void testNonDefaultThetaB() {
        final Airy projection = new Airy(45.0);
        final Transform inverse = projection.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 50.0);
        final double[] recovered = projection.evaluate(intermediate);
        assertEquals(30.0, recovered[0], AIRY_TOLERANCE);
        assertEquals(50.0, recovered[1], AIRY_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Airy projection = new Airy();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final Airy projection = new Airy();
        assertTrue(projection.hasInverse());
    }

    @Test
    void testPoleRoundTrip() {
        final Airy projection = new Airy();
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        assertEquals(0.0, pix[0], AIRY_TOLERANCE);
        assertEquals(0.0, pix[1], AIRY_TOLERANCE);
        final double[] sky = projection.evaluate(pix);
        assertEquals(0.0, sky[0], AIRY_TOLERANCE);
        assertEquals(90.0, sky[1], AIRY_TOLERANCE);
    }

    @Test
    void testSmallAngleRoundTrip() {
        final Airy projection = new Airy();
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 89.999);
        final double[] sky = projection.evaluate(pix);
        assertEquals(0.0, sky[0], AIRY_TOLERANCE);
        assertEquals(89.999, sky[1], AIRY_TOLERANCE);
    }

    @Test
    void testPix2SkyReturnsNaNForLargeRadius() {
        final Airy projection = new Airy();
        final double[] result = projection.evaluate(0.0, -1e12);
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyBeyondDivergenceRadiusReturnsNaN() {
        // rTheta beyond the Airy projection's valid range causes the inverse
        // solver to fail; the contract is to return NaN for both phi and theta.
        final Airy projection = new Airy(45.0);
        final double[] result = projection.evaluate(0.0, -1e30);
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyAtNaNInputReturnsNaN() {
        final Airy projection = new Airy();
        final double[] result = projection.evaluate(Double.NaN, 10.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testLargeRadiusRoundTrip() {
        final Airy projection = new Airy();
        final Transform inverse = projection.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, -80.0);
        final double[] recovered = projection.evaluate(intermediate);
        assertEquals(0.0, recovered[0], AIRY_TOLERANCE);
        assertEquals(-80.0, recovered[1], AIRY_TOLERANCE);
    }

    @Test
    void testAstropy90Pix2SkyReference1() {
        final Airy projection = new Airy();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(67.7112145108343, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy90Pix2SkyReference2() {
        final Airy projection = new Airy();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(56.7055533089348, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy90Sky2PixReference1() {
        final Airy projection = new Airy(90.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(21.3395115856185, result[0], DOUBLE_TOLERANCE);
        assertEquals(-21.3395115856185, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy90Sky2PixReference2() {
        final Airy projection = new Airy(90.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-22.817972030369, result[0], DOUBLE_TOLERANCE);
        assertEquals(-39.5218868822847, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy45Pix2SkyReference1() {
        final Airy projection = new Airy(45.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(66.8143702972421, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy45Pix2SkyReference2() {
        final Airy projection = new Airy(45.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(55.3605571820062, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy45Sky2PixReference1() {
        final Airy projection = new Airy(45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(20.5026958392011, result[0], DOUBLE_TOLERANCE);
        assertEquals(-20.5026958392011, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropy45Sky2PixReference2() {
        final Airy projection = new Airy(45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-21.9032551196808, result[0], DOUBLE_TOLERANCE);
        assertEquals(-37.9375507184303, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Airy projection = new Airy();
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
