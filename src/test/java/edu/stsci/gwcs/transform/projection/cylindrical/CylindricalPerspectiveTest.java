package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.CylindricalPerspective
 * (Pix2Sky_CylindricalPerspective / Sky2Pix_CylindricalPerspective). Regenerate via the project tooling if tolerances drift.
 */
class CylindricalPerspectiveTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new CylindricalPerspective(1.0, 1.0);
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 30.0},
                new double[]{45.0, 30.0},
                new double[]{-30.0, 20.0}
        );
    }

    @Test
    void muEqualsNegativeLambdaThrows() {
        final IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> new CylindricalPerspective(1.0, -1.0));
        assertTrue(ex1.getMessage().contains("mu = -lambda"));
        final IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new CylindricalPerspective(-2.0, 2.0));
        assertTrue(ex2.getMessage().contains("mu = -lambda"));
    }

    @Test
    void constructorRejectsLambdaZero() {
        assertThrows(IllegalArgumentException.class, () -> new CylindricalPerspective(1.0, 0.0));
    }

    @Test
    void roundTrip() {
        final CylindricalPerspective forward = new CylindricalPerspective(1.0, 1.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(45.0, 30.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripDefaultParameters() {
        final CylindricalPerspective forward = new CylindricalPerspective();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(60.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(60.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyOrigin() {
        final CylindricalPerspective projection = new CylindricalPerspective(1.0, 1.0);
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final CylindricalPerspective projection = new CylindricalPerspective();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final CylindricalPerspective projection = new CylindricalPerspective();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final CylindricalPerspective projection = new CylindricalPerspective(1.0, 2.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(5.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(13.2736460933943, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final CylindricalPerspective projection = new CylindricalPerspective(1.0, 2.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-7.5, result[0], DOUBLE_TOLERANCE);
        assertEquals(19.8005544979797, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final CylindricalPerspective projection = new CylindricalPerspective(1.0, 2.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(90.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(99.2392011759226, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final CylindricalPerspective projection = new CylindricalPerspective(1.0, 2.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-60.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(71.1980668231717, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void sky2PixNearSingularityProducesLargeValues() {
        final double mu = -0.5;
        final CylindricalPerspective forward = new CylindricalPerspective(mu, 1.0);
        final Transform inverse = forward.getInverse();
        final double thetaNearSingularity = 59.9;
        final double[] pix = inverse.evaluate(10.0, thetaNearSingularity);
        assertTrue(Double.isFinite(pix[0]) && Double.isFinite(pix[1]));
        final double[] sky = forward.evaluate(pix);
        assertEquals(10.0, sky[0], 1e-6);
        assertEquals(thetaNearSingularity, sky[1], 1e-6);
    }

    @Test
    void sky2PixDenomZeroReturnsNaN() {
        final double theta = 60.0;
        final double mu = -Math.cos(Math.toRadians(theta));
        final CylindricalPerspective forward = new CylindricalPerspective(mu, 1.0);
        final Transform inverse = forward.getInverse();
        final double[] result = inverse.evaluate(0.0, theta);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final CylindricalPerspective projection = new CylindricalPerspective();
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
