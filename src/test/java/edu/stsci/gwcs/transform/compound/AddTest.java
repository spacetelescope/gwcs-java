package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Affine;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.functional.Scale;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class AddTest {

    @Test
    void testTwoTransforms() {
        final Add add = new Add(new Transform[]{new Shift(1.0), new Shift(2.0)});
        // (x+1) + (x+2) = 2x+3
        assertEquals(13.0, add.evaluate(5.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testThreeTransforms() {
        final Add add = new Add(new Transform[]{new Shift(1.0), new Shift(2.0), new Shift(3.0)});
        // (x+1) + (x+2) + (x+3) = 3x+6
        assertEquals(36.0, add.evaluate(10.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCounts() {
        final Add add = new Add(new Transform[]{new Shift(1.0), new Scale(2.0)});
        assertEquals(1, add.getInputCount());
        assertEquals(1, add.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final Add add = new Add(new Transform[]{new Shift(1.0), new Shift(2.0)});
        assertFalse(add.hasInverse());
    }

    @Test
    void testTooFewTransformsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Add(new Transform[]{new Shift(1.0)}));
    }

    @Test
    void testNullElementThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Add(new Transform[]{new Shift(1.0), null}));
    }

    @Test
    void testMismatchedCountsThrows() {
        final Transform twoOut = new Transform() {
            @Override public int getInputCount() { return 1; }
            @Override public int getOutputCount() { return 2; }
            @Override public void evaluate(double[] in, int inOff, double[] out, int outOff) {}
        };
        assertThrows(IllegalArgumentException.class,
                () -> new Add(new Transform[]{new Shift(1.0), twoOut}));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Add add = new Add(new Transform[]{new Shift(1.0), new Shift(2.0)});
        final double[] inputs = {99.0, 5.0, 99.0};
        final double[] outputs = {77.0, 77.0, 77.0};
        add.evaluate(inputs, 1, outputs, 2);
        assertEquals(77.0, outputs[0]);
        assertEquals(77.0, outputs[1]);
        assertEquals(13.0, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testMultiDimensionalAdd() {
        // Affine1: [[2, 0], [0, 3]] with translation [1, 1] -> (2x+1, 3y+1)
        // Affine2: [[1, 1], [1, 1]] with translation [0, 0] -> (x+y, x+y)
        // Add: element-wise sum -> (2x+1 + x+y, 3y+1 + x+y)
        // At (1, 2): Affine1=(3, 7), Affine2=(3, 3), sum=(6, 10)
        final Affine affine1 = new Affine(
                new double[][]{{2.0, 0.0}, {0.0, 3.0}},
                new double[]{1.0, 1.0}
        );
        final Affine affine2 = new Affine(
                new double[][]{{1.0, 1.0}, {1.0, 1.0}},
                new double[]{0.0, 0.0}
        );
        final Add add = new Add(new Transform[]{affine1, affine2});

        assertEquals(2, add.getInputCount());
        assertEquals(2, add.getOutputCount());

        final double[] result = add.evaluate(1.0, 2.0);
        assertEquals(6.0, result[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, result[1], DOUBLE_TOLERANCE);
    }
}
