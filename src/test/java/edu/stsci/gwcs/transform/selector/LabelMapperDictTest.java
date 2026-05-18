package edu.stsci.gwcs.transform.selector;

import edu.stsci.gwcs.transform.Constant;
import edu.stsci.gwcs.transform.Transform;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class LabelMapperDictTest {

    private static Constant c(final double value) {
        return new Constant(1, value);
    }

    @Test
    void testExactMatch() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1), 2.0, c(2), 3.0, c(3)), 0.01, Double.NaN);
        assertEquals(1.0, mapper.evaluate(1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(2.0, mapper.evaluate(2.0)[0], DOUBLE_TOLERANCE);
        assertEquals(3.0, mapper.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithinTolerance() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1), 2.0, c(2), 3.0, c(3)), 0.01, Double.NaN);
        assertEquals(1.0, mapper.evaluate(1.005)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testNoMatch() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1), 2.0, c(2), 3.0, c(3)), 0.01, Double.NaN);
        assertTrue(Double.isNaN(mapper.evaluate(1.5)[0]));
    }

    @Test
    void testCustomNoLabel() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1)), 0.01, -1.0);
        assertEquals(-1.0, mapper.evaluate(99.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1)));
        assertEquals(1, mapper.getInputCount());
        assertEquals(1, mapper.getOutputCount());
    }

    @Test
    void testNanInputReturnsNoLabel() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1), 2.0, c(2)), 0.01, -999.0);
        assertEquals(-999.0, mapper.evaluate(Double.NaN)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testEmptyMapThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new LabelMapperDict(Map.<Double, Transform>of(), 0.01, Double.NaN));
    }

    @Test
    void testNegativeToleranceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new LabelMapperDict(Map.of(1.0, c(1)), -0.01, Double.NaN));
    }

    @Test
    void testNanToleranceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new LabelMapperDict(Map.of(1.0, c(1)), Double.NaN, Double.NaN));
    }

    @Test
    void testNearestNeighborWhenTolerancesOverlap() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(10), 1.005, c(20)), 0.01, -1.0);
        assertEquals(20.0, mapper.evaluate(1.003)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testHasNoInverse() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1)));
        assertFalse(mapper.hasInverse());
        assertThrows(UnsupportedOperationException.class, mapper::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, c(1), 2.0, c(2), 3.0, c(3)), 0.01, Double.NaN);
        final double[] sampleInputs = {2.0};
        final double[] expected = mapper.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 2.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        mapper.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
