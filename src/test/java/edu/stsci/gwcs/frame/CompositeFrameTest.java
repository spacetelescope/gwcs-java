package edu.stsci.gwcs.frame;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFrameTest {
    private Frame2D makeDetectorFrame(final int[] axisOrder) {
        return new Frame2D(
                "detector",
                new String[]{"x", "y"},
                axisOrder,
                new String[]{"custom:x", "custom:y"},
                new String[]{"pixel", "pixel"}
        );
    }

    private CelestialFrame makeCelestialFrame(final int[] axisOrder) {
        return new CelestialFrame(
                "world",
                new String[]{"lon", "lat"},
                axisOrder,
                new String[]{"pos.eq.ra", "pos.eq.dec"},
                new String[]{"deg", "deg"},
                "ICRS"
        );
    }

    @Nested
    class Construction {
        @Test
        void twoFrame2DInstances() {
            final Frame2D frameA = makeDetectorFrame(new int[]{0, 1});
            final Frame2D frameB = makeCelestialFrame(new int[]{2, 3});

            final CompositeFrame composite = new CompositeFrame("composite", new Frame[]{frameA, frameB});
            assertEquals(4, composite.getAxisCount());
        }

        @Test
        void frame2DAndCelestialFrame() {
            final Frame2D detector = makeDetectorFrame(new int[]{0, 1});
            final CelestialFrame celestial = makeCelestialFrame(new int[]{2, 3});

            final CompositeFrame composite = new CompositeFrame("composite", new Frame[]{detector, celestial});
            assertArrayEquals(new String[]{"x", "y", "lon", "lat"}, composite.getAxisNames());
        }

        @Test
        void overlappingAxesOrder() {
            final Frame2D frameA = makeDetectorFrame(new int[]{0, 1});
            final Frame2D frameB = makeDetectorFrame(new int[]{1, 2});

            assertThrows(IllegalArgumentException.class,
                    () -> new CompositeFrame("composite", new Frame[]{frameA, frameB}));
        }

        @Test
        void singleSubFrame() {
            final Frame2D frame = makeDetectorFrame(new int[]{0, 1});
            final CompositeFrame composite = new CompositeFrame("single", new Frame[]{frame});

            assertEquals(2, composite.getAxisCount());
            assertArrayEquals(new int[]{0, 1}, composite.getAxisOrder());
        }

        @Test
        void emptyFramesArray() {
            assertThrows(IllegalArgumentException.class,
                    () -> new CompositeFrame("empty", new Frame[]{}));
        }
    }

    @Nested
    class ContiguityValidation {
        @Test
        void testNonContiguousAxesThrows() {
            final Frame2D frameA = makeDetectorFrame(new int[]{0, 1});
            final Frame2D frameB = new Frame2D(
                    "sky",
                    new String[]{"lon", "lat"},
                    new int[]{4, 5},
                    new String[]{"pos.eq.ra", "pos.eq.dec"},
                    new String[]{"deg", "deg"}
            );

            assertThrows(IllegalArgumentException.class,
                    () -> new CompositeFrame("composite", new Frame[]{frameA, frameB}));
        }
    }

    @Nested
    class AxisOrderRemapping {
        @Test
        void axisOrderSortedByPipelinePosition() {
            final Frame2D frameA = makeDetectorFrame(new int[]{0, 1});
            final CelestialFrame frameB = makeCelestialFrame(new int[]{3, 2});

            final CompositeFrame composite = new CompositeFrame("composite", new Frame[]{frameA, frameB});
            assertArrayEquals(new int[]{0, 1, 2, 3}, composite.getAxisOrder());
        }

        @Test
        void propertiesReorderedByPipelinePosition() {
            final Frame2D frameA = new Frame2D(
                    "detector",
                    new String[]{"x", "y"},
                    new int[]{1, 0},
                    new String[]{"custom:x", "custom:y"},
                    new String[]{"pixel", "pixel"}
            );
            final Frame2D frameB = new Frame2D(
                    "sky",
                    new String[]{"lon", "lat"},
                    new int[]{2, 3},
                    new String[]{"pos.eq.ra", "pos.eq.dec"},
                    new String[]{"deg", "deg"}
            );

            final CompositeFrame composite = new CompositeFrame("composite", new Frame[]{frameA, frameB});

            assertArrayEquals(new int[]{0, 1, 2, 3}, composite.getAxisOrder());
            assertArrayEquals(new String[]{"y", "x", "lon", "lat"}, composite.getAxisNames());
            assertArrayEquals(new String[]{"custom:y", "custom:x", "pos.eq.ra", "pos.eq.dec"},
                    composite.getAxisPhysicalTypes());
            assertArrayEquals(new String[]{"pixel", "pixel", "deg", "deg"}, composite.getUnits());
        }
    }

    @Nested
    class Accessors {
        @Test
        void getFramesReturnsConstituents() {
            final Frame2D frameA = makeDetectorFrame(new int[]{0, 1});
            final CelestialFrame frameB = makeCelestialFrame(new int[]{2, 3});

            final CompositeFrame composite = new CompositeFrame("composite", new Frame[]{frameA, frameB});
            final Frame[] frames = composite.getFrames();

            assertEquals(2, frames.length);
            assertSame(frameA, frames[0]);
            assertSame(frameB, frames[1]);
        }

        @Test
        void getName() {
            final Frame2D frame = makeDetectorFrame(new int[]{0, 1});
            final CompositeFrame composite = new CompositeFrame("mycomposite", new Frame[]{frame});

            assertEquals("mycomposite", composite.getName());
        }
    }
}
