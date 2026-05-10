package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxWrapperTest {
    private static final double FILL_VALUE = Double.NaN;

    private final Transform delegate = new Transform() {
        @Override
        public int getInputCount() {
            return 2;
        }

        @Override
        public int getOutputCount() {
            return 2;
        }

        @Override
        public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
            outputs[outputOffset] = inputs[inputOffset] * 2.0;
            outputs[outputOffset + 1] = inputs[inputOffset + 1] * 2.0;
        }

        @Override
        public boolean hasInverse() {
            return true;
        }

        @Override
        public Transform getInverse() {
            return this;
        }
    };

    @Nested
    public class Constructor {
        @Test
        void testIntervalsMismatch() {
            final double[][] intervals = {{-1.0, 1.0}};

            assertThrows(IllegalArgumentException.class,
                    () -> new BoundingBoxWrapper(delegate, intervals, FILL_VALUE)
            );
        }

        @Test
        void testReversedInterval() {
            final double[][] intervals = {{5.0, 1.0}, {0.0, 1.0}};

            assertThrows(IllegalArgumentException.class,
                    () -> new BoundingBoxWrapper(delegate, intervals, FILL_VALUE));
        }

        @Test
        void testNullInterval() {
            final double[][] intervals = {{0.0, 1.0}, null};

            assertThrows(IllegalArgumentException.class,
                    () -> new BoundingBoxWrapper(delegate, intervals, FILL_VALUE));
        }

        @Test
        void testInvalidIntervalLength() {
            final double[][] intervals = {{-1.0, 1.0}, {0.0, 1.0, 2.0}};

            assertThrows(IllegalArgumentException.class,
                    () -> new BoundingBoxWrapper(delegate, intervals, FILL_VALUE)
            );
        }
    }

    @Nested
    public class Evaluate {
        @Test
        void testInsideBounds() {
            final double[][] intervals = {{0.0, 10.0}, {-5.0, 5.0}};
            final BoundingBoxWrapper transform = new BoundingBoxWrapper(delegate, intervals, FILL_VALUE);

            final double[] inputs = {5.0, 0.0};
            final double[] outputs = new double[2];

            transform.evaluate(inputs, 0, outputs, 0);

            assertEquals(10.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(0.0, outputs[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testOnBounds() {
            final double[][] intervals = {{0.0, 10.0}, {-5.0, 5.0}};
            final BoundingBoxWrapper transform = new BoundingBoxWrapper(delegate, intervals, FILL_VALUE);

            final double[] inputs = {0.0, 5.0};
            final double[] outputs = new double[2];

            transform.evaluate(inputs, 0, outputs, 0);

            assertEquals(0.0, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(10.0, outputs[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testOutsideBounds() {
            final double[][] intervals = {{0.0, 10.0}, {-5.0, 5.0}};
            final double fillValue = -999.0;
            final BoundingBoxWrapper transform = new BoundingBoxWrapper(delegate, intervals, fillValue);

            final double[] inputs = {15.0, 0.0};
            final double[] outputs = new double[2];

            transform.evaluate(inputs, 0, outputs, 0);

            assertEquals(fillValue, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(fillValue, outputs[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testNanAsOutsideBounds() {
            final double[][] intervals = {{0.0, 10.0}, {-5.0, 5.0}};
            final double customFill = -999.0;
            final BoundingBoxWrapper transform = new BoundingBoxWrapper(delegate, intervals, customFill);

            final double[] inputs = {Double.NaN, 0.0};
            final double[] outputs = new double[2];

            transform.evaluate(inputs, 0, outputs, 0);

            assertEquals(customFill, outputs[0], DOUBLE_TOLERANCE);
            assertEquals(customFill, outputs[1], DOUBLE_TOLERANCE);
        }
    }

    @Test
    void testPassThroughMethods() {
        final double[][] intervals = {{0.0, 10.0}, {-5.0, 5.0}};
        final BoundingBoxWrapper transform = new BoundingBoxWrapper(delegate, intervals, FILL_VALUE);

        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
        assertTrue(transform.hasInverse());

        assertEquals(delegate, transform.getInverse());
    }
}