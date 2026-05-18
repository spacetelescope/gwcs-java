package edu.stsci.gwcs.transform.geometry;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class SphericalToCartesianTest {
    @Test
    void testAxes() {
        final SphericalToCartesian transform = new SphericalToCartesian();

        // lon=0, lat=0 -> (1, 0, 0)
        double[] outputs = transform.evaluate(0.0, 0.0);
        assertEquals(1.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[2], DOUBLE_TOLERANCE);

        // lon=90, lat=0 -> (0, 1, 0)
        outputs = transform.evaluate(90.0, 0.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(1.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[2], DOUBLE_TOLERANCE);

        // lon=0, lat=90 -> (0, 0, 1)
        outputs = transform.evaluate(0.0, 90.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(1.0, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testGeneralCoordinate() {
        final SphericalToCartesian transform = new SphericalToCartesian();
        final double[] outputs = transform.evaluate(45.0, 45.0);

        assertEquals(0.5, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.5, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(Math.sqrt(2.0) / 2.0, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final SphericalToCartesian transform = new SphericalToCartesian();
        assertEquals(2, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void testInverse() {
        final SphericalToCartesian transform = new SphericalToCartesian(360);
        assertTrue(transform.hasInverse());
        assertInstanceOf(CartesianToSpherical.class, transform.getInverse());
    }

    @Test
    void testRoundTrip() {
        final SphericalToCartesian s2c = new SphericalToCartesian(360);
        final CartesianToSpherical c2s = new CartesianToSpherical(360);

        final double[] coords = {123.456, 45.678};
        final double[] cartesian = s2c.evaluate(coords);
        final double[] recovered = c2s.evaluate(cartesian);

        assertEquals(coords[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(coords[1], recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripNegativeLongitude() {
        final SphericalToCartesian s2c = new SphericalToCartesian(180);
        final CartesianToSpherical c2s = new CartesianToSpherical(180);

        final double[] coords = {-30.0, -15.0};
        final double[] cartesian = s2c.evaluate(coords);
        final double[] recovered = c2s.evaluate(cartesian);

        assertEquals(coords[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(coords[1], recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final SphericalToCartesian transform = new SphericalToCartesian();
        final double[] sampleInputs = {45.0, 45.0};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 45.0, 45.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(expected[2], outputs[3], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[4]);
        assertEquals(77.0, outputs[5]);
    }
}
