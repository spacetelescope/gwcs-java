package edu.stsci.gwcs.transform.polynomial;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class Polynomial1DTest {
    @Test
    void testConstant() {
        // p(x) = 5
        final Polynomial1D poly = new Polynomial1D(new double[]{5.0}, null, null);
        assertEquals(1, poly.getInputCount());
        assertEquals(1, poly.getOutputCount());

        assertEquals(5.0, poly.evaluate(0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(5.0, poly.evaluate(99.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testLinear() {
        // p(x) = 1 + 2x (default domain/window [-1,1] -> [-1,1] is identity mapping)
        final Polynomial1D poly = new Polynomial1D(new double[]{1.0, 2.0}, null, null);

        assertEquals(1.0, poly.evaluate(0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(3.0, poly.evaluate(1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(7.0, poly.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testQuadratic() {
        // p(x) = 1 + 0x + 1x^2 = 1 + x^2
        final Polynomial1D poly = new Polynomial1D(new double[]{1.0, 0.0, 1.0}, null, null);

        assertEquals(1.0, poly.evaluate(0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(2.0, poly.evaluate(1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(5.0, poly.evaluate(2.0)[0], DOUBLE_TOLERANCE);
        assertEquals(10.0, poly.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testDomainWindowMapping() {
        // domain [0, 10] -> window [-1, 1]: mappedX = 0.2*x - 1
        // p(mappedX) = 1 + 2*mappedX
        // At x=5: mappedX=0, result=1
        // At x=0: mappedX=-1, result=-1
        // At x=10: mappedX=1, result=3
        final Polynomial1D poly = new Polynomial1D(
                new double[]{1.0, 2.0},
                new double[]{0.0, 10.0},
                new double[]{-1.0, 1.0}
        );

        assertEquals(1.0, poly.evaluate(5.0)[0], DOUBLE_TOLERANCE);
        assertEquals(-1.0, poly.evaluate(0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(3.0, poly.evaluate(10.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsEmptyCoefficients() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial1D(new double[]{}, null, null));
    }

    @Test
    void testRejectsInvalidDomainLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial1D(new double[]{1.0}, new double[]{0.0, 1.0, 2.0}, null));
    }

    @Test
    void testRejectsInvalidWindowLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial1D(new double[]{1.0}, null, new double[]{0.0, 1.0, 2.0}));
    }

    @Test
    void testRejectsEqualDomainEndpoints() {
        assertThrows(IllegalArgumentException.class,
                () -> new Polynomial1D(new double[]{1.0}, new double[]{5.0, 5.0}, null));
    }

    @Test
    void testHasNoInverse() {
        final Polynomial1D poly = new Polynomial1D(new double[]{1.0, 2.0}, null, null);
        assertFalse(poly.hasInverse());
        assertThrows(UnsupportedOperationException.class, poly::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Polynomial1D poly = new Polynomial1D(new double[]{1.0, 2.0}, null, null);
        final double[] sampleInputs = {3.0};
        final double[] expected = poly.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 3.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        poly.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }

    @Test
    void testNaNInputPropagates() {
        final Polynomial1D poly = new Polynomial1D(new double[]{1.0, 2.0}, null, null);
        assertTrue(Double.isNaN(poly.evaluate(Double.NaN)[0]));
    }
}
