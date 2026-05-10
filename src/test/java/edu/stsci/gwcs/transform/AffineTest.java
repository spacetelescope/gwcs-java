package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

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
}