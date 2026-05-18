package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Mercator
 * (Pix2Sky_Mercator / Sky2Pix_Mercator). Regenerate via the project tooling if tolerances drift.
 */
class MercatorTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new Mercator();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 30.0},
                new double[]{45.0, 60.0},
                new double[]{-30.0, -45.0}
        );
    }

    @Test
    void pix2SkyOrigin() {
        final Mercator projection = new Mercator();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripOrigin() {
        final Mercator forward = new Mercator();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(0.0, 0.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNonTrivial() {
        final Mercator forward = new Mercator();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(45.0, 30.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyNonTrivial() {
        final Mercator projection = new Mercator();
        final double[] result = projection.evaluate(45.0, 45.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        final double expectedTheta = Math.toDegrees(2.0 * Math.atan(Math.exp(Math.toRadians(45.0)))) - 90.0;
        assertEquals(expectedTheta, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final Mercator projection = new Mercator();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final Mercator projection = new Mercator();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final Mercator projection = new Mercator();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(19.6057939512726, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final Mercator projection = new Mercator();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-15.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(28.7162844516479, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final Mercator projection = new Mercator(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(75.4561292902169, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final Mercator projection = new Mercator(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-30.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(50.4989867105262, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void sky2PixAtPoleReturnsNaN() {
        final Mercator forward = new Mercator();
        final Transform inverse = forward.getInverse();
        final double[] result90 = inverse.evaluate(0.0, 90.0);
        assertTrue(Double.isNaN(result90[0]) && Double.isNaN(result90[1]));
        final double[] resultMinus90 = inverse.evaluate(0.0, -90.0);
        assertTrue(Double.isNaN(resultMinus90[0]) && Double.isNaN(resultMinus90[1]));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Mercator projection = new Mercator();
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
