package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Airy projection — FITS WCS CTYPE code {@code AIR}. The pix-to-sky direction uses an
 * iterative solver ({@link #thetaFromR(double)}) since the Airy function has no closed-form
 * inverse.
 *
 * <p>{@code thetaFromR} returns {@link Double#NaN} when the input radius exceeds the
 * projection's valid range (which depends on {@code thetaB}).
 *
 * <p>See {@code C/prj.c:airx2s} in wcslib for the reference algorithm.
 */
public class Airy extends ZenithalProjection {

    private static final double SMALL_ANGLE_TOL = 1.0e-4;
    private static final int MAX_ITERATIONS = 100;
    private static final double CONVERGENCE_TOL = 1.0e-12;

    private final double thetaB;
    private final double twoR0;
    private final double xiCoeff;
    private final double halfMinusXiCoeff;
    private final double smallAngleScale;
    private final double smallAngleThreshold;
    private final double invSmallAngleScale;

    public Airy(final double thetaB, final Direction direction) {
        super(direction);
        if (!Double.isFinite(thetaB)) {
            throw new IllegalArgumentException("Airy projection thetaB must be finite, got " + thetaB);
        }
        if (thetaB <= -90.0) {
            throw new IllegalArgumentException("Airy projection thetaB must be > -90 degrees, got " + thetaB);
        }
        this.thetaB = thetaB;
        twoR0 = 2.0 * R0;

        if (thetaB == 90.0) {
            xiCoeff = -0.5;
            halfMinusXiCoeff = 1.0;
        } else {
            final double cosXiB = WcsMath.cosd((90.0 - thetaB) / 2.0);
            xiCoeff = Math.log(cosXiB) * (cosXiB * cosXiB) / (1.0 - cosXiB * cosXiB);
            halfMinusXiCoeff = 0.5 - xiCoeff;
        }

        smallAngleScale = twoR0 * halfMinusXiCoeff;
        smallAngleThreshold = halfMinusXiCoeff * SMALL_ANGLE_TOL;
        invSmallAngleScale = R0 / halfMinusXiCoeff;
    }

    public Airy(final double thetaB) {
        this(thetaB, Direction.PIX2SKY);
    }

    public Airy() {
        this(90.0, Direction.PIX2SKY);
    }

    @Override
    protected double rFromTheta(final double theta) {
        if (theta == 90.0) {
            return 0.0;
        }

        final double xiDeg = (90.0 - theta) / 2.0;
        final double xiRad = Math.toRadians(xiDeg);
        if (xiRad < SMALL_ANGLE_TOL) {
            return xiRad * smallAngleScale;
        }

        final double cosxi = WcsMath.cosd(xiDeg);
        final double tanxi = Math.sqrt(1.0 - cosxi * cosxi) / cosxi;
        return -twoR0 * (Math.log(cosxi) / tanxi + xiCoeff * tanxi);
    }

    @Override
    protected double thetaFromR(final double rTheta) {
        final double r = rTheta / twoR0;

        if (r == 0.0) {
            return 90.0;
        }

        if (r < smallAngleThreshold) {
            final double xiDeg = r * invSmallAngleScale;
            return 90.0 - 2.0 * xiDeg;
        }

        double x1 = 1.0;
        double r1 = 0.0;
        double x2 = 1.0;
        double r2 = 0.0;

        for (int k = 0; k < 30; k++) {
            x2 = x1 / 2.0;
            final double tanxi = Math.sqrt(1.0 - x2 * x2) / x2;
            r2 = -(Math.log(x2) / tanxi + xiCoeff * tanxi);

            if (r2 >= r) break;
            x1 = x2;
            r1 = r2;
        }

        if (r2 < r) return Double.NaN;

        double cosxi = x2;
        int k;
        for (k = 0; k < MAX_ITERATIONS; k++) {
            if (r2 == r1) break;
            double lambda = (r2 - r) / (r2 - r1);
            if (lambda < 0.1) {
                lambda = 0.1;
            } else if (lambda > 0.9) {
                lambda = 0.9;
            }
            cosxi = x2 - lambda * (x2 - x1);

            final double tanxi = Math.sqrt(1.0 - cosxi * cosxi) / cosxi;
            final double rt = -(Math.log(cosxi) / tanxi + xiCoeff * tanxi);

            if (rt < r) {
                if (r - rt < CONVERGENCE_TOL) break;
                r1 = rt;
                x1 = cosxi;
            } else {
                if (rt - r < CONVERGENCE_TOL) break;
                r2 = rt;
                x2 = cosxi;
            }
        }
        if (k == MAX_ITERATIONS) {
            return Double.NaN;
        }

        final double xiDeg = Math.toDegrees(Math.acos(cosxi));
        return 90.0 - 2.0 * xiDeg;
    }

    @Override
    protected Projection createInverse() {
        return new Airy(thetaB, opposite(getDirection()));
    }
}
