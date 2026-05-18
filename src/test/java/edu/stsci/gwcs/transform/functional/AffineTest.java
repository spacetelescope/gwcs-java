package edu.stsci.gwcs.transform.functional;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;

class AffineTest {
    @Test
    void test2D() {
        final double[][] matrix = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        final double[] translation = {5.0, 6.0};

        final Affine transform = new Affine(matrix, translation);

        final double[] inputs = {1.0, 2.0};
        final double[] outputs = new double[2];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(10.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(17.0, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void test3D() {
        // Proving our N-dimensional logic works with a 3x3 matrix
        final double[][] matrix = {
                {1.0, 0.0, 0.0},
                {0.0, 2.0, 0.0},
                {0.0, 0.0, 3.0}
        };
        final double[] translation = {10.0, 20.0, 30.0};

        final Affine transform = new Affine(matrix, translation);

        final double[] inputs = {5.0, 5.0, 5.0};
        final double[] outputs = new double[3];

        transform.evaluate(inputs, 0, outputs, 0);

        assertEquals(15.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(45.0, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverse() {
        final double[][] matrix = {
                {1.2, -0.5},
                {0.8, 1.1}
        };
        final double[] translation = {-2.0, 1.5};

        final Affine transform = new Affine(matrix, translation);
        assertTrue(transform.hasInverse());

        final Transform inverse = transform.getInverse();

        final double[] original = {3.5, -1.2};
        final double[] intermediate = new double[2];
        final double[] recovered = new double[2];

        transform.evaluate(original, 0, intermediate, 0);
        inverse.evaluate(intermediate, 0, recovered, 0);

        assertEquals(original[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(original[1], recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testIdentityMatrix() {
        final double[][] matrix = {{1.0, 0.0}, {0.0, 1.0}};
        final double[] translation = {0.0, 0.0};
        final Affine transform = new Affine(matrix, translation);

        final double[] outputs = transform.evaluate(3.5, -1.2);
        assertEquals(3.5, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(-1.2, outputs[1], DOUBLE_TOLERANCE);
    }

    @Test
    void test1x1() {
        final double[][] matrix = {{3.0}};
        final double[] translation = {5.0};
        final Affine transform = new Affine(matrix, translation);

        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());

        final double[] outputs = transform.evaluate(2.0);
        assertEquals(11.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void nanMatrixEntryThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Affine(new double[][]{{Double.NaN}}, new double[]{0.0}));
    }

    @Test
    void infinityTranslationThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Affine(new double[][]{{1.0}}, new double[]{Double.POSITIVE_INFINITY}));
    }

    @Test
    void nullInnerRowThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Affine(new double[][]{{1.0, 0.0}, null}, new double[]{0.0, 0.0}));
    }

    @Test
    void defensiveCopyPreventsExternalMutation() {
        final double[][] matrix = {{2.0}};
        final double[] translation = {5.0};
        final Affine transform = new Affine(matrix, translation);

        matrix[0][0] = 999.0;
        translation[0] = 999.0;

        final double[] outputs = transform.evaluate(1.0);
        assertEquals(7.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void emptyMatrixThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Affine(new double[][]{}, new double[]{}));
    }

    @Test
    void mismatchedTranslationLengthThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Affine(new double[][]{{1.0}}, new double[]{1.0, 2.0}));
    }

    @Test
    void nonSquareMatrixThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Affine(new double[][]{{1.0, 2.0}, {3.0}}, new double[]{0.0, 0.0}));
    }

    @Test
    void testNonInvertible() {
        final double[][] matrix = {
                {2.0, 4.0},
                {1.0, 2.0}
        };

        final double[] translation = {0.0, 0.0};

        final Affine transform = new Affine(matrix, translation);

        assertFalse(transform.hasInverse());

        assertThrows(UnsupportedOperationException.class, transform::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final double[][] matrix = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        final double[] translation = {5.0, 6.0};
        final Affine transform = new Affine(matrix, translation);

        final double[] inputs = new double[]{99.0, 99.0, 1.0, 2.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        final double[] expected = transform.evaluate(1.0, 2.0);
        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}