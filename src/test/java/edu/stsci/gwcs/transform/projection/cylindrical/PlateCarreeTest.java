package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.PlateCarree
 * (Pix2Sky_PlateCarree / Sky2Pix_PlateCarree). Regenerate via the project tooling if tolerances drift.
 */
class PlateCarreeTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new PlateCarree();
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
    void pix2SkyIdentity() {
        final PlateCarree projection = new PlateCarree();
        final double[] result = projection.evaluate(30.0, 45.0);
        assertEquals(30.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void sky2PixIdentity() {
        final PlateCarree projection = new PlateCarree(Direction.SKY2PIX);
        final double[] result = projection.evaluate(30.0, 45.0);
        assertEquals(30.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void roundTrip() {
        final PlateCarree forward = new PlateCarree();
        final Transform inverse = forward.getInverse();
        final double[] intermediate = forward.evaluate(30.0, 45.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(30.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void hasInverse() {
        final PlateCarree projection = new PlateCarree();
        assertTrue(projection.hasInverse());
    }

    @Test
    void inputOutputCount() {
        final PlateCarree projection = new PlateCarree();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final PlateCarree projection = new PlateCarree();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(10.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final PlateCarree projection = new PlateCarree();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-15.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final PlateCarree projection = new PlateCarree(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(60.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final PlateCarree projection = new PlateCarree(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-30.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final PlateCarree projection = new PlateCarree();
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
