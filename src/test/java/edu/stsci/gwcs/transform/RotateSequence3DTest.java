package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class RotateSequence3DTest {
    @Test
    void testXRotation90() {
        // Rx(90): [x, y, z] -> [x, z, -y]
        final RotateSequence3D rot = new RotateSequence3D(new double[]{90.0}, "x");

        assertEquals(3, rot.getInputCount());
        assertEquals(3, rot.getOutputCount());

        double[] out = rot.evaluate(1.0, 0.0, 0.0);
        assertEquals(1.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[2], DOUBLE_TOLERANCE);

        out = rot.evaluate(0.0, 1.0, 0.0);
        assertEquals(0.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(-1.0, out[2], DOUBLE_TOLERANCE);

        out = rot.evaluate(0.0, 0.0, 1.0);
        assertEquals(0.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(1.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testYRotation90() {
        // Ry(90): [x, y, z] -> [-z, y, x]
        final RotateSequence3D rot = new RotateSequence3D(new double[]{90.0}, "y");

        double[] out = rot.evaluate(1.0, 0.0, 0.0);
        assertEquals(0.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(1.0, out[2], DOUBLE_TOLERANCE);

        out = rot.evaluate(0.0, 0.0, 1.0);
        assertEquals(-1.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testZRotation90() {
        // Rz(90): [x, y, z] -> [y, -x, z]
        final RotateSequence3D rot = new RotateSequence3D(new double[]{90.0}, "z");

        double[] out = rot.evaluate(1.0, 0.0, 0.0);
        assertEquals(0.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(-1.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[2], DOUBLE_TOLERANCE);

        out = rot.evaluate(0.0, 1.0, 0.0);
        assertEquals(1.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(0.0, out[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testMultiAxisRotation() {
        // Rz(45) * Rx(30) applied to [0, 1, 0]
        // Expected: [sqrt(6)/4, sqrt(6)/4, -0.5]
        final RotateSequence3D rot = new RotateSequence3D(
                new double[]{30.0, 45.0}, "xz"
        );

        final double[] out = rot.evaluate(0.0, 1.0, 0.0);
        final double sqrt6over4 = Math.sqrt(6.0) / 4.0;

        assertEquals(sqrt6over4, out[0], DOUBLE_TOLERANCE);
        assertEquals(sqrt6over4, out[1], DOUBLE_TOLERANCE);
        assertEquals(-0.5, out[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final RotateSequence3D rot = new RotateSequence3D(
                new double[]{30.0, 45.0, 60.0}, "xyz"
        );
        assertTrue(rot.hasInverse());

        final Transform inverse = rot.getInverse();
        final double[] original = {1.0, 2.0, 3.0};
        final double[] rotated = rot.evaluate(original);
        final double[] recovered = inverse.evaluate(rotated);

        assertEquals(original[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(original[1], recovered[1], DOUBLE_TOLERANCE);
        assertEquals(original[2], recovered[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testIdentityRotation() {
        final RotateSequence3D rot = new RotateSequence3D(
                new double[]{0.0, 0.0}, "xz"
        );

        final double[] out = rot.evaluate(1.0, 2.0, 3.0);
        assertEquals(1.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(2.0, out[1], DOUBLE_TOLERANCE);
        assertEquals(3.0, out[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsEmptyAngles() {
        assertThrows(IllegalArgumentException.class,
                () -> new RotateSequence3D(new double[]{}, ""));
    }

    @Test
    void testRejectsInvalidAxis() {
        assertThrows(IllegalArgumentException.class,
                () -> new RotateSequence3D(new double[]{90.0}, "w"));
    }

    @Test
    void testRejectsAngleCountMismatch() {
        assertThrows(IllegalArgumentException.class,
                () -> new RotateSequence3D(new double[]{90.0, 45.0}, "x"));
    }

    @Test
    void testCaseInsensitive() {
        final RotateSequence3D lower = new RotateSequence3D(new double[]{45.0}, "x");
        final RotateSequence3D upper = new RotateSequence3D(new double[]{45.0}, "X");

        final double[] outLower = lower.evaluate(0.0, 1.0, 0.0);
        final double[] outUpper = upper.evaluate(0.0, 1.0, 0.0);

        assertEquals(outLower[0], outUpper[0], DOUBLE_TOLERANCE);
        assertEquals(outLower[1], outUpper[1], DOUBLE_TOLERANCE);
        assertEquals(outLower[2], outUpper[2], DOUBLE_TOLERANCE);
    }
}
