package edu.stsci.gwcs.transform;

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
        final SphericalToCartesian transform = new SphericalToCartesian(true);
        assertTrue(transform.hasInverse());
        assertInstanceOf(CartesianToSpherical.class, transform.getInverse());
    }

    @Test
    void testRoundTrip() {
        final SphericalToCartesian s2c = new SphericalToCartesian(true);
        final CartesianToSpherical c2s = new CartesianToSpherical(true);

        final double[] coords = {123.456, 45.678};
        final double[] cartesian = s2c.evaluate(coords);
        final double[] recovered = c2s.evaluate(cartesian);

        assertEquals(coords[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(coords[1], recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTripNegativeLongitude() {
        final SphericalToCartesian s2c = new SphericalToCartesian(false);
        final CartesianToSpherical c2s = new CartesianToSpherical(false);

        final double[] coords = {-30.0, -15.0};
        final double[] cartesian = s2c.evaluate(coords);
        final double[] recovered = c2s.evaluate(cartesian);

        assertEquals(coords[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(coords[1], recovered[1], DOUBLE_TOLERANCE);
    }
}
