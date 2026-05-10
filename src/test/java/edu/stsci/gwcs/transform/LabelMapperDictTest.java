package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class LabelMapperDictTest {
    @Test
    void testExactMatch() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, 1, 2.0, 2, 3.0, 3), 0.01, Double.NaN);
        assertEquals(1.0, mapper.evaluate(1.0)[0], DOUBLE_TOLERANCE);
        assertEquals(2.0, mapper.evaluate(2.0)[0], DOUBLE_TOLERANCE);
        assertEquals(3.0, mapper.evaluate(3.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testWithinTolerance() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, 1, 2.0, 2, 3.0, 3), 0.01, Double.NaN);
        assertEquals(1.0, mapper.evaluate(1.005)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testNoMatch() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, 1, 2.0, 2, 3.0, 3), 0.01, Double.NaN);
        assertTrue(Double.isNaN(mapper.evaluate(1.5)[0]));
    }

    @Test
    void testCustomNoLabel() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, 1), 0.01, -1.0);
        assertEquals(-1.0, mapper.evaluate(99.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCount() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, 1));
        assertEquals(1, mapper.getInputCount());
        assertEquals(1, mapper.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final LabelMapperDict mapper = new LabelMapperDict(Map.of(1.0, 1));
        assertFalse(mapper.hasInverse());
        assertThrows(UnsupportedOperationException.class, mapper::getInverse);
    }
}
