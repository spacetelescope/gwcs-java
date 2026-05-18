package edu.stsci.gwcs.transform.projection.pseudoconic;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.BonneEqualArea
 * (Pix2Sky_BonneEqualArea / Sky2Pix_BonneEqualArea). Regenerate via the project tooling if tolerances drift.
 */
class BonneEqualAreaTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new BonneEqualArea(45.0);
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 30.0},
                new double[]{30.0, 60.0},
                new double[]{10.0, 20.0}
        );
    }

    @Test
    void pix2SkyOrigin() {
        final BonneEqualArea projection = new BonneEqualArea(45.0);
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripOrigin() {
        final BonneEqualArea forward = new BonneEqualArea(45.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(0.0, 0.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNonTrivial() {
        final BonneEqualArea forward = new BonneEqualArea(45.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(60.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNonTrivial2() {
        final BonneEqualArea forward = new BonneEqualArea(30.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(10.0, 20.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTripNegativeTheta1() {
        final BonneEqualArea forward = new BonneEqualArea(-45.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(10.0, -50.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-50.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final BonneEqualArea projection = new BonneEqualArea(45.0);
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final BonneEqualArea projection = new BonneEqualArea(45.0);
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void roundTripTheta1At90() {
        final BonneEqualArea forward = new BonneEqualArea(90.0);
        final Transform inverse = forward.getInverse();
        final double[] intermediate = inverse.evaluate(30.0, 60.0);
        final double[] recovered = forward.evaluate(intermediate);
        assertEquals(30.0, recovered[0], 1e-8);
        assertEquals(60.0, recovered[1], 1e-8);
    }

    @Test
    void rejectsZeroTheta1() {
        assertThrows(IllegalArgumentException.class, () -> new BonneEqualArea(0.0));
    }

    @Test
    void noArgConstructorDefaultsToTheta1Of45() {
        final BonneEqualArea defaultProjection = new BonneEqualArea();
        final BonneEqualArea explicit = new BonneEqualArea(45.0);
        final double[] defaultResult = defaultProjection.evaluate(10.0, 20.0);
        final double[] explicitResult = explicit.evaluate(10.0, 20.0);
        assertEquals(explicitResult[0], defaultResult[0], DOUBLE_TOLERANCE);
        assertEquals(explicitResult[1], defaultResult[1], DOUBLE_TOLERANCE);
    }

    @Test
    void pix2SkyAtPoleReturnsPhi0() {
        final BonneEqualArea projection = new BonneEqualArea(45.0);
        final double[] result = projection.evaluate(0.0, 90.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNegativeTheta1AstropyReference() {
        final BonneEqualArea projection = new BonneEqualArea(-45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, -30.0);
        assertEquals(37.1110258326922, result[0], DOUBLE_TOLERANCE);
        assertEquals(-40.2518314032991, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final BonneEqualArea projection = new BonneEqualArea(45.0);
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.6274824400673, result[0], DOUBLE_TOLERANCE);
        assertEquals(19.3946617542375, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final BonneEqualArea projection = new BonneEqualArea(45.0);
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-17.1815786082391, result[0], DOUBLE_TOLERANCE);
        assertEquals(28.4602884846872, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final BonneEqualArea projection = new BonneEqualArea(45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(21.4537026343075, result[0], DOUBLE_TOLERANCE);
        assertEquals(65.8448315432697, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final BonneEqualArea projection = new BonneEqualArea(45.0, Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-20.7318709609446, result[0], DOUBLE_TOLERANCE);
        assertEquals(48.8823366688836, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final BonneEqualArea projection = new BonneEqualArea();
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
