package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;

/**
 * Zenithal equidistant projection — FITS WCS CTYPE code {@code ARC}.
 */
public class ZenithalEquidistant extends ZenithalProjection {

    public ZenithalEquidistant(final Direction direction) {
        super(direction);
    }

    public ZenithalEquidistant() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected double thetaFromR(final double rTheta) {
        // Out-of-disk inputs (|rTheta| > 180) yield theta outside [-90, 90].
        if (rTheta > 180.0) {
            return Double.NaN;
        }
        return 90.0 - rTheta;
    }

    @Override
    protected double rFromTheta(final double theta) {
        return 90.0 - theta;
    }

    @Override
    protected Projection createInverse() {
        return new ZenithalEquidistant(opposite(getDirection()));
    }
}
