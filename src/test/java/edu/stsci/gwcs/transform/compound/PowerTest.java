package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.Constant;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.functional.Scale;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class PowerTest {

    @Test
    void testSquare() {
        final Power pow = new Power(new Transform[]{new Shift(1.0), new Constant(1, 2.0)});
        // (x+1)^2; x=4 -> 5^2 = 25
        assertEquals(25.0, pow.evaluate(4.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testCube() {
        final Power pow = new Power(new Transform[]{new Scale(2.0), new Constant(1, 3.0)});
        // (2x)^3; x=3 -> 6^3 = 216
        assertEquals(216.0, pow.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testFractionalExponent() {
        final Power pow = new Power(new Transform[]{new Constant(1, 9.0), new Constant(1, 0.5)});
        // 9^0.5 = 3
        assertEquals(3.0, pow.evaluate(0.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testHasNoInverse() {
        final Power pow = new Power(new Transform[]{new Shift(1.0), new Constant(1, 2.0)});
        assertFalse(pow.hasInverse());
    }

    @Test
    void testRequiresExactlyTwoTransforms() {
        assertThrows(IllegalArgumentException.class,
                () -> new Power(new Transform[]{new Shift(1.0)}));
        assertThrows(IllegalArgumentException.class,
                () -> new Power(new Transform[]{new Shift(1.0), new Shift(2.0), new Shift(3.0)}));
    }

    @Test
    void testMismatchedCountsThrows() {
        final Transform twoOut = new Transform() {
            @Override public int getInputCount() { return 1; }
            @Override public int getOutputCount() { return 2; }
            @Override public void evaluate(double[] in, int inOff, double[] out, int outOff) {}
        };
        assertThrows(IllegalArgumentException.class,
                () -> new Power(new Transform[]{new Shift(1.0), twoOut}));
    }

    @Test
    void testNullElementThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Power(new Transform[]{new Shift(1.0), null}));
    }

    @Test
    void testZeroToZeroPower() {
        final Power pow = new Power(new Transform[]{new Constant(1, 0.0), new Constant(1, 0.0)});
        assertEquals(1.0, pow.evaluate(0.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testNegativeBaseWithFractionalExponent() {
        final Power pow = new Power(new Transform[]{new Constant(1, -1.0), new Constant(1, 0.5)});
        assertTrue(Double.isNaN(pow.evaluate(0.0)[0]));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Power pow = new Power(new Transform[]{new Shift(1.0), new Constant(1, 2.0)});
        final double[] inputs = {99.0, 4.0, 99.0};
        final double[] outputs = {77.0, 77.0, 77.0};
        pow.evaluate(inputs, 1, outputs, 2);
        assertEquals(77.0, outputs[0]);
        assertEquals(77.0, outputs[1]);
        assertEquals(25.0, outputs[2], DOUBLE_TOLERANCE);
    }
}
