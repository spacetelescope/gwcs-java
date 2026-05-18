package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class RemapAxesTest {
    @Test
    void testIdentityMapping() {
        final RemapAxes remap = new RemapAxes(new int[]{0, 1, 2}, 3);
        assertEquals(3, remap.getInputCount());
        assertEquals(3, remap.getOutputCount());

        final double[] outputs = remap.evaluate(10.0, 20.0, 30.0);
        assertEquals(10.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(20.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(30.0, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testSwapAxes() {
        final RemapAxes remap = new RemapAxes(new int[]{1, 0}, 2);
        final double[] outputs = remap.evaluate(10.0, 20.0);
        assertEquals(20.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testSelectAxes() {
        final RemapAxes remap = new RemapAxes(new int[]{2, 0}, 3);
        assertEquals(3, remap.getInputCount());
        assertEquals(2, remap.getOutputCount());

        final double[] inputs = {10.0, 20.0, 30.0};
        final double[] outputs = new double[2];
        remap.evaluate(inputs, 0, outputs, 0);

        assertEquals(30.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testDuplicateAxesNotInvertible() {
        final RemapAxes remap = new RemapAxes(new int[]{0, 0}, 2);
        assertFalse(remap.hasInverse());
        assertThrows(UnsupportedOperationException.class, remap::getInverse);
    }

    @Test
    void testDroppingAxesNotInvertible() {
        final RemapAxes remap = new RemapAxes(new int[]{0}, 2);
        assertFalse(remap.hasInverse());
    }

    @Test
    void testPermutationInverse() {
        final RemapAxes remap = new RemapAxes(new int[]{2, 0, 1}, 3);
        assertTrue(remap.hasInverse());

        final Transform inverse = remap.getInverse();
        final double[] original = {10.0, 20.0, 30.0};
        final double[] intermediate = remap.evaluate(original);
        final double[] recovered = inverse.evaluate(intermediate);

        assertEquals(original[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(original[1], recovered[1], DOUBLE_TOLERANCE);
        assertEquals(original[2], recovered[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsOutOfBounds() {
        assertThrows(IllegalArgumentException.class,
                () -> new RemapAxes(new int[]{0, 3}, 3));
        assertThrows(IllegalArgumentException.class,
                () -> new RemapAxes(new int[]{-1}, 2));
    }

    @Test
    void testRejectsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new RemapAxes(new int[]{}, 2));
    }

    @Test
    void testRejectsNonPositiveInputCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new RemapAxes(new int[]{0}, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new RemapAxes(new int[]{0}, -1));
    }

    @Test
    void testSameArrayAliasing() {
        final RemapAxes remap = new RemapAxes(new int[]{1, 0}, 2);
        final double[] buffer = {10.0, 20.0, 0.0, 0.0};
        remap.evaluate(buffer, 0, buffer, 2);
        assertEquals(20.0, buffer[2], DOUBLE_TOLERANCE);
        assertEquals(10.0, buffer[3], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final RemapAxes remap = new RemapAxes(new int[]{2, 0}, 3);
        final double[] sampleInputs = {10.0, 20.0, 30.0};
        final double[] expected = remap.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 10.0, 20.0, 30.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        remap.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
