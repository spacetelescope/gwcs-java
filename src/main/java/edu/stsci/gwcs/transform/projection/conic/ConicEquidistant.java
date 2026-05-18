package edu.stsci.gwcs.transform.projection.conic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Conic equidistant projection — FITS WCS CTYPE code {@code COD}.
 */
public class ConicEquidistant extends ConicProjection {

    private final double y0PlusSigma;

    public ConicEquidistant(final double sigma, final double delta, final Direction direction) {
        this(sigma, delta, computeC(sigma, delta), direction);
    }

    private ConicEquidistant(final double sigma, final double delta, final double c, final Direction direction) {
        super(sigma, delta, c, computeY0(sigma, delta, c), direction);
        y0PlusSigma = y0 + sigma;
    }

    public ConicEquidistant(final double sigma, final double delta) {
        this(sigma, delta, Direction.PIX2SKY);
    }

    private static double computeC(final double sigma, final double delta) {
        final double sinSigma = WcsMath.sind(sigma);
        if (delta == 0.0) {
            return R0 * sinSigma * Math.toRadians(1.0);
        } else {
            return R0 * sinSigma * WcsMath.sind(delta) / delta;
        }
    }

    private static double computeY0(final double sigma, final double delta, final double c) {
        return R0 * WcsMath.cosd(delta) * WcsMath.cosd(sigma) / c;
    }

    @Override
    protected double thetaFromR(final double r) {
        return y0PlusSigma - r;
    }

    @Override
    protected double rFromTheta(final double theta) {
        return y0PlusSigma - theta;
    }

    @Override
    protected Projection createInverse() {
        return new ConicEquidistant(sigma, delta, opposite(getDirection()));
    }
}
