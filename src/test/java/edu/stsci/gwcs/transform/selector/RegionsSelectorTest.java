package edu.stsci.gwcs.transform.selector;

import edu.stsci.gwcs.transform.Constant;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.Transform;

class RegionsSelectorTest {

    private static Constant c(final double value) {
        return new Constant(1, value);
    }

    @Test
    void testDispatchToRegions() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1), 5.0, c(2)));
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
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1)));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertTrue(Double.isNaN(rs.evaluate(99.0)[0]));
    }

    @Test
    void testCustomUndefinedValue() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1)));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector, -999.0);

        assertEquals(-999.0, rs.evaluate(99.0)[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testMismatchedTransformCountsThrows() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1), 1.0, c(2)));
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
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1)));
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
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(99)));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertTrue(Double.isNaN(rs.evaluate(0.0)[0]));
    }

    @Test
    void testInputOutputCounts() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1)));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertEquals(1, rs.getInputCount());
        assertEquals(1, rs.getOutputCount());
    }

    @Test
    void testHasNoInverse() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1)));
        final Map<Integer, Transform> selector = Map.of(1, new Shift(10.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertFalse(rs.hasInverse());
        assertThrows(UnsupportedOperationException.class, rs::getInverse);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, c(1), 5.0, c(2)));
        final Map<Integer, Transform> selector = Map.of(
                1, new Shift(10.0),
                2, new Shift(20.0)
        );
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);
        final double[] sampleInputs = {5.0};
        final double[] expected = rs.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 5.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        rs.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }

    @Test
    void testNonIntegerLabelRounding() {
        // Label mapper returns 1.5 for input 0.0; rounding to nearest int gives 2
        final LabelMapperDict labelMapper = new LabelMapperDict(Map.of(0.0, new Constant(1, 1.5)));
        final Map<Integer, Transform> selector = Map.of(2, new Shift(100.0));
        final RegionsSelector rs = new RegionsSelector(labelMapper, selector);

        assertEquals(100.0, rs.evaluate(0.0)[0], DOUBLE_TOLERANCE);
    }
}
