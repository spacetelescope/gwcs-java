package edu.stsci.gwcs.transform.compound;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.compound.Concatenate;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.Transform;

class FixInputsTest {
    private final Transform twoInputTransform = new Concatenate(new Transform[]{
            new Shift(10), new Shift(20)
    });

    private final Transform threeInputTransform = new Concatenate(new Transform[]{
            new Shift(10), new Shift(20), new Shift(30)
    });

    @Nested
    public class Constructor {
        @Test
        void testEmptyFixedInputs() {
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of())
            );
        }

        @Test
        void testAllInputsFixed() {
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of(0, 1.0, 1, 2.0))
            );
        }

        @Test
        void testInvalidIndex() {
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of(5, 1.0))
            );
        }

        @Test
        void testNegativeIndex() {
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of(-1, 1.0))
            );
        }

        @Test
        void testNaNValueRejected() {
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of(0, Double.NaN))
            );
        }

        @Test
        void testInfiniteValueRejected() {
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of(0, Double.POSITIVE_INFINITY))
            );
            assertThrows(IllegalArgumentException.class,
                    () -> new FixInputs(twoInputTransform, Map.of(0, Double.NEGATIVE_INFINITY))
            );
        }
    }

    @Nested
    public class Evaluate {
        @Test
        void testFixFirstInput() {
            final FixInputs transform = new FixInputs(twoInputTransform, Map.of(0, 5.0));

            assertEquals(1, transform.getInputCount());

            final double[] result = transform.evaluate(3.0);

            assertEquals(15.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(23.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testFixSecondInput() {
            final FixInputs transform = new FixInputs(twoInputTransform, Map.of(1, 7.0));

            assertEquals(1, transform.getInputCount());

            final double[] result = transform.evaluate(3.0);

            assertEquals(13.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(27.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void testFixMiddleInput() {
            final FixInputs transform = new FixInputs(threeInputTransform, Map.of(1, 5.0));

            assertEquals(2, transform.getInputCount());

            final double[] result = transform.evaluate(2.0, 3.0);

            assertEquals(12.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(25.0, result[1], DOUBLE_TOLERANCE);
            assertEquals(33.0, result[2], DOUBLE_TOLERANCE);
        }

        @Test
        void testFixMultipleInputs() {
            final FixInputs transform = new FixInputs(threeInputTransform, Map.of(0, 1.0, 2, 3.0));

            assertEquals(1, transform.getInputCount());

            final double[] result = transform.evaluate(2.0);

            assertEquals(11.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(22.0, result[1], DOUBLE_TOLERANCE);
            assertEquals(33.0, result[2], DOUBLE_TOLERANCE);
        }

        @Test
        void freeInputNaNPropagates() {
            // Fix delegate input 0 -> free input maps to delegate input 1. With Shift+Shift, only
            // the second output should be NaN; the first output reflects the fixed value's path.
            final FixInputs transform = new FixInputs(twoInputTransform, Map.of(0, 5.0));

            final double[] result = transform.evaluate(Double.NaN);

            assertEquals(15.0, result[0], DOUBLE_TOLERANCE);
            assertTrue(Double.isNaN(result[1]));
        }
    }

    @Test
    void testOutputCount() {
        final FixInputs transform = new FixInputs(twoInputTransform, Map.of(0, 5.0));
        assertEquals(2, transform.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final FixInputs transform = new FixInputs(twoInputTransform, Map.of(0, 5.0));
        assertFalse(transform.hasInverse());
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final FixInputs transform = new FixInputs(twoInputTransform, Map.of(0, 3.0));
        final double[] inputs = new double[]{99.0, 99.0, 7.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        transform.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(13.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(27.0, outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }
}
