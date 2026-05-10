package edu.stsci.gwcs;

import edu.stsci.gwcs.coordinate.CelestialFrame;
import edu.stsci.gwcs.coordinate.CompositeFrame;
import edu.stsci.gwcs.coordinate.Frame;
import edu.stsci.gwcs.coordinate.Frame2D;
import edu.stsci.gwcs.transform.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

class WcsTest {
    private static final double ROUND_TRIP_TOLERANCE = 1e-5;

    private Frame2D detectorFrame() {
        return new Frame2D("detector",
                new String[]{"x", "y"}, new int[]{0, 1},
                new String[]{"custom:x", "custom:y"}, new String[]{"pixel", "pixel"});
    }

    private CelestialFrame celestialFrame() {
        return new CelestialFrame("world",
                new String[]{"lon", "lat"}, new int[]{0, 1},
                new String[]{"pos.eq.ra", "pos.eq.dec"}, new String[]{"deg", "deg"},
                "ICRS");
    }

    private Frame2D intermediateFrame(final String name) {
        return new Frame2D(name,
                new String[]{"x", "y"}, new int[]{0, 1},
                new String[]{"custom:x", "custom:y"}, new String[]{"arcsec", "arcsec"});
    }

    @Nested
    class CalPipeline {
        @Test
        void fiveStepForwardAndInverseRoundTrip() {
            final Affine step1 = new Affine(
                    new double[][]{{0.1, 0.0}, {0.0, 0.1}},
                    new double[]{1.0, 2.0}
            );
            final Affine step2 = new Affine(
                    new double[][]{{1.0, 0.01}, {-0.01, 1.0}},
                    new double[]{0.5, -0.3}
            );
            final Affine step3 = new Affine(
                    new double[][]{{1.001, 0.0}, {0.0, 0.999}},
                    new double[]{0.0, 0.0}
            );
            final Affine step4 = new Affine(
                    new double[][]{{0.001, 0.0}, {0.0, 0.001}},
                    new double[]{45.0, 30.0}
            );

            final Step[] steps = {
                    new Step(detectorFrame(), step1),
                    new Step(intermediateFrame("v2v3"), step2),
                    new Step(intermediateFrame("v2v3vacorr"), step3),
                    new Step(intermediateFrame("v2v3corr"), step4),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("cal_wcs", steps, null, null);

            final double[] sky = wcs.evaluate(100.0, 200.0);
            assertEquals(2, sky.length);

            assertTrue(wcs.hasInverse());
            final double[] pixel = wcs.evaluateInverse(sky);
            assertEquals(100.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(200.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }
    }

    @Nested
    class CoaddPipeline {
        @Test
        void twoStepFitsWcsRoundTrip() {
            final FitsWcsImaging fitsWcs = new FitsWcsImaging(
                    new double[]{512.0, 512.0},
                    new double[]{180.0, 45.0},
                    new double[]{-1e-4, 1e-4},
                    new double[][]{{1.0, 0.0}, {0.0, 1.0}}
            );

            final Step[] steps = {
                    new Step(detectorFrame(), fitsWcs),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("coadd_wcs", steps, null, null);

            final double[] sky = wcs.evaluate(520.0, 530.0);
            assertEquals(2, sky.length);

            final double[] pixel = wcs.evaluateInverse(sky);
            assertEquals(520.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(530.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }
    }

    @Nested
    class BackwardTransform {
        @Test
        void explicitBackwardTransformUsed() {
            final Affine forward = new Affine(
                    new double[][]{{2.0, 0.0}, {0.0, 3.0}},
                    new double[]{10.0, 20.0}
            );
            final Affine explicitInverse = new Affine(
                    new double[][]{{0.5, 0.0}, {0.0, 1.0 / 3.0}},
                    new double[]{-5.0, -20.0 / 3.0}
            );

            final Step[] steps = {
                    new Step(detectorFrame(), forward),
                    new Step(intermediateFrame("output"), null)
            };

            final Wcs wcs = new Wcs("test_wcs", steps, null, explicitInverse);

            final double[] sky = wcs.evaluate(1.0, 1.0);
            final double[] pixel = wcs.evaluateInverse(sky);
            assertEquals(1.0, pixel[0], DOUBLE_TOLERANCE);
            assertEquals(1.0, pixel[1], DOUBLE_TOLERANCE);
        }
    }

    @Nested
    class Accessors {
        @Test
        void getNameStepsPixelShape() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("my_wcs", steps, new int[]{4096, 4096}, null);

            assertEquals("my_wcs", wcs.getName());
            assertEquals(2, wcs.getSteps().length);
            assertArrayEquals(new int[]{4096, 4096}, wcs.getPixelShape());
        }

        @Test
        void nullPixelShape() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("wcs", steps, null, null);
            assertNull(wcs.getPixelShape());
        }

        @Test
        void inputAndOutputFrames() {
            final Frame2D detector = detectorFrame();
            final CelestialFrame world = celestialFrame();
            final Step[] steps = {
                    new Step(detector, new Identity(2)),
                    new Step(world, null)
            };

            final Wcs wcs = new Wcs("wcs", steps, null, null);
            assertSame(detector, wcs.getInputFrame());
            assertSame(world, wcs.getOutputFrame());
        }
    }

    @Nested
    class ConstructionValidation {
        @Test
        void lessThanTwoSteps() {
            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs",
                    new Step[]{new Step(detectorFrame(), null)},
                    null, null
            ));
        }

        @Test
        void lastStepHasNonNullTransform() {
            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs",
                    new Step[]{
                            new Step(detectorFrame(), new Identity(2)),
                            new Step(celestialFrame(), new Identity(2))
                    },
                    null, null
            ));
        }

