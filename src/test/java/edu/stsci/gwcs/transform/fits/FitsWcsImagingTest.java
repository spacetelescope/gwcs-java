package edu.stsci.gwcs.transform.fits;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.projection.conic.ConicPerspective;
import edu.stsci.gwcs.transform.projection.conic.ConicEqualArea;
import edu.stsci.gwcs.transform.projection.conic.ConicEquidistant;
import edu.stsci.gwcs.transform.projection.conic.ConicOrthomorphic;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.FitsWcsImaging
 * (Pix2Sky_FitsWcsImaging / Sky2Pix_FitsWcsImaging). Regenerate via the project tooling if tolerances drift.
 */
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
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(99.0, 99.0);
            assertEquals(45.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(30.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void atCrpixWithPolarCrval() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 90.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(99.0, 99.0);
            assertEquals(90.0, result[1], DOUBLE_TOLERANCE);
        }

        @Test
        void simpleShift() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 45.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(511.0, 511.0);
            assertEquals(180.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
        }
    }

    @Nested
    class AlphaNormalization {
        @Test
        void alphaWrapsToPositiveRange() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{1.0, 45.0},
                    new double[]{-0.01, 0.01},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(611.0, 511.0);
            assertTrue(result[0] >= 0.0 && result[0] < 360.0,
                    "Alpha should be in [0, 360) but was " + result[0]);
        }
    }

    @Nested
    class RoundTrip {
        @Test
        void identityScaleRoundTrip() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(109.0, 104.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(109.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(104.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }

        @Test
        void rotatedCdMatrixRoundTrip() {
            final double angle = Math.toRadians(30.0);
            final double[][] pc = {
                    {Math.cos(angle), -Math.sin(angle)},
                    {Math.sin(angle), Math.cos(angle)}
            };
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{255.0, 255.0},
                    new double[]{120.0, -20.0},
                    new double[]{-2.8e-5, 2.8e-5},
                    pc
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(299.0, 279.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(299.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(279.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }

        @Test
        void atCrpixRoundTrip() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 45.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(511.0, 511.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(511.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(511.0, pixel[1], ROUND_TRIP_TOLERANCE);
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

        @Test
        void nonFiniteLonPoleRejected() {
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC,
                    Double.NaN
            ));
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC,
                    Double.POSITIVE_INFINITY
            ));
            assertThrows(IllegalArgumentException.class, () -> new FitsWcsImaging(
                    new double[]{0.0, 0.0},
                    new double[]{0.0, 0.0},
                    new double[]{1.0, 1.0},
                    IDENTITY_PC,
                    Double.NEGATIVE_INFINITY
            ));
        }
    }

    /**
     * Tests for the FITS-default {@code lonPole} derivation rule (FITS WCS Paper II §2.4 /
     * wcslib {@code cel.c:282}). For the TAN projection (theta0 = 90, phi0 = 0), the rule is
     * {@code lonPole = (crval[1] < 90) ? 180 : 0}. The tests assert the derivation by comparing
     * the no-{@code lonPole} constructor against the explicit form at the same parameters,
     * since the two should be observationally identical when the explicit value matches the
     * derived default.
     */
    @Nested
    class LonPole {
        @Test
        void defaultIs180ForCrvalBelow90() {
            final FitsWcsImaging derived = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final FitsWcsImaging explicit180 = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC,
                    180.0
            );
            final double[] derivedOut = derived.evaluate(149.0, 74.0);
            final double[] explicitOut = explicit180.evaluate(149.0, 74.0);
            assertEquals(explicitOut[0], derivedOut[0], DOUBLE_TOLERANCE);
            assertEquals(explicitOut[1], derivedOut[1], DOUBLE_TOLERANCE);
        }

        @Test
        void defaultIsZeroAtNorthCelestialPole() {
            // crval at the celestial pole: FITS default rule gives lonPole = 0.
            final FitsWcsImaging derived = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC
            );
            final FitsWcsImaging explicitZero = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC,
                    0.0
            );
            // A pixel one step from crpix lands near but not at the pole; longitude is well-defined.
            final double[] derivedOut = derived.evaluate(100.0, 99.0);
            final double[] explicitOut = explicitZero.evaluate(100.0, 99.0);
            assertEquals(explicitOut[0], derivedOut[0], DOUBLE_TOLERANCE);
            assertEquals(explicitOut[1], derivedOut[1], DOUBLE_TOLERANCE);
        }

        @Test
        void defaultIs180AtSouthCelestialPole() {
            // crval[1] = -90 satisfies (crval[1] < 90), so FITS default gives lonPole = 180.
            final FitsWcsImaging derived = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, -90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC
            );
            final FitsWcsImaging explicit180 = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, -90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC,
                    180.0
            );
            final double[] derivedOut = derived.evaluate(100.0, 99.0);
            final double[] explicitOut = explicit180.evaluate(100.0, 99.0);
            assertEquals(explicitOut[0], derivedOut[0], DOUBLE_TOLERANCE);
            assertEquals(explicitOut[1], derivedOut[1], DOUBLE_TOLERANCE);
        }

        @Test
        void polarLonPole180DiffersFromLonPole0By180Degrees() {
            // At the pole, lonPole selects which celestial meridian the native +y axis points
            // along. Switching from 0 to 180 rotates the projection by exactly 180 degrees
            // around the polar axis; verify by checking longitudes are antipodal at a near-pole
            // pixel offset where alpha is well-defined.
            final FitsWcsImaging atZero = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC,
                    0.0
            );
            final FitsWcsImaging at180 = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC,
                    180.0
            );
            final double[] outZero = atZero.evaluate(100.0, 99.0);
            final double[] out180 = at180.evaluate(100.0, 99.0);

            // Same latitude (the pixel sits at the same declination regardless of lonPole).
            assertEquals(out180[1], outZero[1], DOUBLE_TOLERANCE);

            // Longitudes differ by 180 degrees modulo 360.
            final double delta = ((outZero[0] - out180[0]) % 360.0 + 360.0) % 360.0;
            assertEquals(180.0, delta, DOUBLE_TOLERANCE);
        }

        @Test
        void explicitLonPolePropagatesThroughInverse() {
            // The inverse of an explicit-lonPole forward should round-trip with the same lonPole.
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 90.0},
                    new double[]{-1e-3, 1e-3},
                    IDENTITY_PC,
                    180.0
            );
            final Transform inverse = wcs.getInverse();
            final double[] sky = wcs.evaluate(100.0, 99.0);
            final double[] pixel = inverse.evaluate(sky);
            assertEquals(100.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(99.0, pixel[1], ROUND_TRIP_TOLERANCE);
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
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            assertTrue(inverse.hasInverse());

            final double[] sky = wcs.evaluate(109.0, 104.0);
            final double[] skyViaDoubleInverse = inverse.getInverse().evaluate(109.0, 104.0);
            assertEquals(sky[0], skyViaDoubleInverse[0], DOUBLE_TOLERANCE);
            assertEquals(sky[1], skyViaDoubleInverse[1], DOUBLE_TOLERANCE);
        }

        @Test
        void inverseAtCrvalReturnsCrpix() {
            // Independent reference: by definition of the WCS reference point,
            // sky = crval must map back to pixel = crpix.
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 199.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(45.0, 30.0);
            assertEquals(99.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(199.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }

        @Test
        void inverseAtCrvalReturnsCrpixWithRotation() {
            final double angle = Math.toRadians(30.0);
            final double[][] pc = {
                    {Math.cos(angle), -Math.sin(angle)},
                    {Math.sin(angle), Math.cos(angle)}
            };
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{255.0, 255.0},
                    new double[]{120.0, -20.0},
                    new double[]{-2.8e-5, 2.8e-5},
                    pc
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(120.0, -20.0);
            assertEquals(255.0, pixel[0], ROUND_TRIP_TOLERANCE);
            assertEquals(255.0, pixel[1], ROUND_TRIP_TOLERANCE);
        }

    }

    @Nested
    class AstropyReference {
        // Reference values computed via astropy 7.2.0 (RA---TAN, DEC--TAN):
        //   from astropy.wcs import WCS
        //   w = WCS(naxis=2); w.wcs.crpix=...; w.wcs.crval=...; w.wcs.cdelt=...; w.wcs.pc=...
        //   w.wcs.ctype = ["RA---TAN", "DEC--TAN"]
        //   w.wcs_world2pix([[ra, dec]], 0)
        // Asserts catch matched forward/inverse bugs that the round-trip tests would mask.
        private static final double ASTROPY_TOLERANCE = 1.0e-9;

        @Test
        void centeredCrvalPositiveOffset() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(45.5, 30.3);
            assertEquals(530.710505048984942, pixel[0], ASTROPY_TOLERANCE);
            assertEquals(399.953135512700385, pixel[1], ASTROPY_TOLERANCE);
        }

        @Test
        void centeredCrvalNegativeOffset() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(44.7, 29.8);
            assertEquals(-161.332714344057308, pixel[0], ASTROPY_TOLERANCE);
            assertEquals(-100.662097148675542, pixel[1], ASTROPY_TOLERANCE);
        }

        @Test
        void rotatedCdSmallPositiveOffset() {
            final double angle = Math.toRadians(30.0);
            final double[][] pc = {
                    {Math.cos(angle), -Math.sin(angle)},
                    {Math.sin(angle), Math.cos(angle)}
            };
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{255.0, 255.0},
                    new double[]{120.0, -20.0},
                    new double[]{-2.8e-5, 2.8e-5},
                    pc
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(120.001, -19.999);
            assertEquals(243.792705239668123, pixel[0], ASTROPY_TOLERANCE);
            assertEquals(302.709723934350393, pixel[1], ASTROPY_TOLERANCE);
        }

        @Test
        void rotatedCdSmallNegativeOffset() {
            final double angle = Math.toRadians(30.0);
            final double[][] pc = {
                    {Math.cos(angle), -Math.sin(angle)},
                    {Math.sin(angle), Math.cos(angle)}
            };
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{255.0, 255.0},
                    new double[]{120.0, -20.0},
                    new double[]{-2.8e-5, 2.8e-5},
                    pc
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(119.998, -20.002);
            assertEquals(277.413181230463067, pixel[0], ASTROPY_TOLERANCE);
            assertEquals(159.580671173105486, pixel[1], ASTROPY_TOLERANCE);
        }

        @Test
        void highLatitudeCrvalOffset() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 85.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(180.5, 84.9);
            assertEquals(66.533348970949646, pixel[0], ASTROPY_TOLERANCE);
            assertEquals(-487.069326338936435, pixel[1], ASTROPY_TOLERANCE);
        }

        @Test
        void highLatitudeCrvalAcrossPole() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 85.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(179.7, 85.05);
            assertEquals(769.858039851313833, pixel[0], ASTROPY_TOLERANCE);
            assertEquals(1011.675289963203454, pixel[1], ASTROPY_TOLERANCE);
        }

        @Test
        void evaluateInverseAtAntipodeReturnsNaN() {
            // The TAN projection diverges in the back hemisphere; for crval=(180, 45),
            // the antipode (0, -45) lies on the far side of the sky and must return
            // (NaN, NaN) rather than silently mapping to crpix.
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{180.0, 45.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(0.0, -45.0);
            assertTrue(Double.isNaN(pixel[0]) && Double.isNaN(pixel[1]),
                    "Expected (NaN, NaN) at antipode; got (" + pixel[0]
                            + ", " + pixel[1] + ")");
        }

        @Test
        void inverseAtCelestialPoleMatchesAstropyNaN() {
            // Astropy 7.2.0 returns (NaN, NaN) for sky=(0, 90) when crval=(0, 0):
            // the celestial pole maps to native theta=0 exactly when WcsMath.cosd(90)
            // is exactly 0, so the sinTheta=0 guard in evaluateInverse fires.
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{0.0, 0.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final Transform inverse = wcs.getInverse();
            final double[] pixel = inverse.evaluate(0.0, 90.0);
            assertTrue(Double.isNaN(pixel[0]) && Double.isNaN(pixel[1]),
                    "Expected (NaN, NaN) matching astropy; got (" + pixel[0]
                            + ", " + pixel[1] + ")");
        }
    }

    @Nested
    class ForwardAstropyReference {
        private static final double ASTROPY_TOLERANCE = 1.0e-9;

        @Test
        void identityScalePositiveOffset() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(109.0, 104.0);
            assertEquals(4.501154758703316e+01, result[0], ASTROPY_TOLERANCE);
            assertEquals(3.000499949605263e+01, result[1], ASTROPY_TOLERANCE);
        }

        @Test
        void rotatedCdOffset() {
            final double angle = Math.toRadians(30.0);
            final double[][] pc = {
                    {Math.cos(angle), -Math.sin(angle)},
                    {Math.sin(angle), Math.cos(angle)}
            };
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{255.0, 255.0},
                    new double[]{120.0, -20.0},
                    new double[]{-2.8e-5, 2.8e-5},
                    pc
            );
            final double[] result = wcs.evaluate(299.0, 279.0);
            assertEquals(1.199992221523094e+02, result[0], ASTROPY_TOLERANCE);
            assertEquals(-1.999880202923195e+01, result[1], ASTROPY_TOLERANCE);
        }

        @Test
        void highLatitudeSmallOffset() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 85.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(519.0, 529.0);
            assertEquals(1.799908177322612e+02, result[0], ASTROPY_TOLERANCE);
            assertEquals(8.500179993613884e+01, result[1], ASTROPY_TOLERANCE);
        }

        @Test
        void negativeCdeltLargeOffset() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 45.0},
                    new double[]{-1e-4, 1e-4},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(611.0, 411.0);
            assertEquals(1.799858603325009e+02, result[0], ASTROPY_TOLERANCE);
            assertEquals(4.498999912774153e+01, result[1], ASTROPY_TOLERANCE);
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

    /**
     * Regression: near the celestial pole the inverse trig at high latitudes is the most
     * precision-sensitive path. At exactly crpix the output must equal crval, regardless of
     * how close crval2 gets to 90.
     *
     * <p>The tolerance here is {@code 5e-14 deg}. The composed Gnomonic + RotateNative2Celestial
     * path does a few more trig operations than a minimal implementation and accumulates slightly
     * more rounding error, but the result is still far below any practical precision requirement.
     */
    @Nested
    class NearPolePrecision {
        @ParameterizedTest
        @ValueSource(doubles = {88.0, 89.0, 89.9, 89.99})
        void forwardAtCrpixRecoversCrvalNearNorthPole(final double crval2) {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, crval2},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(99.0, 99.0);
            assertEquals(45.0, result[0], 5e-14);
            assertEquals(crval2, result[1], 5e-14);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-88.0, -89.0, -89.9, -89.99})
        void forwardAtCrpixRecoversCrvalNearSouthPole(final double crval2) {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, crval2},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(99.0, 99.0);
            assertEquals(45.0, result[0], 5e-14);
            assertEquals(crval2, result[1], 5e-14);
        }
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final FitsWcsImaging wcs = new FitsWcsImaging(
                new double[]{99.0, 99.0},
                new double[]{45.0, 30.0},
                new double[]{0.001, 0.001},
                IDENTITY_PC
        );
        final double[] inputs = new double[]{98.0, 98.0, 99.0, 99.0, 98.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        wcs.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(45.0, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(30.0, outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }

    @Nested
    class CdMatrixConstructor {
        @Test
        void testCdMatrixConstructorRoundTrip() {
            final double[][] cd = {{-2.8e-5, 0.0}, {0.0, 2.8e-5}};
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{511.0, 511.0},
                    new double[]{180.0, 45.0},
                    cd
            );
            final double[] result = wcs.evaluate(511.0, 511.0);
            assertEquals(180.0, result[0], DOUBLE_TOLERANCE);
            assertEquals(45.0, result[1], DOUBLE_TOLERANCE);
        }
    }

    /**
     * End-to-end tests for conic projections through the FitsWcsImaging pipeline.
     *
     * Reference values generated with astropy 7.2.0:
     *   w = WCS(naxis=2); w.wcs.crpix=[512,512]; w.wcs.crval=[120,30]; w.wcs.cdelt=[-0.01,0.01]
     *   w.wcs.ctype=["RA---COP","DEC--COP"]; w.wcs.set_pv([(2,1,45.0),(2,2,15.0)]); w.wcs.set()
     *   w.pixel_to_world_values(px, py)
     *
     */
    @Nested
    class ConicProjectionReference {
        private final double ASTROPY_TOLERANCE = 1.0e-9;
        private final double[] CRPIX = {511.0, 511.0};
        private final double[] CRVAL = {120.0, 30.0};
        private final double[] CDELT = {-0.01, 0.01};
        private final double SIGMA = 45.0;
        private final double DELTA = 15.0;

        @Test
        void conicPerspectiveForward() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicPerspective(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);

            assertForward(wcs, 511.0, 511.0, 120.000000000000000, 30.000000000000000);
            assertForward(wcs, 0.0, 0.0, 125.794279915693863, 24.592818208371281);
            assertForward(wcs, 1023.0, 0.0, 114.194401500801618, 24.592301956348233);
            assertForward(wcs, 0.0, 1023.0, 126.446952069644567, 35.138798005019645);
            assertForward(wcs, 1023.0, 1023.0, 113.540456749445525, 35.138222647858271);
            assertForward(wcs, 255.0, 767.0, 123.143517501655396, 32.612193788264207);
        }

        @Test
        void conicPerspectiveInverse() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicPerspective(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);
            final Transform inverse = wcs.getInverse();

            assertInverse(inverse, 120.000000000000000, 30.000000000000000, 511.0, 511.0);
            assertInverse(inverse, 125.794279915693863, 24.592818208371281, 0.0, 0.0);
            assertInverse(inverse, 114.194401500801618, 24.592301956348233, 1023.0, 0.0);
            assertInverse(inverse, 126.446952069644567, 35.138798005019645, 0.0, 1023.0);
        }

        @Test
        void conicEqualAreaForward() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicEqualArea(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);

            assertForward(wcs, 511.0, 511.0, 120.000000000000000, 29.999999999999993);
            assertForward(wcs, 0.0, 0.0, 125.795755583634772, 24.935167329534476);
            assertForward(wcs, 1023.0, 0.0, 114.192921948103020, 24.934722742147649);
            assertForward(wcs, 0.0, 1023.0, 126.439078969898972, 34.824875219029217);
            assertForward(wcs, 255.0, 767.0, 123.140664758441517, 32.443231448666751);
        }

        @Test
        void conicEqualAreaInverse() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicEqualArea(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);
            final Transform inverse = wcs.getInverse();

            assertInverse(inverse, 120.000000000000000, 29.999999999999993, 511.0, 511.0);
            assertInverse(inverse, 125.795755583634772, 24.935167329534476, 0.0, 0.0);
            assertInverse(inverse, 114.192921948103020, 24.934722742147649, 1023.0, 0.0);
            assertInverse(inverse, 126.439078969898972, 34.824875219029217, 0.0, 1023.0);
        }

        @Test
        void conicEquidistantForward() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicEquidistant(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);

            assertForward(wcs, 511.0, 511.0, 120.000000000000000, 30.000000000000000);
            assertForward(wcs, 0.0, 0.0, 125.793681072721768, 24.766082826340487);
            assertForward(wcs, 1023.0, 0.0, 114.195001391147471, 24.765597375082134);
            assertForward(wcs, 0.0, 1023.0, 126.448592222304740, 34.983344738108848);
            assertForward(wcs, 255.0, 767.0, 123.143976817520084, 32.526557922468136);
        }

        @Test
        void conicEquidistantInverse() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicEquidistant(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);
            final Transform inverse = wcs.getInverse();

            assertInverse(inverse, 120.000000000000000, 30.000000000000000, 511.0, 511.0);
            assertInverse(inverse, 125.793681072721768, 24.766082826340487, 0.0, 0.0);
            assertInverse(inverse, 114.195001391147471, 24.765597375082134, 1023.0, 0.0);
        }

        @Test
        void conicOrthomorphicForward() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicOrthomorphic(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);

            assertForward(wcs, 511.0, 511.0, 120.000000000000000, 30.000000000000007);
            assertForward(wcs, 0.0, 0.0, 125.789603093344198, 24.583455192861198);
            assertForward(wcs, 1023.0, 0.0, 114.199088184231073, 24.582925391628084);
            assertForward(wcs, 0.0, 1023.0, 126.455406639454154, 35.145609533623301);
            assertForward(wcs, 255.0, 767.0, 123.145958403156584, 32.613549871841592);
        }

        @Test
        void conicOrthomorphicInverse() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new ConicOrthomorphic(SIGMA, DELTA),
                    CRPIX, CRVAL, CDELT, IDENTITY_PC);
            final Transform inverse = wcs.getInverse();

            assertInverse(inverse, 120.000000000000000, 30.000000000000007, 511.0, 511.0);
            assertInverse(inverse, 125.789603093344198, 24.583455192861198, 0.0, 0.0);
            assertInverse(inverse, 114.199088184231073, 24.582925391628084, 1023.0, 0.0);
            assertInverse(inverse, 126.455406639454154, 35.145609533623301, 0.0, 1023.0);
        }

        private void assertForward(final FitsWcsImaging wcs, final double px, final double py,
                                    final double expectedRa, final double expectedDec) {
            final double[] result = wcs.evaluate(px, py);
            assertEquals(expectedRa, result[0], ASTROPY_TOLERANCE,
                    "RA mismatch at pixel (" + px + ", " + py + ")");
            assertEquals(expectedDec, result[1], ASTROPY_TOLERANCE,
                    "Dec mismatch at pixel (" + px + ", " + py + ")");
        }

        private void assertInverse(final Transform inverse, final double ra, final double dec,
                                    final double expectedPx, final double expectedPy) {
            final double[] result = inverse.evaluate(ra, dec);
            assertEquals(expectedPx, result[0], ROUND_TRIP_TOLERANCE,
                    "Pixel X mismatch at world (" + ra + ", " + dec + ")");
            assertEquals(expectedPy, result[1], ROUND_TRIP_TOLERANCE,
                    "Pixel Y mismatch at world (" + ra + ", " + dec + ")");
        }
    }

    @Nested
    class NaNPropagation {
        @Test
        void forwardNaNFirstInput() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(Double.NaN, 99.0);
            assertTrue(Double.isNaN(result[0]));
            assertTrue(Double.isNaN(result[1]));
        }

        @Test
        void forwardNaNSecondInput() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(99.0, Double.NaN);
            assertTrue(Double.isNaN(result[0]));
            assertTrue(Double.isNaN(result[1]));
        }

        @Test
        void forwardBothNaN() {
            final FitsWcsImaging wcs = new FitsWcsImaging(
                    new double[]{99.0, 99.0},
                    new double[]{45.0, 30.0},
                    new double[]{0.001, 0.001},
                    IDENTITY_PC
            );
            final double[] result = wcs.evaluate(Double.NaN, Double.NaN);
            assertTrue(Double.isNaN(result[0]));
            assertTrue(Double.isNaN(result[1]));
        }
    }
}
