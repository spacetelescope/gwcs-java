package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class RegionsSelectorTest {
    @Test
    void testDispatchToRegions() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1, 5.0, 2));
        final Map<Integer, Transform> selector = Map.of(
                1, new Shift(10.0),
                2, new Shift(20.0)
        );
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertEquals(10.0, rs.evaluate(0.0)[0], DOUBLE_TOLERANCE);
        assertEquals(25.0, rs.evaluate(5.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testUndefinedRegionReturnsNaN() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertTrue(Double.isNaN(rs.evaluate(99.0)[0]));
    }

    @Test
    void testCustomUndefinedValue() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector, -999.0);

        assertEquals(-999.0, rs.evaluate(99.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testMismatchedTransformCountsThrows() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1, 1.0, 2));
        final Transform twoOutputTransform = new Transform() {
            @Override
            public int getInputCount() { return 1; }
            @Override
            public int getOutputCount() { return 2; }
            @Override
            public void evaluate(double[] inputs, int inputOffset, double[] outputs, int outputOffset) {
                outputs[outputOffset] = 0.0;
                outputs[outputOffset + 1] = 0.0;
            }
        };
        final Map<Integer, Transform> selector = Map.of(
                1, new Shift(10.0),
                2, twoOutputTransform
        );
        assertThrows(IllegalArgumentException.class, () -> new RegionsSelector(labelMapper, selector));
    }

    @Test
    void testEmptySelectorThrows() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1));
        assertThrows(IllegalArgumentException.class, () -> new RegionsSelector(labelMapper, Map.of()));
    }

    @Test
    void testLabelMapperInputCountMismatchThrows() {
        final Transform twoInputMapper = new Transform() {
            @Override
            public int getInputCount() { return 2; }
            @Override
            public int getOutputCount() { return 1; }
            @Override
            public void evaluate(double[] inputs, int inputOffset, double[] outputs, int outputOffset) {
                outputs[outputOffset] = 1.0;
            }
        };
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        assertThrows(IllegalArgumentException.class, () -> new RegionsSelector(twoInputMapper, selector));
    }

    @Test
    void testLabelMapperOutputCountMismatchThrows() {
        final Transform twoOutputMapper = new Transform() {
            @Override
            public int getInputCount() { return 1; }
            @Override
            public int getOutputCount() { return 2; }
            @Override
            public void evaluate(double[] inputs, int inputOffset, double[] outputs, int outputOffset) {
                outputs[outputOffset] = 1.0;
                outputs[outputOffset + 1] = 2.0;
            }
        };
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        assertThrows(IllegalArgumentException.class, () -> new RegionsSelector(twoOutputMapper, selector));
    }

    @Test
    void testLabelNotInSelectorReturnsUndefined() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 99));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertTrue(Double.isNaN(rs.evaluate(0.0)[0]));
    }

    @Test
    void testInputOutputCounts() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertEquals(1, rs.getInputCount());
        assertEquals(1, rs.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, 1));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertFalse(rs.hasInverse());
        assertThrows(UnsupportedOperationException.class, rs::getInverse);
    }
}
