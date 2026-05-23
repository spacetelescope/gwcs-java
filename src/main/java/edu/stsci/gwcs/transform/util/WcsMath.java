package edu.stsci.gwcs.transform.util;

/**
 * Degree-input trig helpers that return exact results at multiples of 90° (or 180° for tand).
 * Java's {@code Math.sin(Math.toRadians(x))} returns tiny non-zero values at these points due to
 * floating-point representation of pi; those small errors accumulate through projection math and
 * cause differences from wcslib/astropy reference values at the poles and longitude boundaries.
 *
 * <p>Mirrors wcslib's wcstrig.c (sind/cosd/tand/asind/atan2d).
 */
public class WcsMath {

    private static final double ASIN_CLAMP_TOL = 1.0e-10;

    private WcsMath() {
    }

    public static double sind(final double angleDeg) {
        if (!Double.isFinite(angleDeg)) {
            return Math.sin(Math.toRadians(angleDeg));
        }
        if (angleDeg % 90.0 == 0.0) {
            final int i = ((int) (((long) Math.abs(Math.floor(angleDeg / 90.0 - 0.5)))) & 3);
            switch (i) {
                case 0: return 1.0;
                case 1: return 0.0;
                case 2: return -1.0;
                default: return 0.0;
            }
        }
        return Math.sin(Math.toRadians(angleDeg));
    }

    public static double cosd(final double angleDeg) {
        if (!Double.isFinite(angleDeg)) {
            return Math.cos(Math.toRadians(angleDeg));
        }
        if (angleDeg % 90.0 == 0.0) {
            final int i = ((int) (((long) Math.abs(Math.floor(angleDeg / 90.0 + 0.5)))) & 3);
            switch (i) {
                case 0: return 1.0;
                case 1: return 0.0;
                case 2: return -1.0;
                default: return 0.0;
            }
        }
        return Math.cos(Math.toRadians(angleDeg));
    }

    public static double asind(final double v) {
        if (v >= 1.0) {
            return v - 1.0 < ASIN_CLAMP_TOL ? 90.0 : Double.NaN;
        }
        if (v <= -1.0) {
            return -1.0 - v < ASIN_CLAMP_TOL ? -90.0 : Double.NaN;
        }
        return Math.toDegrees(Math.asin(v));
    }

    public static double acosd(final double v) {
        if (v >= 1.0) {
            return v - 1.0 < ASIN_CLAMP_TOL ? 0.0 : Double.NaN;
        }
        if (v <= -1.0) {
            return -1.0 - v < ASIN_CLAMP_TOL ? 180.0 : Double.NaN;
        }
        return Math.toDegrees(Math.acos(v));
    }

    public static double atan2d(final double y, final double x) {
        if (y == 0.0) {
            if (Double.isNaN(x)) {
                return Double.NaN;
            }
            return x >= 0.0 ? 0.0 : 180.0;
        }
        if (x == 0.0) {
            if (Double.isNaN(y)) {
                return Double.NaN;
            }
            return y > 0.0 ? 90.0 : -90.0;
        }
        return Math.toDegrees(Math.atan2(y, x));
    }

    public static double tand(final double angleDeg) {
        final double resid = angleDeg % 360.0;
        if (resid == 0.0 || Math.abs(resid) == 180.0) {
            return 0.0;
        } else if (resid == 45.0 || resid == -315.0) {
            return 1.0;
        } else if (resid == -45.0 || resid == 315.0) {
            return -1.0;
        } else if (resid == 135.0 || resid == -225.0) {
            return -1.0;
        } else if (resid == -135.0 || resid == 225.0) {
            return 1.0;
        }
        return Math.tan(Math.toRadians(angleDeg));
    }
}
