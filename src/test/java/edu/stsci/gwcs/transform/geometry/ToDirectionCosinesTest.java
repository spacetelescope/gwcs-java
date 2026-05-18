package edu.stsci.gwcs.transform.geometry;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class ToDirectionCosinesTest {
    @Test
    void testOrigin() {
        final ToDirectionCosines transform = new ToDirectionCosines();
        final double[] outputs = transform.evaluate(0.0, 0.0, 0.0);
        assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
        assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(1.0, outputs[2], DOUBLE_TOLERANCE);
        assertEquals(1.0, outputs[3], DOUBLE_TOLERANCE);
    }

    @Test
    void testUnitVector() {
        final ToDirectionCosines transform = new ToDirectionCosines();
        final double[] outputs = transform.evaluate(1.0, 0.0, 0.0);

        final double sumOfSquares = outputs[0] * outputs[0] + outputs[1] * outputs[1] + outputs[2] * outputs[2];
        assertEquals(1.0, sumOfSquares, DOUBLE_TOLERANCE);
    }

    @Test
    void testZInputIgnored() {
        final ToDirectionCosines transform = new ToDirectionCosines();
        final double[] outputs1 = transform.evaluate(3.0, 4.0, 99.0);
        final double[] outputs2 = transform.evaluate(3.0, 4.0, -42.0);

        assertEquals(outputs1[0], outputs2[0], DOUBLE_TOLERANCE);
        assertEquals(outputs1[1], outputs2[1], DOUBLE_TOLERANCE);
        assertEquals(outputs1[2], outputs2[2], DOUBLE_TOLERANCE);
        assertEquals(outputs1[3], outputs2[3], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final ToDirectionCosines transform = new ToDirectionCosines();
        assertEquals(3, transform.getInputCount());
        assertEquals(4, transform.getOutputCount());
    }

    @Test
    void testInverse() {
        final ToDirectionCosines transform = new ToDirectionCosines();
        assertTrue(transform.hasInverse());
        assertInstanceOf(FromDirectionCosines.class, transform.getInverse());
    }

    @Test
    void testRoundTrip() {
        final ToDirectionCosines toDC = new ToDirectionCosines();
        final FromDirectionCosines fromDC = new FromDirectionCosines();

        final double[] dc = toDC.evaluate(3.0, 4.0, 7.0);
        final double[] recovered = fromDC.evaluate(dc);

        assertEquals(3.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(4.0, recovered[1], DOUBLE_TOLERANCE);
        // z output is cosc * length = 1.0, regardless of original z
        assertEquals(1.0, recovered[2], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ToDirectionCosines transform = new ToDirectionCosines();
        final double[] sampleInputs = {3.0, 4.0, 7.0};
        final double[] expected = transform.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 3.0, 4.0, 7.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(expected[1], outputs[2], DOUBLE_TOLERANCE);
        assertEquals(expected[2], outputs[3], DOUBLE_TOLERANCE);
        assertEquals(expected[3], outputs[4], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[5]);
        assertEquals(77.0, outputs[6]);
    }
}