        @Test
        void nonTerminalStepHasNullTransform() {
            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs",
                    new Step[]{
                            new Step(detectorFrame(), null),
                            new Step(intermediateFrame("mid"), new Identity(2)),
                            new Step(celestialFrame(), null)
                    },
                    null, null
            ));
        }

        @Test
        void dimensionMismatchBetweenConsecutiveTransforms() {
            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs",
                    new Step[]{
                            new Step(detectorFrame(), new Identity(2)),
                            new Step(intermediateFrame("mid"), new Identity(3)),
                            new Step(celestialFrame(), null)
                    },
                    null, null
            ));
        }

        @Test
        void firstTransformInputMismatchesFrameAxisCount() {
            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs",
                    new Step[]{
                            new Step(detectorFrame(), new Identity(3)),
                            new Step(celestialFrame(), null)
                    },
                    null, null
            ));
        }

        @Test
        void lastTransformOutputMismatchesOutputFrameAxisCount() {
            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs",
                    new Step[]{
                            new Step(detectorFrame(), new Constant(2, 42.0)),
                            new Step(celestialFrame(), null)
                    },
                    null, null
            ));
        }

        @Test
        void backwardTransformInputMismatchesOutputFrame() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs", steps, null, new Identity(3)
            ));
        }

        @Test
        void backwardTransformOutputMismatchesInputFrame() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            final SphericalToCartesian mismatch = new SphericalToCartesian();

            assertThrows(IllegalArgumentException.class, () -> new Wcs(
                    "wcs", steps, null, mismatch
            ));
        }
    }

    @Nested
    class TransformAccessors {
        @Test
        void getForwardTransform() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("wcs", steps, null, null);
            final Transform forward = wcs.getForwardTransform();
            assertNotNull(forward);
            final double[] result = forward.evaluate(1.0, 2.0);
            assertEquals(1.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(2.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void getBackwardTransformWhenInvertible() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("wcs", steps, null, null);
            assertTrue(wcs.hasInverse());
            final Transform backward = wcs.getBackwardTransform();
            assertNotNull(backward);
        }
    }

    @Nested
    class NonInvertiblePipeline {
        @Test
        void hasInverseReturnsFalse() {
            final Concatenate polyTransform = new Concatenate(new Transform[]{
                    new Polynomial1D(new double[]{1.0, 2.0, 3.0}, null, null),
                    new Polynomial1D(new double[]{4.0, 5.0, 6.0}, null, null)
            });

            final Step[] steps = {
                    new Step(detectorFrame(), polyTransform),
                    new Step(intermediateFrame("output"), null)
            };

            final Wcs wcs = new Wcs("non_invertible", steps, null, null);
            assertFalse(wcs.hasInverse());
            assertThrows(UnsupportedOperationException.class, () -> wcs.evaluateInverse(1.0, 2.0));
        }

        @Test
        void evaluateInverseWithOffsetThrows() {
            final Concatenate polyTransform = new Concatenate(new Transform[]{
                    new Polynomial1D(new double[]{1.0, 2.0, 3.0}, null, null),
                    new Polynomial1D(new double[]{4.0, 5.0, 6.0}, null, null)
            });

            final Step[] steps = {
                    new Step(detectorFrame(), polyTransform),
                    new Step(intermediateFrame("output"), null)
            };

            final Wcs wcs = new Wcs("non_invertible", steps, null, null);
            assertThrows(UnsupportedOperationException.class,
                    () -> wcs.evaluateInverse(new double[]{1.0, 2.0}, 0, new double[2], 0));
        }

        @Test
        void getBackwardTransformThrows() {
            final Concatenate polyTransform = new Concatenate(new Transform[]{
                    new Polynomial1D(new double[]{1.0, 2.0, 3.0}, null, null),
                    new Polynomial1D(new double[]{4.0, 5.0, 6.0}, null, null)
            });

            final Step[] steps = {
                    new Step(detectorFrame(), polyTransform),
                    new Step(intermediateFrame("output"), null)
            };

            final Wcs wcs = new Wcs("non_invertible", steps, null, null);
            assertThrows(UnsupportedOperationException.class, wcs::getBackwardTransform);
        }
    }

    @Nested
    class OffsetEvaluate {
        @Test
        void evaluateWithOffset() {
            final Step[] steps = {
                    new Step(detectorFrame(), new Identity(2)),
                    new Step(celestialFrame(), null)
            };

            final Wcs wcs = new Wcs("wcs", steps, null, null);

            final double[] inputs = {99.0, 100.0, 200.0, 99.0};
            final double[] outputs = new double[4];
            wcs.evaluate(inputs, 1, outputs, 2);

            assertEquals(100.0, outputs[2], DOUBLE_TOLERANCE);
            assertEquals(200.0, outputs[3], DOUBLE_TOLERANCE);
        }

        @Test
        void evaluateInverseWithOffset() {
            final Affine forward = new Affine(
                    new double[][]{{2.0, 0.0}, {0.0, 3.0}},
                    new double[]{10.0, 20.0}
            );

            final Step[] steps = {
                    new Step(detectorFrame(), forward),
                    new Step(intermediateFrame("output"), null)
            };

            final Wcs wcs = new Wcs("wcs", steps, null, null);

            final double[] sky = wcs.evaluate(5.0, 5.0);
            final double[] inputs = {0.0, sky[0], sky[1]};
            final double[] outputs = new double[4];
            wcs.evaluateInverse(inputs, 1, outputs, 2);

            assertEquals(5.0, outputs[2], ROUND_TRIP_TOLERANCE);
            assertEquals(5.0, outputs[3], ROUND_TRIP_TOLERANCE);
        }
    }

    @Nested
    class CompositeFrameIntegration {
        @Test
        void outputFrameCompositeAxesOrderAccessible() {
            final CelestialFrame celestial = new CelestialFrame("icrs",
                    new String[]{"lon", "lat"}, new int[]{2, 3},
                    new String[]{"pos.eq.ra", "pos.eq.dec"}, new String[]{"deg", "deg"},
                    "ICRS");
            final Frame2D spatial = new Frame2D("spatial",
                    new String[]{"x", "y"}, new int[]{0, 1},
                    new String[]{"custom:x", "custom:y"}, new String[]{"arcsec", "arcsec"});
            final CompositeFrame outputComposite = new CompositeFrame("composite",
                    new Frame[]{spatial, celestial});

            final Frame2D inputA = new Frame2D("inA",
                    new String[]{"a", "b"}, new int[]{0, 1},
                    new String[]{"t1", "t2"}, new String[]{"u1", "u2"});
            final Frame2D inputB = new Frame2D("inB",
                    new String[]{"c", "d"}, new int[]{2, 3},
                    new String[]{"t3", "t4"}, new String[]{"u3", "u4"});
            final CompositeFrame inputComposite = new CompositeFrame("input_composite",
                    new Frame[]{inputA, inputB});

            final Step[] steps = {
                    new Step(inputComposite, new Identity(4)),
                    new Step(outputComposite, null)
            };

            final Wcs wcs = new Wcs("test", steps, null, null);
            final Frame outputFrame = wcs.getOutputFrame();
            assertArrayEquals(new int[]{0, 1, 2, 3}, outputFrame.getAxisOrder());
            assertEquals(4, outputFrame.getAxisCount());
        }
    }
}
