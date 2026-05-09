package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;
import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class CartesianToSphericalTest {
    @Test
    void testAxes() {
        final CartesianToSpherical transform = new CartesianToSpherical(false);
        final double[] inputs = new double[3];
        final double[] outputs = new double[2];

        // Longitude 0, Latitude 0
        inputs[0] = 1.0; inputs[1] = 0.0; inputs[2] = 0.0;
        transform.evaluate(inputs, 0, outputs, 0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);

        // Longitude 90, Latitude 0
        inputs[0] = 0.0; inputs[1] = 1.0; inputs[2] = 0.0;
        transform.evaluate(inputs, 0, outputs, 0);
        assertEquals(90.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);

        // Longitude 0, Latitude 90
        inputs[0] = 0.0; inputs[1] = 0.0; inputs[2] = 1.0;
        transform.evaluate(inputs, 0, outputs, 0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testGeneralCoordinate() {
        final CartesianToSpherical transform = new CartesianToSpherical(false);

        final double[] inputs = { 1.0, 1.0, Math.sqrt(2.0) };
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(45.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithoutLongitudeWrapping() {
        final CartesianToSpherical transform = new CartesianToSpherical(false);

        final double[] inputs = { 1.0, -1.0, 0.0 };
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(-45.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithLongitudeWrapping() {
        final CartesianToSpherical transform = new CartesianToSpherical(true);

        final double[] inputs = { 1.0, -1.0, 0.0 };
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(315.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverse() {
        final CartesianToSpherical transform = new CartesianToSpherical(true);

        assertTrue(transform.hasInverse());

        final Transform inverse = transform.getInverse();
        assertNotNull(inverse);
        assertInstanceOf(SphericalToCartesian.class, inverse);
    }

    @Test
    void testRoundTrip() {
        final CartesianToSpherical c2s = new CartesianToSpherical(true);
        final SphericalToCartesian s2c = new SphericalToCartesian(true);

        final double[] cartesian = {1.0, 1.0, Math.sqrt(2.0)};
        final double[] spherical = c2s.evaluate(cartesian);
        final double[] recovered = s2c.evaluate(spherical);

        final double r = Math.sqrt(cartesian[0] * cartesian[0]
                + cartesian[1] * cartesian[1]
                + cartesian[2] * cartesian[2]);
        assertEquals(cartesian[0] / r, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(cartesian[1] / r, recovered[1], DOUBLE_TOLERANCE);
        assertEquals(cartesian[2] / r, recovered[2], DOUBLE_TOLERANCE);
    }
}