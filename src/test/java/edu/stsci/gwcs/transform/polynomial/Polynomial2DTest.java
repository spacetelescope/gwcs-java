package edu.stsci.gwcs.transform.polynomial;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class Polynomial2DTest {
    @Test
    void testConstant() {
        // p(x, y) = 7
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{7.0}},
                null, null, null, null
        );
        assertEquals(2, poly.getInputCount());
        assertEquals(1, poly.getOutputCount());

        assertEquals(7.0, poly.evaluate(0.0, 0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(7.0, poly.evaluate(5.0, 3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testLinear() {
        // coefficients[i][j] is the coefficient of x^i * y^j
        // [[c00, c01], [c10, 0]] -> p(x,y) = c00 + c10*x + c01*y
        // Using c00=1, c10=3, c01=2: p(x,y) = 1 + 3x + 2y
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0, 2.0}, {3.0, 0.0}},
                null, null, null, null
        );

        assertEquals(1.0, poly.evaluate(0.0, 0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(6.0, poly.evaluate(1.0, 1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(13.0, poly.evaluate(2.0, 3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testQuadratic() {
        // p(x,y) = 1 + 2x + 3y + 4x^2 + 5xy + 6y^2
        // coefficients[i][j] for i+j <= 2:
        //   [0][0]=1, [0][1]=3, [0][2]=6
        //   [1][0]=2, [1][1]=5
        //   [2][0]=4
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0, 3.0, 6.0}, {2.0, 5.0, 0.0}, {4.0, 0.0, 0.0}},
                null, null, null, null
        );

        // At (0,0): 1
        assertEquals(1.0, poly.evaluate(0.0, 0.0)[0], DOUBLE_TOLERANCE);

        // At (1,0): 1 + 2 + 4 = 7
        assertEquals(7.0, poly.evaluate(1.0, 0.0)[0], DOUBLE_TOLERANCE);

        // At (0,1): 1 + 3 + 6 = 10
        assertEquals(10.0, poly.evaluate(0.0, 1.0)[0], DOUBLE_TOLERANCE);

        // At (1,1): 1 + 2 + 3 + 4 + 5 + 6 = 21
        assertEquals(21.0, poly.evaluate(1.0, 1.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testDomainWindowMapping() {
        // Linear polynomial with x domain [0, 10] -> window [-1, 1]
        // mappedX = 0.2*x - 1, mappedY = y (default)
        // p(mappedX, mappedY) = 1 + 2*mappedX
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0, 0.0}, {2.0, 0.0}},
                new double[]{0.0, 10.0}, null,
                new double[]{-1.0, 1.0}, null
        );

        // At x=5: mappedX=0, result=1
        assertEquals(1.0, poly.evaluate(5.0, 0.0)[0], DOUBLE_TOLERANCE);
        // At x=0: mappedX=-1, result=-1
        assertEquals(-1.0, poly.evaluate(0.0, 0.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsEmptyCoefficients() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial2D(new double[][]{}, null, null, null, null));
    }

    @Test
    void testRejectsNonSquareCoefficients() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial2D(
                        new double[][]{{1.0, 2.0}, {3.0}},
                        null, null, null, null
                ));
    }

    @Test
    void testRejectsWrongLengthDomain() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial2D(
                        new double[][]{{1.0}},
                        new double[]{1.0, 2.0, 3.0}, null, null, null
                ));
    }

    @Test
    void testRejectsEqualYDomainEndpoints() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial2D(
                        new double[][]{{1.0}},
                        null, new double[]{3.0, 3.0}, null, null
                ));
    }

    @Test
    void testRejectsEqualDomainEndpoints() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial2D(
                        new double[][]{{1.0}},
                        new double[]{5.0, 5.0}, null, null, null
                ));
    }

    @Test
    void testUpperTriangleEntriesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Polynomial2D(
                new double[][]{{1.0, 3.0, 6.0}, {2.0, 5.0, 999.0}, {4.0, 0.0, 0.0}},
                null, null, null, null
        ));
        assertThrows(IllegalArgumentException.class, () -> new Polynomial2D(
                new double[][]{{1.0, 3.0, 6.0}, {2.0, 5.0, 0.0}, {4.0, 888.0, 0.0}},
                null, null, null, null
        ));
    }

    @Test
    void testHasNoInverse() {
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0}}, null, null, null, null
        );
        assertFalse(poly.hasInverse());
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0, 2.0}, {3.0, 0.0}},
                null, null, null, null
        );
        final double[] sampleInputs = {2.0, 3.0};
        final double[] expected = poly.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 2.0, 3.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        poly.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }

    @Test
    void testNaNCoefficientProducesNaNOutput() {
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{Double.NaN}},
                null, null, null, null
        );
        assertTrue(Double.isNaN(poly.evaluate(1.0, 1.0)[0]));
    }

    @Test
    void testNaNInputPropagates() {
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0, 2.0}, {3.0, 0.0}},
                null, null, null, null
        );
        assertTrue(Double.isNaN(poly.evaluate(Double.NaN, 1.0)[0]));
    }

    @Test
    void testYDomainWindowMapping() {
        // Linear polynomial with y domain [0, 10] -> y window [-1, 1]
        // mappedY = 0.2*y - 1, mappedX = x (default)
        // p(mappedX, mappedY) = 1 + 2*mappedY
        // coefficients[i][j] for x^i * y^j: [[1.0, 2.0], [0.0, 0.0]]
        final Polynomial2D poly = new Polynomial2D(
                new double[][]{{1.0, 2.0}, {0.0, 0.0}},
                null, new double[]{0.0, 10.0},
                null, new double[]{-1.0, 1.0}
        );

        // At y=5: mappedY=0, result=1
        assertEquals(1.0, poly.evaluate(0.0, 5.0)[0], DOUBLE_TOLERANCE);
        // At y=0: mappedY=-1, result=1 + 2*(-1) = -1
        assertEquals(-1.0, poly.evaluate(0.0, 0.0)[0], DOUBLE_TOLERANCE);
        // At y=10: mappedY=1, result=1 + 2*(1) = 3
        assertEquals(3.0, poly.evaluate(0.0, 10.0)[0], DOUBLE_TOLERANCE);
    }
}
