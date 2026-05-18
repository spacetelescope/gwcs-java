package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.SlantOrthographic
 * (Pix2Sky_SlantOrthographic / Sky2Pix_SlantOrthographic). Regenerate via the project tooling if tolerances drift.
 */
class SlantOrthographicTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new SlantOrthographic();
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
    void testReferencePoint() {
        final SlantOrthographic projection = new SlantOrthographic();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonTrivialPoint() {
        final SlantOrthographic projection = new SlantOrthographic();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(67.02885587583629, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final SlantOrthographic projection = new SlantOrthographic();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testGeneralReferencePoint() {
        final SlantOrthographic projection = new SlantOrthographic(0.1, 0.2);
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testGeneralNonTrivialPoint() {
        final SlantOrthographic projection = new SlantOrthographic(0.1, 0.2);
        final double[] sky = projection.evaluate(21.024735006046992, -18.721881328510683);
        assertEquals(45.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(60.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testGeneralRoundTrip() {
        final SlantOrthographic projection = new SlantOrthographic(0.1, 0.2);
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 5.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(5.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testGeneralSky2Pix() {
        final SlantOrthographic forward = new SlantOrthographic(0.1, 0.2);
        final Transform inverse = forward.getInverse();
        final double[] result = inverse.evaluate(45.0, 60.0);
        assertEquals(21.024735006046992, result[0], DOUBLE_TOLERANCE);
        assertEquals(-18.721881328510683, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final SlantOrthographic projection = new SlantOrthographic();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final SlantOrthographic projection = new SlantOrthographic();
        assertTrue(projection.hasInverse());
    }

    @Test
    void testInvalidPixelReturnsNaN() {
        final SlantOrthographic projection = new SlantOrthographic(0.5, 0.5);
        final double[] result = projection.evaluate(1000.0, 1000.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testPoleInput() {
        final SlantOrthographic projection = new SlantOrthographic(0.1, 0.2);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(90.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyDefaultPix2SkyReference() {
        final SlantOrthographic projection = new SlantOrthographic();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(54.1686600407569, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyDefaultSky2PixReference1() {
        final SlantOrthographic projection = new SlantOrthographic(0.0, 0.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(20.2571171135349, result[0], DOUBLE_TOLERANCE);
        assertEquals(-20.2571171135349, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyDefaultSky2PixReference2() {
        final SlantOrthographic projection = new SlantOrthographic(0.0, 0.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-20.2571171135349, result[0], DOUBLE_TOLERANCE);
        assertEquals(-35.0863560555154, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyGeneralPix2SkyReference1() {
        final SlantOrthographic projection = new SlantOrthographic(0.3, 0.5);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.90207770629, result[0], DOUBLE_TOLERANCE);
        assertEquals(69.3365117659452, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyGeneralPix2SkyReference2() {
        final SlantOrthographic projection = new SlantOrthographic(0.3, 0.5);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-145.118205120881, result[0], DOUBLE_TOLERANCE);
        assertEquals(57.2297922037141, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyGeneralSky2PixReference1() {
        final SlantOrthographic projection = new SlantOrthographic(0.3, 0.5, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(22.5599707910712, result[0], DOUBLE_TOLERANCE);
        assertEquals(-16.4190276509744, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyGeneralSky2PixReference2() {
        final SlantOrthographic projection = new SlantOrthographic(0.3, 0.5, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-15.2226535277311, result[0], DOUBLE_TOLERANCE);
        assertEquals(-26.6955834125092, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final SlantOrthographic projection = new SlantOrthographic();
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
