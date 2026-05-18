package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Gnomonic
 * (Pix2Sky_Gnomonic / Sky2Pix_Gnomonic). Regenerate via the project tooling if tolerances drift.
 */
class GnomonicTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new Gnomonic();
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
        final Gnomonic projection = new Gnomonic();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonTrivialPoint() {
        final Gnomonic projection = new Gnomonic();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(68.68091512616529, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final Gnomonic projection = new Gnomonic();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Gnomonic projection = new Gnomonic();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final Gnomonic projection = new Gnomonic();
        assertTrue(projection.hasInverse());
    }


    @Test
    void testAstropyPix2SkyReference() {
        final Gnomonic projection = new Gnomonic();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(59.6552416079002, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final Gnomonic projection = new Gnomonic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(23.3909040370103, result[0], DOUBLE_TOLERANCE);
        assertEquals(-23.3909040370103, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final Gnomonic projection = new Gnomonic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-28.6478897565412, result[0], DOUBLE_TOLERANCE);
        assertEquals(-49.6196005879613, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void sky2PixRejectsBackHemisphere() {
        final Gnomonic projection = new Gnomonic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(0.0, -45.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testEquatorSingularity() {
        final Gnomonic projection = new Gnomonic();
        final Transform inverse = projection.getInverse();
        final double[] result = inverse.evaluate(0.0, 0.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Gnomonic projection = new Gnomonic();
        final double[] inputs = new double[]{99.0, 99.0, -15.0, 30.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        projection.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(-153.434948822922, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(59.6552416079002, outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }

    @Test
    void sky2PixAtEquatorReturnsNaN() {
        final Gnomonic projection = new Gnomonic(Direction.SKY2PIX);
        final double[] result = projection.evaluate(0.0, 0.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testNearDivergenceTheta() {
        final Gnomonic projection = new Gnomonic(Direction.SKY2PIX);
        final double[] pix = projection.evaluate(0.0, 1.0);
        assertTrue(Double.isFinite(pix[0]) && Double.isFinite(pix[1]));
        final Transform inverse = projection.getInverse();
        final double[] recovered = inverse.evaluate(pix);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(1.0, recovered[1], DOUBLE_TOLERANCE);
    }
}
