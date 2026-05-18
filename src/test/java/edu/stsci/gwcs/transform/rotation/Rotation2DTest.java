package edu.stsci.gwcs.transform.rotation;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

class Rotation2DTest {
    @Test
    void testIdentityRotation() {
        final Rotation2D rotation = new Rotation2D(0.0);
        final double[] outputs = rotation.evaluate(3.0, 4.0);
        assertEquals(3.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(4.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testQuarterTurn() {
        final Rotation2D rotation = new Rotation2D(90.0);
        final double[] outputs = rotation.evaluate(1.0, 0.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(1.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testFortyFiveDegrees() {
        final Rotation2D rotation = new Rotation2D(45.0);
        final double[] outputs = rotation.evaluate(1.0, 0.0);
        final double cos45 = Math.cos(Math.toRadians(45.0));
        final double sin45 = Math.sin(Math.toRadians(45.0));
        assertEquals(cos45, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(sin45, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final Rotation2D rotation = new Rotation2D(37.5);
        assertTrue(rotation.hasInverse());

        final Transform inverse = rotation.getInverse();
        final double[] intermediate = rotation.evaluate(3.0, 4.0);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(3.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(4.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final Rotation2D rotation = new Rotation2D(0.0);
        assertEquals(2, rotation.getInputCount());
        assertEquals(2, rotation.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final Rotation2D rotation = new Rotation2D(0.0);
        assertTrue(rotation.hasInverse());
    }

    @Test
    void nonFiniteAngleThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Rotation2D(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Rotation2D(Double.POSITIVE_INFINITY));
    }

    @Test
    void rotateBy90DegreesIsExact() {
        // WcsMath gives exact sin/cos at 90° multiples, so rotating (1, 0)
        // by 90° produces exactly (0, 1) with no rounding error.
        final Rotation2D r = new Rotation2D(90.0);
        final double[] out = r.evaluate(1.0, 0.0);
        assertEquals(0.0, out[0]);
        assertEquals(1.0, out[1]);
    }

    @Test
    void rotateBy180DegreesIsExact() {
        final Rotation2D r = new Rotation2D(180.0);
        final double[] out = r.evaluate(3.0, 4.0);
        assertEquals(-3.0, out[0]);
        assertEquals(-4.0, out[1]);
    }

    @Test
    void rotateBy30DegreesMatchesReference() {
        final Rotation2D r = new Rotation2D(30.0);
        final double[] out = r.evaluate(1.0, 0.0);
        assertEquals(0.8660254037844387, out[0], DOUBLE_TOLERANCE);
        assertEquals(0.49999999999999994, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void rotateNegativeAngleMatchesReference() {
        final Rotation2D r = new Rotation2D(-60.0);
        final double[] out = r.evaluate(2.0, 3.0);
        assertEquals(3.598076211353316, out[0], DOUBLE_TOLERANCE);
        assertEquals(-0.23205080756887675, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Rotation2D rotation = new Rotation2D(45.0);
        final double[] sampleInputs = {1.0, 0.0};
        final double[] expected = rotation.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 1.0, 0.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        rotation.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
