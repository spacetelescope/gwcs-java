package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.SansonFlamsteed
 * (Pix2Sky_SansonFlamsteed / Sky2Pix_SansonFlamsteed). Regenerate via the project tooling if tolerances drift.
 */
class SansonFlamsteedTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new SansonFlamsteed();
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
        final SansonFlamsteed sfl = new SansonFlamsteed();
        final double[] result = sfl.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testZeroPhiFortyFiveTheta() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        final double[] result = sfl.evaluate(0.0, 45.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonTrivialPoint() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        final double[] pix2sky = sfl.evaluate(30.0, 60.0);
        assertEquals(60.0, pix2sky[0], DOUBLE_TOLERANCE);
        assertEquals(60.0, pix2sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        assertTrue(sfl.hasInverse());

        final Transform inverse = sfl.getInverse();
        final double[] intermediate = sfl.evaluate(15.0, 30.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(15.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        assertEquals(2, sfl.getInputCount());
        assertEquals(2, sfl.getOutputCount());
    }

    @Test
    void testPoleReturnsZeroPhi() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        final double[] result = sfl.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testHasInverse() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        assertTrue(sfl.hasInverse());
    }

    @Test
    void testPoleClampsPhiToZeroForZeroX() {
        final SansonFlamsteed sfl = new SansonFlamsteed();
        final double[] result = sfl.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testPoleWithNonzeroXReturnsNaN() {
        // At the pole only (0, +/-90) maps to a valid sky point; nonzero x is out of domain.
        final SansonFlamsteed sfl = new SansonFlamsteed();
        final double[] result = sfl.evaluate(10.0, 90.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final SansonFlamsteed projection = new SansonFlamsteed();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.6417777247591, result[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final SansonFlamsteed projection = new SansonFlamsteed();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-17.3205080756888, result[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final SansonFlamsteed projection = new SansonFlamsteed(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(22.5, result[0], DOUBLE_TOLERANCE);
        assertEquals(60.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final SansonFlamsteed projection = new SansonFlamsteed(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-21.2132034355964, result[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final SansonFlamsteed projection = new SansonFlamsteed();
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
