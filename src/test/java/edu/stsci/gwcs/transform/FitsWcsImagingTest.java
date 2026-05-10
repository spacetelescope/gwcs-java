package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class FitsWcsImagingTest {
    // TAN projection round-trip accumulates numerical error through trig functions;
    // sub-pixel accuracy is the meaningful criterion for pixel-space recovery
    private static final double ROUND_TRIP_TOLERANCE = 1e-5;
    private static final double[][] IDENTITY_PC = {{1.0, 0.0}, {0.0, 1.0}};

    @Nested
    class Forward {
        @Test
        void identityCase() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(0.0, 0.0);
            assertEquals(0.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(0.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void atCrpixOutputEqualsCrval() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{100.0, 100.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(100.0, 100.0);
            assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void simpleShift() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{512.0, 512.0},
                    new double[]{180.0, 45.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(512.0, 512.0);
            assertEquals(180.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
        }
    }

    @Nested
    class RoundTrip {
        @Test
        void identityScaleRoundTrip() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{100.0, 100.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(110.0, 105.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(110.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(105.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }

        @Test
        void rotatedCdMatrixRoundTrip() {
            final double angle = Math.toRadians(30.0);
            final double[][] pc = {
                    {Math.cos(angle), -Math.sin(angle)},
                    {Math.sin(angle), Math.cos(angle)}
            };
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{256.0, 256.0},
                    new double[]{120.0, -20.0},
                    new double[]{-2.8e-5, 2.8e-5},
                    pc
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(300.0, 280.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(300.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(280.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }

        @Test
        void atCrpixRoundTrip() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{512.0, 512.0},
                    new double[]{180.0, 45.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(512.0, 512.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(512.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(512.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }
    }

    @Nested
    class Construction {
        @Test
        void wrongLengthCrpix() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC
            ));
        }

        @Test
        void wrongLengthCrval() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC
            ));
        }

        @Test
        void wrongLengthCdelt() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0},
                    IDENTITY_PC
            ));
        }

        @Test
        void wrongInnerRowLengthFirstRow() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    new double[][]{{1.0}, {0.0, 1.0}}
            ));
        }

        @Test
        void wrongInnerRowLengthSecondRow() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    new double[][]{{1.0, 0.0}, {1.0, 0.0, 0.0}}
            ));
        }

        @Test
        void singularCdMatrix() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 1.0},
                    IDENTITY_PC
            ));
        }
    }

    @Nested
    class Inverse {
        @Test
        void hasInverse() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC
            );
            assertTrue(wcs.hasInverse());
        }

        @Test
        void inverseOfInverseIsForward() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{100.0, 100.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            assertTrue(inverse.hasInverse());

            final double[] sky = wcs.evaluate(110.0, 105.0);
            final double[] skyViaDoubleInverse = inverse.getInverse().evaluate(110.0, 105.0);
            assertEquals(sky[0], skyViaDoubleInverse[0], DOUBLE_TOLERANCE);
            assertEquals(sky[1], skyViaDoubleInverse[1], DOUBLE_TOLERANCE);
        }
    }

    @Nested
    class InputOutputCounts {
        @Test
        void counts() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC
            );
            assertEquals(2, wcs.getInputCount());
            assertEquals(2, wcs.getOutputCount());
        }
    }
}
