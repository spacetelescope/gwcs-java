package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Stereographic zenithal projection — FITS WCS CTYPE code {@code STG}.
 */
public class Stereographic extends ZenithalProjection {

    public Stereographic(final Direction direction) {
        super(direction);
    }

    public Stereographic() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected double thetaFromR(final double rTheta) {
        return 90.0 - 2.0 * Math.toDegrees(Math.atan(rTheta / (2.0 * R0)));
    }

    @Override
    protected double rFromTheta(final double theta) {
        final double denom = 1.0 + WcsMath.sind(theta);
        if (denom == 0.0) {
            return Double.NaN;
        }
        return 2.0 * R0 * WcsMath.cosd(theta) / denom;
    }

    @Override
    protected Projection createInverse() {
        return new Stereographic(opposite(getDirection()));
    }
}
