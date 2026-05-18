package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.ZenithalPerspective
 * (Pix2Sky_ZenithalPerspective / Sky2Pix_ZenithalPerspective). Regenerate via the project tooling if tolerances drift.
 */
class ZenithalPerspectiveTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new ZenithalPerspective();
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
    void constructorRejectsMuMinusOne() {
        assertThrows(IllegalArgumentException.class, () -> new ZenithalPerspective(-1.0, 0.0));
    }

    @Test
    void constructorRejectsGamma90() {
        assertThrows(IllegalArgumentException.class, () -> new ZenithalPerspective(0.0, 90.0));
        assertThrows(IllegalArgumentException.class, () -> new ZenithalPerspective(0.0, -90.0));
    }

    @Test
    void testReferencePoint() {
        final ZenithalPerspective projection = new ZenithalPerspective();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testDefaultParamsMatchGnomonic() {
        final ZenithalPerspective projection = new ZenithalPerspective();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(68.68091512616529, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripDefaultParams() {
        final ZenithalPerspective projection = new ZenithalPerspective();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonDefaultParams() {
        final ZenithalPerspective projection = new ZenithalPerspective(2.0, 30.0);
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(45.0, 60.0);
        assertEquals(19.794264211030015, pix[0], DOUBLE_TOLERANCE);
        assertEquals(-22.856447541297506, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripNonDefaultParams() {
        final ZenithalPerspective projection = new ZenithalPerspective(2.0, 30.0);
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(15.0, 10.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(15.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final ZenithalPerspective projection = new ZenithalPerspective();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final ZenithalPerspective projection = new ZenithalPerspective();
        assertTrue(projection.hasInverse());
    }

    @Test
    void testSky2PixReturnsNaNAtThetaNeg90() {
        final ZenithalPerspective projection = new ZenithalPerspective(1.0, 0.0);
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(0.0, -90.0);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void testPix2SkyAtQZeroSingularity() {
        final ZenithalPerspective projection = new ZenithalPerspective(2.0, 30.0);
        final double yAtSingularity = -(180.0 / Math.PI) * 3.0 / Math.sin(Math.toRadians(30.0));
        final double[] result = projection.evaluate(0.0, yAtSingularity);
        assertTrue(Double.isNaN(result[0]) || Double.isNaN(result[1]));
    }

    @Test
    void testPoleRoundTrip() {
        final ZenithalPerspective projection = new ZenithalPerspective();
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        assertEquals(0.0, pix[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, pix[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final ZenithalPerspective projection = new ZenithalPerspective(2.0, 30.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(150.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(71.0983217779465, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final ZenithalPerspective projection = new ZenithalPerspective(2.0, 30.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-150.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(62.399379413567, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference() {
        final ZenithalPerspective projection = new ZenithalPerspective(2.0, 30.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-19.855635036171, result[0], DOUBLE_TOLERANCE);
        assertEquals(-39.7112700723421, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ZenithalPerspective projection = new ZenithalPerspective();
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
