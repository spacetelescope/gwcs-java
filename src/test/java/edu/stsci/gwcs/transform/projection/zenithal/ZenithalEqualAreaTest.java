package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection.Direction;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.projection.AbstractProjectionContractTest;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.Transform;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.ZenithalEqualArea
 * (Pix2Sky_ZenithalEqualArea / Sky2Pix_ZenithalEqualArea). Regenerate via the project tooling if tolerances drift.
 */
class ZenithalEqualAreaTest extends AbstractProjectionContractTest {

    @Override
    protected Projection factory() {
        return new ZenithalEqualArea();
    }

    @Override
    protected java.util.stream.Stream<double[]> roundTripSamples() {
        return java.util.stream.Stream.of(
                new double[]{0.0, 60.0},
                new double[]{45.0, 30.0},
                new double[]{-30.0, 45.0}
        );
    }

    @Test
    void testReferencePoint() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final double[] result = projection.evaluate(0.0, 0.0);
        assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNonTrivialPoint() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final double[] result = projection.evaluate(10.0, 20.0);
        assertEquals(153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(67.49492687516586, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        assertTrue(projection.hasInverse());
        final Transform inverse = projection.getInverse();
        final double[] intermediate = projection.evaluate(10.0, 20.0);
        final double[] recovered = inverse.evaluate(intermediate);
        assertEquals(10.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        assertEquals(2, projection.getInputCount());
        assertEquals(2, projection.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        assertTrue(projection.hasInverse());
    }

    @Test
    void testAstropyPix2SkyReference() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final double[] result = projection.evaluate(-15.0, 30.0);
        assertEquals(-153.434948822922, result[0], DOUBLE_TOLERANCE);
        assertEquals(55.9605855920904, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference1() {
        final ZenithalEqualArea projection = new ZenithalEqualArea(Direction.SKY2PIX);
        final double[] result = projection.evaluate(45.0, 60.0);
        assertEquals(20.9717108314201, result[0], DOUBLE_TOLERANCE);
        assertEquals(-20.9717108314201, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testAstropySky2PixReference2() {
        final ZenithalEqualArea projection = new ZenithalEqualArea(Direction.SKY2PIX);
        final double[] result = projection.evaluate(-30.0, 45.0);
        assertEquals(-21.9261455640997, result[0], DOUBLE_TOLERANCE);
        assertEquals(-37.9771981311717, result[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testPoleRoundTrip() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, 90.0);
        assertEquals(0.0, pix[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, pix[1], DOUBLE_TOLERANCE);
        final double[] sky = projection.evaluate(pix);
        assertEquals(0.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
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

    @Test
    void testSouthPoleRoundTrip() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final Transform inverse = projection.getInverse();
        final double[] pix = inverse.evaluate(0.0, -90.0);
        final double[] sky = projection.evaluate(pix);
        assertEquals(0.0, sky[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, sky[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNegativeThetaRoundTrip() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final Transform inverse = projection.getInverse();
        for (final double theta : new double[]{-45.0, -89.0}) {
            final double[] pix = inverse.evaluate(30.0, theta);
            final double[] sky = projection.evaluate(pix);
            assertEquals(30.0, sky[0], DOUBLE_TOLERANCE);
            assertEquals(theta, sky[1], DOUBLE_TOLERANCE);
        }
    }

    @Test
    void testGetInverseThreadSafety() throws Exception {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        final List<Future<Transform>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            futures.add(executor.submit(() -> projection.getInverse()));
        }

        final List<Transform> results = new ArrayList<>();
        for (Future<Transform> future : futures) {
            results.add(future.get());
        }

        final Transform first = results.get(0);
        for (Transform result : results) {
            assertSame(first, result);
        }

        executor.shutdown();
    }

    @Test
    void testGetInverseOfInverseReturnsSameInstance() {
        final ZenithalEqualArea projection = new ZenithalEqualArea();
        final Transform inverse = projection.getInverse();
        final Transform original = inverse.getInverse();
        assertSame(projection, original);
    }
}
