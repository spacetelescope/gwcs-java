package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.HammerAitoff
 * (Pix2Sky_HammerAitoff / Sky2Pix_HammerAitoff). Regenerate via the project tooling if tolerances drift.
 */
class HammerAitoffTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new HammerAitoff();
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
        final HammerAitoff ait = new HammerAitoff();
        final double[] result = ait.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonTrivialPoint() {
        final HammerAitoff ait = new HammerAitoff(Direction.SKY2PIX);
        final double[] sky2pix = ait.evaluate(45.0, 30.0);
        assertEquals(40.030334838276694, sky2pix[0], DOUBLE_TOLERANCE);
        assertEquals(30.196662097172940, sky2pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final HammerAitoff ait = new HammerAitoff();
        assertTrue(ait.hasInverse());

        final Transform inverse = ait.getInverse();
        final double[] intermediate = ait.evaluate(20.0, 35.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(20.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(35.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripSky2Pix() {
        final HammerAitoff ait = new HammerAitoff(Direction.SKY2PIX);
        final Transform inverse = ait.getInverse();
        final double[] intermediate = ait.evaluate(60.0, 45.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(60.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final HammerAitoff ait = new HammerAitoff();
        assertEquals(2, ait.getInputCount());
        assertEquals(2, ait.getOutputCount());
    }

    @Test
    void testPix2SkyReturnsNaNForOutOfDomain() {
        final HammerAitoff ait = new HammerAitoff();
        final double[] result = ait.evaluate(1000.0, 1000.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void testHasInverse() {
        final HammerAitoff ait = new HammerAitoff();
        assertTrue(ait.hasInverse());
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final HammerAitoff projection = new HammerAitoff();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.4882756340463, result[0], DOUBLE_TOLERANCE);
        assertEquals(20.0823582655591, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final HammerAitoff projection = new HammerAitoff();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-16.7846736051656, result[0], DOUBLE_TOLERANCE);
        assertEquals(30.2765047779559, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final HammerAitoff projection = new HammerAitoff(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(25.6455815091775, result[0], DOUBLE_TOLERANCE);
        assertEquals(58.0368085038589, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final HammerAitoff projection = new HammerAitoff(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-22.8615231447637, result[0], DOUBLE_TOLERANCE);
        assertEquals(44.1650712676651, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyOutsideLemonReturnsNaN() {
        // Pixel inside the bounding ellipse (s >= 0) but outside the valid Aitoff lemon (s >= 0.5).
        // x/(4R0)=0.7, y/(2R0)=0.5 -> s = 1 - 0.49 - 0.25 = 0.26 < 0.5.
        final HammerAitoff projection = new HammerAitoff();
        final double R0 = 180.0 / Math.PI;
        final double[] result = projection.evaluate(0.7 * 4.0 * R0, 0.5 * 2.0 * R0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyOutsideEllipseReturnsNaN() {
        final HammerAitoff projection = new HammerAitoff();
        final double R0 = 180.0 / Math.PI;
        final double[] result = projection.evaluate(5.0 * R0, 5.0 * R0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final HammerAitoff projection = new HammerAitoff();
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
