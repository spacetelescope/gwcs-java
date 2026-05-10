package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class FromDirectionCosinesTest {
    @Test
    void testEvaluate() {
        final FromDirectionCosines transform = new FromDirectionCosines();
        final double[] outputs = transform.evaluate(0.5, 0.5, 0.707, 2.0);

        assertEquals(1.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(1.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(1.414, outputs[2], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final FromDirectionCosines transform = new FromDirectionCosines();
        assertEquals(4, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void testInverse() {
        final FromDirectionCosines transform = new FromDirectionCosines();
        assertTrue(transform.hasInverse());
        assertInstanceOf(ToDirectionCosines.class, transform.getInverse());
    }

    @Test
    void testRoundTrip() {
        final FromDirectionCosines fromDC = new FromDirectionCosines();
        final ToDirectionCosines toDC = new ToDirectionCosines();

        final double x = 2.5;
        final double y = -1.3;
        final double[] dc = toDC.evaluate(x, y, 0.0);
        final double[] recovered = fromDC.evaluate(dc);

        assertEquals(x, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(y, recovered[1], DOUBLE_TOLERANCE);
    }
}
