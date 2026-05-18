package edu.stsci.gwcs.transform.projection.conic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Conic orthomorphic (conformal) projection — FITS WCS CTYPE code {@code COO}.
 */
public class ConicOrthomorphic extends ConicProjection {

    private final double psi;
    private final double invPsi;

    public ConicOrthomorphic(final double sigma, final double delta, final Direction direction) {
        this(sigma, delta, computeC(sigma, delta), direction);
    }

    private ConicOrthomorphic(final double sigma, final double delta, final double c, final Direction direction) {
        super(sigma, delta, c, computeY0(sigma, delta, c), direction);

        final double theta1 = sigma - delta;
        psi = computePsi(theta1, c);
        if (psi == 0.0 || !Double.isFinite(psi)) {
            throw new IllegalArgumentException("COO projection is not defined for these parameters (degenerate psi)");
        }
        invPsi = 1.0 / psi;
    }

    public ConicOrthomorphic(final double sigma, final double delta) {
        this(sigma, delta, Direction.PIX2SKY);
    }

    private static double computePsi(final double theta1, final double c) {
        final double tan1 = WcsMath.tand((90.0 - theta1) / 2.0);
        final double cos1 = WcsMath.cosd(theta1);
        return R0 * (cos1 / c) / Math.pow(tan1, c);
    }

    private static double computeC(final double sigma, final double delta) {
        final double theta1 = sigma - delta;
        final double theta2 = sigma + delta;

        double c;
        if (theta1 == theta2) {
            c = WcsMath.sind(theta1);
        } else {
            final double tan1 = WcsMath.tand((90.0 - theta1) / 2.0);
            final double cos1 = WcsMath.cosd(theta1);
            final double tan2 = WcsMath.tand((90.0 - theta2) / 2.0);
            final double cos2 = WcsMath.cosd(theta2);
            c = Math.log(cos2 / cos1) / Math.log(tan2 / tan1);
        }
        if (c == 0.0 || !Double.isFinite(c)) {
            throw new IllegalArgumentException("COO projection is not defined for these parameters (c = " + c + ")");
        }
        return c;
    }

    private static double computeY0(final double sigma, final double delta, final double c) {
        return computePsi(sigma - delta, c) * Math.pow(WcsMath.tand((90.0 - sigma) / 2.0), c);
    }

    @Override
    protected double thetaFromR(final double r) {
        if (r == 0.0) {
            return (c < 0.0) ? -90.0 : 90.0;
        }
        return 90.0 - 2.0 * Math.toDegrees(Math.atan(Math.pow(r * invPsi, invC())));
    }

    @Override
    protected double rFromTheta(final double theta) {
        if (theta == -90.0) {
            return c > 0.0 ? Double.NaN : 0.0;
        }
        final double base = WcsMath.tand((90.0 - theta) / 2.0);
        if (base == 0.0 && c < 0.0) {
            return Double.NaN;
        }
        return psi * Math.pow(base, c);
    }

    @Override
    protected Projection createInverse() {
        return new ConicOrthomorphic(sigma, delta, opposite(getDirection()));
    }
}
