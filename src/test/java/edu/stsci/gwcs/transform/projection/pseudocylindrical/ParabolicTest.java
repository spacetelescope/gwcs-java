package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Parabolic
 * (Pix2Sky_Parabolic / Sky2Pix_Parabolic). Regenerate via the project tooling if tolerances drift.
 */
class ParabolicTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new Parabolic();
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
    void testOrigin() {
        final Parabolic par = new Parabolic();
        final double[] result = par.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSky2PixReferenceValues() {
        final Parabolic par = new Parabolic(Direction.SKY2PIX);
        final double[] sky2pix = par.evaluate(45.0, 30.0);
        assertEquals(39.572335870731756, sky2pix[0], DOUBLE_TOLERANCE);
        assertEquals(31.256671980047461, sky2pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final Parabolic par = new Parabolic();
        assertTrue(par.hasInverse());

        final Transform inverse = par.getInverse();
        final double[] intermediate = par.evaluate(20.0, 50.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(20.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(50.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Parabolic par = new Parabolic();
        assertEquals(2, par.getInputCount());
        assertEquals(2, par.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final Parabolic par = new Parabolic();
        assertTrue(par.hasInverse());
    }

    @Test
    void pix2SkyAtNorthPoleRoundTrips() {
        final Parabolic par = new Parabolic();
        final double[] result = par.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyAtSouthPoleRoundTrips() {
        final Parabolic par = new Parabolic();
        final double[] result = par.evaluate(0.0, -90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyOutOfRangeReturnsNaN() {
        final Parabolic par = new Parabolic();
        final double[] result = par.evaluate(0.0, 200.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
        final double[] result2 = par.evaluate(0.0, -200.0);
        assertTrue(Double.isNaN(result2[0]) && Double.isNaN(result2[1]));
    }

    @Test
    void testPix2SkyAtDenomNearZero() {
        final Parabolic par = new Parabolic();
        final double[] result = par.evaluate(10.0, 90.0);
        assertTrue(Double.isNaN(result[0]) || Math.abs(result[0]) > 1e10);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final Parabolic projection = new Parabolic();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.5194805194805, result[0], DOUBLE_TOLERANCE);
        assertEquals(19.1381106253284, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final Parabolic projection = new Parabolic();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-16.875, result[0], DOUBLE_TOLERANCE);
        assertEquals(28.7822046805814, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final Parabolic projection = new Parabolic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(23.943999880708, result[0], DOUBLE_TOLERANCE);
        assertEquals(61.5636257986204, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final Parabolic projection = new Parabolic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-21.9615242270663, result[0], DOUBLE_TOLERANCE);
        assertEquals(46.5874281184537, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Parabolic projection = new Parabolic();
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
