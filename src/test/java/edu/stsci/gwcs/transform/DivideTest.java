package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class DivideTest {
    @Nested
    public class Constructor {
        @Test
        void testInsufficientTransforms() {
            final Transform[] transforms = { new Shift(1.0) };
            assertThrows(IllegalArgumentException.class, () -> new Divide(transforms));
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
        void testMultipleDimensions() {
            final Transform scale1 = new Scale(new double[]{10.0, 10.0});
            final Transform scale2 = new Scale(new double[]{2.0, 2.0});
            final Transform divide = new Divide(new Transform[]{t1, t2});

            final double[] inputs = {5.0, 8.0};
            final double[] outputs = new double[2];

            divide.evaluate(inputs, 0, outputs, 0);

            // Output 0: (5*10) / (5*2) = 5.0
            // Output 1: (8*10) / (8*2) = 5.0
            assertEquals(5.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(5.0, outputs[1], DOUBLE_TOLERANCE);
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
}