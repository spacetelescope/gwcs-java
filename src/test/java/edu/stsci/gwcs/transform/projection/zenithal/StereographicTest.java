package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Stereographic
 * (Pix2Sky_Stereographic / Sky2Pix_Stereographic). Regenerate via the project tooling if tolerances drift.
 */
class StereographicTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new Stereographic();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 60.0},
                new double[]{45.0, 30.0},
                new double[]{-30.0, 45.0}
        );
    }

    @Test
    void testReferencePoint() {
        final Stereographic projection = new Stereographic();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonTrivialPoint() {
        final Stereographic projection = new Stereographic();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(67.91681801404866, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final Stereographic projection = new Stereographic();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Stereographic projection = new Stereographic();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final Stereographic projection = new Stereographic();
        assertTrue(projection.hasInverse());
    }

    @Test
    void testAstropyPix2SkyReference() {
        final Stereographic projection = new Stereographic();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(57.3704283006084, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final Stereographic projection = new Stereographic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(21.7115126862174, result[0], DOUBLE_TOLERANCE);
        assertEquals(-21.7115126862174, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final Stereographic projection = new Stereographic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-23.7326889410572, result[0], DOUBLE_TOLERANCE);
        assertEquals(-41.1062230461391, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixNegativeTheta() {
        final Stereographic projection = new Stereographic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(30.0, -60.0);
        assertEquals(213.83076020208716, result[0], DOUBLE_TOLERANCE);
        assertEquals(-370.3657408910921, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testPoleRoundTrip() {
        final Stereographic projection = new Stereographic();
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        assertEquals(0.0, pix[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAntiPoleRejectsNaN() {
        final Stereographic projection = new Stereographic();
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(0.0, -90.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testNegativeThetaRoundTrip() {
        final Stereographic projection = new Stereographic();
        final Transform inverse = projection.getInverse();
        for (final double theta : new double[]{-45.0, -89.0}) {
            final double[] pix = inverse.evaluate(30.0, theta);
            final double[] sky = projection.evaluate(pix);
            assertEquals(30.0, sky[0], DOUBLE_TOLERANCE);
            assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Stereographic projection = new Stereographic();
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
