package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.functional.Scale;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class MultiplyTest {

    @Test
    void testTwoTransforms() {
        final Multiply mul = new Multiply(new Transform[]{new Shift(1.0), new Shift(2.0)});
        // (x+1) * (x+2); x=3 -> 4*5 = 20
        assertEquals(20.0, mul.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testThreeTransforms() {
        final Multiply mul = new Multiply(new Transform[]{new Scale(2.0), new Scale(3.0), new Scale(4.0)});
        // (2x) * (3x) * (4x) = 24x^3; x=2 -> 24*8 = 192
        assertEquals(192.0, mul.evaluate(2.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testHasNoInverse() {
        final Multiply mul = new Multiply(new Transform[]{new Shift(1.0), new Shift(2.0)});
        assertFalse(mul.hasInverse());
    }

    @Test
    void testTooFewTransformsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Multiply(new Transform[]{new Shift(1.0)}));
    }

    @Test
    void testInputOutputCounts() {
        final Multiply mul = new Multiply(new Transform[]{new Shift(1.0), new Scale(2.0)});
        assertEquals(1, mul.getInputCount());
        assertEquals(1, mul.getOutputCount());
    }

    @Test
    void testMismatchedCountsThrows() {
        final Transform twoOut = new Transform() {
            @Override public int getInputCount() { return 1; }
            @Override public int getOutputCount() { return 2; }
            @Override public void evaluate(double[] in, int inOff, double[] out, int outOff) {}
        };
        assertThrows(IllegalArgumentException.class,
                () -> new Multiply(new Transform[]{new Shift(1.0), twoOut}));
    }

    @Test
    void testNullElementThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Multiply(new Transform[]{new Shift(1.0), null}));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Multiply mul = new Multiply(new Transform[]{new Shift(1.0), new Shift(2.0)});
        final double[] inputs = {99.0, 3.0, 99.0};
        final double[] outputs = {77.0, 77.0, 77.0};
        mul.evaluate(inputs, 1, outputs, 2);
        assertEquals(77.0, outputs[0]);
        assertEquals(77.0, outputs[1]);
        assertEquals(20.0, outputs[2], DOUBLE_TOLERANCE);
    }
}
