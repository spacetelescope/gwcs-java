package edu.stsci.gwcs.transform.projection.conic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Conic equal-area projection — FITS WCS CTYPE code {@code COE}.
 */
public class ConicEqualArea extends ConicProjection {

    private final double r0OverC;
    private final double sinProduct;
    private final double twoC;
    private final double r0OverCSqTimesSinProduct;
    private final double invAsinScale;

    public ConicEqualArea(final double sigma, final double delta, final Direction direction) {
        this(sigma, delta, computeC(sigma, delta), direction);
    }

    private ConicEqualArea(final double sigma, final double delta, final double c, final Direction direction) {
        super(sigma, delta, c, computeY0(sigma, delta, c), direction);

        final double theta1 = sigma - delta;
        final double theta2 = sigma + delta;

        r0OverC = R0 / c;
        sinProduct = 1.0 + WcsMath.sind(theta1) * WcsMath.sind(theta2);
        twoC = 2.0 * c;
        r0OverCSqTimesSinProduct = r0OverC * r0OverC * sinProduct;
        invAsinScale = 1.0 / (2.0 * R0 * r0OverC);
    }

    public ConicEqualArea(final double sigma, final double delta) {
        this(sigma, delta, Direction.PIX2SKY);
    }

    private static double computeC(final double sigma, final double delta) {
        return (WcsMath.sind(sigma - delta) + WcsMath.sind(sigma + delta)) / 2.0;
    }

    private static double computeY0(final double sigma, final double delta, final double c) {
        final double r0OverC = R0 / c;
        final double sinSigma = WcsMath.sind(sigma);
        final double sinDelta = WcsMath.sind(delta);
        final double sinProduct = 1.0 + (sinSigma - sinDelta) * (sinSigma + sinDelta);
        return r0OverC * Math.sqrt(sinProduct - 2.0 * c * sinSigma);
    }

    @Override
    protected double thetaFromR(final double r) {
        final double w = (r0OverCSqTimesSinProduct - r * r) * invAsinScale;
        if (w > 1.0) {
            return (w - 1.0 < 1e-12) ? 90.0 : Double.NaN;
        }
        if (w < -1.0) {
            return (w + 1.0 > -1e-12) ? -90.0 : Double.NaN;
        }
        return WcsMath.asind(w);
    }

    @Override
    protected double rFromTheta(final double theta) {
        return r0OverC * Math.sqrt(sinProduct - twoC * WcsMath.sind(theta));
    }

    @Override
    protected Projection createInverse() {
        return new ConicEqualArea(sigma, delta, opposite(getDirection()));
    }
}
