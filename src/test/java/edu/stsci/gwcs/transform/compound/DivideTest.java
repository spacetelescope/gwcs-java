package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.functional.Affine;
import edu.stsci.gwcs.transform.functional.Scale;
import edu.stsci.gwcs.transform.functional.Shift;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

class DivideTest {
    @Nested
    public class Constructor {
        @Test
        void testInsufficientTransforms() {
            final Transform[] transforms = { new Shift(1.0) };
            assertThrows(IllegalArgumentException.class, () -> new Divide(transforms));
        }

        @Test
        void testNullElementThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Divide(new Transform[]{new Shift(1.0), null}));
        }

        @Test
        void testMismatchedDimensions() {
            final Transform shift = new Shift(1.0);

            final double[][] matrix = {{1.0, 0.0}, {0.0, 1.0}};
            final double[] translation = {0.0, 0.0};
            final Transform affine = new Affine(matrix, translation);

            final Transform[] transforms = new Transform[]{shift, affine};

            assertThrows(IllegalArgumentException.class, () -> new Divide(transforms));
        }
    }

    @Nested
    public class Evaluate {
        @Test
        void testSingleDimension() {
            final Transform shift1 = new Shift(10.0);
            final Transform shift2 = new Shift(2.0);
            final Transform divide = new Divide(new Transform[]{shift1, shift2});

            final double[] inputs = {6.0};
            final double[] outputs = new double[1];

            divide.evaluate(inputs, 0, outputs, 0);

            assertEquals(2.0, outputs[0], DOUBLE_TOLERANCE);
        }

        @Test
        void testDivisionByZeroProducesInfinity() {
            final Transform shift5 = new Shift(5.0);
            final Transform zero = new Scale(0.0);
            final Transform divide = new Divide(new Transform[]{shift5, zero});

            final double[] outputs = new double[1];
            divide.evaluate(new double[]{0.0}, 0, outputs, 0);
            assertTrue(Double.isInfinite(outputs[0]));
        }

        @Test
        void testZeroDividedByZeroProducesNaN() {
            final Transform zero1 = new Scale(0.0);
            final Transform zero2 = new Scale(0.0);
            final Transform divide = new Divide(new Transform[]{zero1, zero2});

            final double[] outputs = new double[1];
            divide.evaluate(new double[]{1.0}, 0, outputs, 0);
            assertTrue(Double.isNaN(outputs[0]));
        }

        @Test
        void testManyTransforms() {
            final Transform shift1 = new Shift(20.0);
            final Transform shift2 = new Shift(2.0);
            final Transform shift3 = new Shift(1.0);
            final Transform divide = new Divide(new Transform[]{shift1, shift2, shift3});

            final double[] inputs = {2.0};
            final double[] outputs = new double[1];

            divide.evaluate(inputs, 0, outputs, 0);

            assertEquals(22.0 / 4.0 / 3.0, outputs[0], DOUBLE_TOLERANCE);
        }
    }

    @Nested
    public class InputOutputCounts {
        @Test
        void countsMatchDelegateTransforms() {
            final Transform t1 = new Shift(1.0);
            final Transform t2 = new Shift(2.0);
            final Transform divide = new Divide(new Transform[]{t1, t2});

            assertEquals(1, divide.getInputCount());
            assertEquals(1, divide.getOutputCount());
        }
    }

    @Nested
    public class Inverse {
        @Test
        void testInvertibility() {
            final Transform t1 = new Shift(1.0);
            final Transform t2 = new Shift(2.0);
            final Transform divide = new Divide(new Transform[]{t1, t2});

            assertFalse(divide.hasInverse());
            assertThrows(UnsupportedOperationException.class, divide::getInverse);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Transform divide = new Divide(new Transform[]{new Shift(10.0), new Shift(2.0)});
        final double[] sampleInputs = {6.0};
        final double[] expected = divide.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 6.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        divide.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
