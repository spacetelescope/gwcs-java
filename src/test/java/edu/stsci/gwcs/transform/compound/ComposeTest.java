package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.functional.Affine;
import edu.stsci.gwcs.transform.functional.Scale;
import edu.stsci.gwcs.transform.functional.Shift;

class ComposeTest {
    @Nested
    public class Constructor {
        @Test
        void testEmptyArray() {
            assertThrows(IllegalArgumentException.class, () -> new Compose(new Transform[0]));
        }

        @Test
        void testNullElement() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Compose(new Transform[]{new Shift(1.0), null}));
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

        @Test
        void testGetInverseThrowsWhenNonInvertible() {
            final Transform scale = new Scale(0);
            final Transform shift = new Shift(5.0);

            final Compose compose = new Compose(new Transform[]{scale, shift});
            assertThrows(UnsupportedOperationException.class, compose::getInverse);
        }
    }

    @Nested
    public class MultiDimensional {
        @Test
        void testComposeAffines() {
            final double[][] m1 = {{1.0, 2.0}, {3.0, 4.0}};
            final double[] t1 = {0.0, 0.0};
            final double[][] m2 = {{2.0, 0.0}, {0.0, 2.0}};
            final double[] t2 = {1.0, 1.0};

            final Compose compose = new Compose(new Transform[]{
                    new Affine(m1, t1), new Affine(m2, t2)
            });

            assertEquals(2, compose.getInputCount());
            assertEquals(2, compose.getOutputCount());

            final double[] outputs = compose.evaluate(1.0, 0.0);
            // m1 * [1,0] = [1,3], then m2 * [1,3] + [1,1] = [3,7]
            assertEquals(3.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(7.0, outputs[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testThreeAffinesBufferSwap() {
            final double[][] eye = {{1.0, 0.0}, {0.0, 1.0}};
            final double[] t1 = {1.0, 2.0};
            final double[] t2 = {10.0, 20.0};
            final double[] t3 = {100.0, 200.0};

            final Compose compose = new Compose(new Transform[]{
                    new Affine(eye, t1), new Affine(eye, t2), new Affine(eye, t3)
            });

            final double[] outputs = compose.evaluate(0.0, 0.0);
            assertEquals(111.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(222.0, outputs[1], DOUBLE_TOLERANCE);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Compose compose = new Compose(new Transform[]{new Scale(2.0), new Shift(3.0)});
        final double[] sampleInputs = {5.0};
        final double[] expected = compose.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 5.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        compose.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}