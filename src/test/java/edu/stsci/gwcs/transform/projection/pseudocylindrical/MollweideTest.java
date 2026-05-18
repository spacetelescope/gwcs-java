package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.Mollweide
 * (Pix2Sky_Mollweide / Sky2Pix_Mollweide). Regenerate via the project tooling if tolerances drift.
 */
class MollweideTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new Mollweide();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 30.0},
                new double[]{45.0, 60.0},
                new double[]{-30.0, -45.0}
        );
    }


    private static final double TOLERANCE = 1e-10;

    @Test
    void testOrigin() {
        final Mollweide mol = new Mollweide();
        final double[] result = mol.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], TOLERANCE);
        assertEquals(0.0, result[1], TOLERANCE);
    }

    @Test
    void testNonTrivialSky2Pix() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final double[] sky2pix = mol.evaluate(45.0, 30.0);
        assertEquals(37.061247270088934, sky2pix[0], TOLERANCE);
        assertEquals(32.733293497061820, sky2pix[1], TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final Mollweide mol = new Mollweide();
        assertTrue(mol.hasInverse());

        final Transform inverse = mol.getInverse();
        final double[] intermediate = mol.evaluate(20.0, 35.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(20.0, recovered[0], TOLERANCE);
        assertEquals(35.0, recovered[1], TOLERANCE);
    }

    @Test
    void testRoundTripSky2Pix() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final Transform inverse = mol.getInverse();
        final double[] intermediate = mol.evaluate(60.0, 45.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(60.0, recovered[0], TOLERANCE);
        assertEquals(45.0, recovered[1], TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Mollweide mol = new Mollweide();
        assertEquals(2, mol.getInputCount());
        assertEquals(2, mol.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final Mollweide mol = new Mollweide();
        assertTrue(mol.hasInverse());
    }

    @Test
    void testPoleRoundTrip() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final Transform inverse = mol.getInverse();
        final double[] pix = mol.evaluate(0.0, 90.0);
        final double[] sky = inverse.evaluate(pix);
        assertEquals(0.0, sky[0], TOLERANCE);
        assertEquals(90.0, sky[1], TOLERANCE);
    }

    @Test
    void testSouthPoleRoundTrip() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final Transform inverse = mol.getInverse();
        final double[] pix = mol.evaluate(0.0, -90.0);
        final double[] sky = inverse.evaluate(pix);
        assertEquals(0.0, sky[0], TOLERANCE);
        assertEquals(-90.0, sky[1], TOLERANCE);
    }

    @Test
    void testOutOfBoundsReturnsNaN() {
        final Mollweide mol = new Mollweide();
        final double R0 = Math.toDegrees(1.0);
        final double outOfBoundsY = 3.0 * Math.sqrt(2.0) * R0;
        final double[] result = mol.evaluate(0.0, outOfBoundsY);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void testNearPoleRoundTrip() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final Transform inverse = mol.getInverse();
        final double[] thetas = {89.9, 89.99, 89.999, 89.9999, -89.9, -89.99, -89.999, -89.9999};
        for (final double theta : thetas) {
            final double[] pix = mol.evaluate(10.0, theta);
            assertFalse(Double.isNaN(pix[0]), "sky2pix returned NaN for theta=" + theta);
            assertFalse(Double.isNaN(pix[1]), "sky2pix returned NaN for theta=" + theta);
            final double[] sky = inverse.evaluate(pix);
            // Near-pole round-trip tolerance is looser due to inherent floating-point
            // precision loss in the asin chain at extreme latitudes
            assertEquals(10.0, sky[0], 1e-4, "phi round-trip failed for theta=" + theta);
            assertEquals(theta, sky[1], 1e-4, "theta round-trip failed for theta=" + theta);
        }
    }

    @Test
    void testSky2PixThetaZero() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final double[] result = mol.evaluate(45.0, 0.0);
        assertEquals(40.51423422706978, result[0], TOLERANCE);
        assertEquals(0.0, result[1], TOLERANCE);
    }

    @Test
    void testPoleBoundaryPix2SkyReturnsPhi0() {
        final Mollweide mol = new Mollweide();
        final double R0 = Math.toDegrees(1.0);
        final double poleY = Math.sqrt(2.0) * R0;
        final double[] result = mol.evaluate(10.0, poleY);
        assertEquals(0.0, result[0], TOLERANCE);
        assertEquals(90.0, result[1], TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference1() {
        final Mollweide mol = new Mollweide();
        final double[] result = mol.evaluate(10.0, 20.0);
        assertEquals(11.46184125302, result[0], TOLERANCE);
        assertEquals(18.1224472660321, result[1], TOLERANCE);
    }

    @Test
    void testAstropyPix2SkyReference2() {
        final Mollweide mol = new Mollweide();
        final double[] result = mol.evaluate(-15.0, 30.0);
        assertEquals(-17.935369446321, result[0], TOLERANCE);
        assertEquals(27.4130248361496, result[1], TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final double[] result = mol.evaluate(45.0, 60.0);
        assertEquals(26.217670117976, result[0], TOLERANCE);
        assertEquals(61.7749770891374, result[1], TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final double[] result = mol.evaluate(-30.0, 45.0);
        assertEquals(-21.7671445818261, result[0], TOLERANCE);
        assertEquals(47.9722362498186, result[1], TOLERANCE);
    }

    @Test
    void sky2PixNearBoundaryConverges() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final Transform inverse = mol.getInverse();
        for (final double theta : new double[]{89.999999999, -89.999999999}) {
            final double[] pix = mol.evaluate(0.0, theta);
            assertFalse(Double.isNaN(pix[0]), "sky2pix NaN at theta=" + theta);
            assertFalse(Double.isNaN(pix[1]), "sky2pix NaN at theta=" + theta);
            final double[] sky = inverse.evaluate(pix);
            assertEquals(0.0, sky[0], 1e-6);
            assertEquals(theta, sky[1], 1e-6);
        }
    }

    @Test
    void pix2SkyOutOfRangeReturnsNaN() {
        // y beyond the SQRT2 * R0 ellipse half-height triggers the early-return NaN guard.
        final Mollweide mol = new Mollweide();
        final double[] result = mol.evaluate(0.0, 1.0e9);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void pix2SkyAtNaNInputReturnsNaN() {
        final Mollweide mol = new Mollweide();
        final double[] result = mol.evaluate(Double.NaN, 0.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void sky2PixAtNaNInputReturnsNaN() {
        final Mollweide mol = new Mollweide(Direction.SKY2PIX);
        final double[] result = mol.evaluate(Double.NaN, 0.0);
        assertTrue(Double.isNaN(result[0]) && Double.isNaN(result[1]));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Mollweide projection = new Mollweide();
        final double[] sampleInputs = {10.0, 45.0};
        final double[] expected = projection.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 10.0, 45.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        projection.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], TOLERANCE);
        assertEquals(expected[1], outputs[2], TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
