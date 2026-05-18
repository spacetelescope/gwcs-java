package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Zenithal equal-area projection — FITS WCS CTYPE code {@code ZEA}.
 */
public class ZenithalEqualArea extends ZenithalProjection {

    public ZenithalEqualArea(final Direction direction) {
        super(direction);
    }

    public ZenithalEqualArea() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected double thetaFromR(final double rTheta) {
        if (rTheta > 2.0 * R0) {
            if (rTheta - 2.0 * R0 < 1.0e-12) {
                return -90.0;
            }
            return Double.NaN;
        }
        double arg = rTheta / (2.0 * R0);
        if (arg > 1.0) arg = 1.0;
        return 90.0 - 2.0 * WcsMath.asind(arg);
    }

    @Override
    protected double rFromTheta(final double theta) {
        return 2.0 * R0 * WcsMath.sind((90.0 - theta) / 2.0);
    }

    @Override
    protected Projection createInverse() {
        return new ZenithalEqualArea(opposite(getDirection()));
    }
}
