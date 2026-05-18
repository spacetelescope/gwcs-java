package edu.stsci.gwcs.transform.geometry;

import org.junit.jupiter.api.Test;
import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

class CartesianToSphericalTest {
    @Test
    void testAxes() {
        final CartesianToSpherical transform = new CartesianToSpherical(180);
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
        final CartesianToSpherical transform = new CartesianToSpherical(180);

        final double[] inputs = { 1.0, 1.0, Math.sqrt(2.0) };
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(45.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(45.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithoutLongitudeWrapping() {
        final CartesianToSpherical transform = new CartesianToSpherical(180);

        final double[] inputs = { 1.0, -1.0, 0.0 };
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(-45.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithLongitudeWrapping() {
        final CartesianToSpherical transform = new CartesianToSpherical(360);

        final double[] inputs = { 1.0, -1.0, 0.0 };
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(315.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testNorthPoleReturnsLongitudeZero() {
        final CartesianToSpherical transform = new CartesianToSpherical(180);
        final double[] outputs = transform.evaluate(0.0, 0.0, 1.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(90.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSouthPoleReturnsLongitudeZero() {
        final CartesianToSpherical transform = new CartesianToSpherical(180);
        final double[] outputs = transform.evaluate(0.0, 0.0, -1.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testOriginReturnsZero() {
        final CartesianToSpherical transform = new CartesianToSpherical(180);
        final double[] outputs = transform.evaluate(0.0, 0.0, 0.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverse() {
        final CartesianToSpherical transform = new CartesianToSpherical(360);

        assertTrue(transform.hasInverse());

        final Transform inverse = transform.getInverse();
        assertNotNull(inverse);
        assertInstanceOf(SphericalToCartesian.class, inverse);
    }

    @Test
    void testRoundTrip() {
        final CartesianToSpherical c2s = new CartesianToSpherical(360);
        final SphericalToCartesian s2c = new SphericalToCartesian(360);

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

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final CartesianToSpherical transform = new CartesianToSpherical(180);
        final double[] sampleInputs = {1.0, 1.0, Math.sqrt(2.0)};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, sampleInputs[0], sampleInputs[1], sampleInputs[2], 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}