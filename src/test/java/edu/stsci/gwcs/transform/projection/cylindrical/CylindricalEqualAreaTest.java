package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.CylindricalEqualArea
 * (Pix2Sky_CylindricalEqualArea / Sky2Pix_CylindricalEqualArea). Regenerate via the project tooling if tolerances drift.
 */
class CylindricalEqualAreaTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new CylindricalEqualArea(1.0);
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
        final CylindricalEqualArea projection = new CylindricalEqualArea(1.0);
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTrip() {
        final CylindricalEqualArea forward = new CylindricalEqualArea(1.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(45.0, 30.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(45.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripDefaultLambda() {
        final CylindricalEqualArea forward = new CylindricalEqualArea();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(60.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(60.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final CylindricalEqualArea projection = new CylindricalEqualArea();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final CylindricalEqualArea projection = new CylindricalEqualArea();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void pix2SkyReturnsNaNForOutOfDomainY() {
        // Forward pass routes y through WcsMath.asind, which returns NaN for out-of-domain
        // arguments. phi is passed straight through (since x is independent of the
        // out-of-domain check), matching wcslib's per-coordinate failure semantics.
        final CylindricalEqualArea projection = new CylindricalEqualArea(1.0);
        final double[] result = projection.evaluate(0.0, 1000.0);
        assertTrue(Double.isNaN(result[1]));
    }

    @Test
    void rejectsZeroLambda() {
        assertThrows(IllegalArgumentException.class, () -> new CylindricalEqualArea(0.0));
    }

    @Test
    void rejectsNegativeLambda() {
        assertThrows(IllegalArgumentException.class, () -> new CylindricalEqualArea(-0.5));
    }

    @Test
    void rejectsLambdaGreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> new CylindricalEqualArea(1.5));
    }

    @Test
    void testAstropyLam1Pix2SkyReference1() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(1.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(20.4301889998248, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam1Pix2SkyReference2() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(1.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-15.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(31.5739613296321, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam1Sky2PixReference1() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(1.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(49.6196005879613, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam1Sky2PixReference2() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(1.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-30.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(40.5142342270698, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam05Pix2SkyReference1() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(0.5);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(10.0514783946115, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam05Pix2SkyReference2() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(0.5);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-15.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(15.1768582759705, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam05Sky2PixReference1() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(0.5, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(99.2392011759226, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyLam05Sky2PixReference2() {
        final CylindricalEqualArea projection = new CylindricalEqualArea(0.5, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-30.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(81.0284684541395, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final CylindricalEqualArea projection = new CylindricalEqualArea();
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
