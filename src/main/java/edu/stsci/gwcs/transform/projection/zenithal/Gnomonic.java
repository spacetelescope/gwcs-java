package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Gnomonic (tangent plane) projection — FITS WCS CTYPE code {@code TAN}.
 */
public class Gnomonic extends ZenithalProjection {

    public Gnomonic(final Direction direction) {
        super(direction);
    }

    public Gnomonic() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected double thetaFromR(final double rTheta) {
        return Math.toDegrees(Math.atan(R0 / rTheta));
    }

    @Override
    protected double rFromTheta(final double theta) {
        final double sinTheta = WcsMath.sind(theta);
        if (sinTheta <= 0.0) {
            return Double.NaN;
        }
        return R0 * WcsMath.cosd(theta) / sinTheta;
    }

    @Override
    protected Projection createInverse() {
        return new Gnomonic(opposite(getDirection()));
    }
}
