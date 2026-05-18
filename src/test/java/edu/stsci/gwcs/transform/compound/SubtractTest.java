package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.functional.Scale;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class SubtractTest {

    @Test
    void testTwoTransforms() {
        final Subtract sub = new Subtract(new Transform[]{new Shift(10.0), new Shift(3.0)});
        // (x+10) - (x+3) = 7
        assertEquals(7.0, sub.evaluate(5.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testThreeTransforms() {
        final Subtract sub = new Subtract(new Transform[]{new Scale(10.0), new Shift(1.0), new Shift(2.0)});
        // (10*x) - (x+1) - (x+2) = 8x-3
        assertEquals(37.0, sub.evaluate(5.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testHasNoInverse() {
        final Subtract sub = new Subtract(new Transform[]{new Shift(1.0), new Shift(2.0)});
        assertFalse(sub.hasInverse());
    }

    @Test
    void testTooFewTransformsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Subtract(new Transform[]{new Shift(1.0)}));
    }

    @Test
    void testInputOutputCounts() {
        final Subtract sub = new Subtract(new Transform[]{new Shift(1.0), new Scale(2.0)});
        assertEquals(1, sub.getInputCount());
        assertEquals(1, sub.getOutputCount());
    }

    @Test
    void testMismatchedCountsThrows() {
        final Transform twoOut = new Transform() {
            @Override public int getInputCount() { return 1; }
            @Override public int getOutputCount() { return 2; }
            @Override public void evaluate(double[] in, int inOff, double[] out, int outOff) {}
        };
        assertThrows(IllegalArgumentException.class,
                () -> new Subtract(new Transform[]{new Shift(1.0), twoOut}));
    }

    @Test
    void testNullElementThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Subtract(new Transform[]{new Shift(1.0), null}));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final Subtract sub = new Subtract(new Transform[]{new Shift(10.0), new Shift(3.0)});
        final double[] inputs = {99.0, 5.0, 99.0};
        final double[] outputs = {77.0, 77.0, 77.0};
        sub.evaluate(inputs, 1, outputs, 2);
        assertEquals(77.0, outputs[0]);
        assertEquals(77.0, outputs[1]);
        assertEquals(7.0, outputs[2], DOUBLE_TOLERANCE);
    }
}
