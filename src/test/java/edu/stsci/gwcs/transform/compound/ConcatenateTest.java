package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.functional.Affine;
import edu.stsci.gwcs.transform.functional.Scale;
import edu.stsci.gwcs.transform.functional.Shift;

class ConcatenateTest {
    @Nested
    public class Constructor {
        @Test
        void testEmptyArray() {
            assertThrows(IllegalArgumentException.class, () -> new Concatenate(new Transform[0]));
        }

        @Test
        void testNullElement() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Concatenate(new Transform[]{new Shift(1.0), null}));
        }
    }

    @Nested
    public class InputOutputCounts {
        @Test
        void testGetInputAndOutputCounts() {
            final Transform scale = new Scale(2.0);

            final double[][] matrix = {
                    {1.0, 0.0},
                    {0.0, 1.0}
            };
            final double[] translation = {0.0, 0.0};
            final Transform affine = new Affine(matrix, translation);

            final Concatenate concatenate = new Concatenate(new Transform[]{scale, affine});

            assertEquals(3, concatenate.getInputCount());
            assertEquals(3, concatenate.getOutputCount());
        }
    }

    @Nested
    public class Evaluate {
        @Test
        void testTwoTransforms() {
            final Transform shift = new Shift(10.0);
            final Transform scale = new Scale(5.0);
            final Concatenate concatenate = new Concatenate(new Transform[]{shift, scale});

            final double[] inputs = {1.0, 2.0};
            final double[] outputs = new double[2];

            concatenate.evaluate(inputs, 0, outputs, 0);

            assertEquals(11.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(10.0, outputs[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testThreeTransforms() {
            final Transform shift1 = new Shift(1.0);
            final Transform scale = new Scale(2.0);
            final Transform shift2 = new Shift(5.0);

            final Concatenate concatenate = new Concatenate(new Transform[]{shift1, scale, shift2});

            final double[] inputs = {10.0, 10.0, 10.0};
            final double[] outputs = new double[3];

            concatenate.evaluate(inputs, 0, outputs, 0);

            assertEquals(11.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(20.0, outputs[1], DOUBLE_TOLERANCE);
            assertEquals(15.0, outputs[2], DOUBLE_TOLERANCE);
        }
    }

    @Nested
    public class Inverse {
        @Test
        void testInvertible() {
            final Transform shift = new Shift(5.0);
            final Transform scale = new Scale(2.0);

            final Concatenate concat = new Concatenate(new Transform[]{shift, scale});
            assertTrue(concat.hasInverse());

            final Transform inverse = concat.getInverse();
            final double[] initial = {1.0, 10.0};
            final double[] intermediate = new double[2];
            final double[] recovered = new double[2];

            concat.evaluate(initial, 0, intermediate, 0);
            inverse.evaluate(intermediate, 0, recovered, 0);

            assertEquals(initial[0], recovered[0], DOUBLE_TOLERANCE);
            assertEquals(initial[1], recovered[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testNonInvertible() {
            final Transform shift = new Shift(5.0);
            final Transform scale = new Scale(0.0);

            final Concatenate concat = new Concatenate(new Transform[]{shift, scale});
            assertFalse(concat.hasInverse());
        }

        @Test
        void testGetInverseThrowsWhenNonInvertible() {
            final Transform shift = new Shift(5.0);
            final Transform scale = new Scale(0.0);

            final Concatenate concat = new Concatenate(new Transform[]{shift, scale});
            assertThrows(UnsupportedOperationException.class, concat::getInverse);
        }
    }

    @Nested
    public class MixedDimensions {
        @Test
        void testConcatenateWithDifferentDimensions() {
            final double[][] matrix = {{1.0, 0.0}, {0.0, 2.0}};
            final double[] translation = {0.0, 0.0};
            final Transform affine = new Affine(matrix, translation);
            final Transform shift = new Shift(5.0);

            final Concatenate concat = new Concatenate(new Transform[]{affine, shift});
            assertEquals(3, concat.getInputCount());
            assertEquals(3, concat.getOutputCount());

            final double[] outputs = concat.evaluate(1.0, 2.0, 10.0);
            assertEquals(1.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(4.0, outputs[1], DOUBLE_TOLERANCE);
            assertEquals(15.0, outputs[2], DOUBLE_TOLERANCE);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Concatenate concat = new Concatenate(new Transform[]{new Shift(10.0), new Scale(5.0)});
        final double[] sampleInputs = {1.0, 2.0};
        final double[] expected = concat.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 1.0, 2.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        concat.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}