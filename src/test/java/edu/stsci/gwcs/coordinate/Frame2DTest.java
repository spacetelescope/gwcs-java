package edu.stsci.gwcs.coordinate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Frame2DTest {
    @Nested
    class Construction {
        @Test
        void validParameters() {
            final Frame2D frame = new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{0, 1},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            );

            assertEquals("detector", frame.getName());
            assertEquals(2, frame.getAxisCount());
            assertArrayEquals(new int[]{0, 1}, frame.getAxisOrder());
            assertArrayEquals(new String[]{"x", "y"}, frame.getAxisNames());
            assertArrayEquals(new String[]{"custom:x", "custom:y"}, frame.getAxisPhysicalTypes());
            assertArrayEquals(new String[]{"pixel", "pixel"}, frame.getUnits());
        }

        @Test
        void wrongLengthAxisNames() {
            assertThrows(IllegalArgumentException.class, () -> new Frame2D(
                    "detector",
                    new String[]{"x"},
                    new int[]{0, 1},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            ));
        }

        @Test
        void wrongLengthAxisOrder() {
            assertThrows(IllegalArgumentException.class, () -> new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{0, 1, 2},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            ));
        }

        @Test
        void wrongLengthAxisPhysicalTypes() {
            assertThrows(IllegalArgumentException.class, () -> new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{0, 1},
                    new String[]{"custom:x", "custom:y", "custom:z"},
                    new String[]{"pixel", "pixel"}
            ));
        }

        @Test
        void wrongLengthUnits() {
            assertThrows(IllegalArgumentException.class, () -> new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{0, 1},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel"}
            ));
        }

        @Test
        void duplicateAxisOrderValues() {
            assertThrows(IllegalArgumentException.class, () -> new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{1, 1},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            ));
        }

        @Test
        void negativeAxisOrderValues() {
            assertThrows(IllegalArgumentException.class, () -> new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{-1, 0},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            ));
        }
    }

    @Nested
    class DefensiveCopies {
        @Test
        void mutatingInputArrayDoesNotAffectFrame() {
            final int[] axisOrder = {0, 1};
            final Frame2D frame = new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    axisOrder,
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            );

            axisOrder[0] = 99;
            assertArrayEquals(new int[]{0, 1}, frame.getAxisOrder());
        }

        @Test
        void mutatingReturnedArrayDoesNotAffectFrame() {
            final Frame2D frame = new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{0, 1},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            );

            final int[] order = frame.getAxisOrder();
            order[0] = 99;
            assertArrayEquals(new int[]{0, 1}, frame.getAxisOrder());
        }
    }
}
