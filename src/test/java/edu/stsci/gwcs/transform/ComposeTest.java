package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class ComposeTest {
    @Nested
    public class Constructor {
        @Test
        void testEmptyArray() {
            assertThrows(IllegalArgumentException.class, () -> new Compose(new Transform[0]));
        }

        @Test
        void testInputOutputMismatch() {
            final Transform shift = new Shift(1.0);

            final double[][] matrix = {
                    {1.0, 0.0},
                    {0.0, 1.0}
            };
            final double[] translation = {0.0, 0.0};
            final Transform affine = new Affine(matrix, translation);

            assertThrows(IllegalArgumentException.class, () -> new Compose(new Transform[]{shift, affine}));
        }
    }

    @Nested
    public class Evaluate {
        @Test
        void testSingleTransform() {
            final Transform scale = new Scale(2.0);
            final Compose compose = new Compose(new Transform[]{scale});

            final double[] inputs = {5.0};
            final double[] outputs = new double[1];

            compose.evaluate(inputs, 0, outputs, 0);

            assertEquals(10.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(1, compose.getInputCount());
            assertEquals(1, compose.getOutputCount());
        }

        @Test
        void testTwoTransforms() {
            final Transform scale = new Scale(2.0);
            final Transform shift = new Shift(3.0);
            final Compose compose = new Compose(new Transform[]{scale, shift});

            final double[] inputs = {5.0};
            final double[] outputs = new double[1];

            compose.evaluate(inputs, 0, outputs, 0);

            assertEquals(13.0, outputs[0], DOUBLE_TOLERANCE);
        }

        @Test
        void testManyTransforms() {
            final Transform scale1 = new Scale(2.0);
            final Transform shift1 = new Shift(1.0);
            final Transform scale2 = new Scale(3.0);
            final Transform shift2 = new Shift(-5.0);

            final Compose compose = new Compose(new Transform[]{scale1, shift1, scale2, shift2});

            final double[] inputs = {4.0};
            final double[] outputs = new double[1];

            compose.evaluate(inputs, 0, outputs, 0);

            assertEquals(22.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(1, compose.getInputCount());
            assertEquals(1, compose.getOutputCount());
        }
    }

    @Nested
    public class Inverse {
        @Test
        void testInvertible() {
            final Transform scale = new Scale(2.0);
            final Transform shift = new Shift(5.0);

            final Compose compose = new Compose(new Transform[]{scale, shift});
            assertTrue(compose.hasInverse());

            final Transform inverse = compose.getInverse();
            final double[] initial = {10.0};
            final double[] intermediate = new double[1];
            final double[] recovered = new double[1];

            compose.evaluate(initial, 0, intermediate, 0);
            assertEquals(25.0, intermediate[0], DOUBLE_TOLERANCE);

            inverse.evaluate(intermediate, 0, recovered, 0);
            assertEquals(initial[0], recovered[0], DOUBLE_TOLERANCE);
        }

        @Test
        void testNonInvertible() {
            final Transform scale = new Scale(0);
            final Transform shift = new Shift(5.0);

            final Compose compose = new Compose(new Transform[]{scale, shift});
            assertFalse(compose.hasInverse());
        }
    }
}