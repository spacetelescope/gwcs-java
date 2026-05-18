package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Hammer-Aitoff projection — FITS WCS CTYPE code {@code AIT}.
 */
public class HammerAitoff extends Projection {

    public HammerAitoff(final Direction direction) {
        super(direction);
    }

    public HammerAitoff() {
        this(Direction.PIX2SKY);
    }

    private static final double LEMON_TOLERANCE = 1.0e-13;

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        double zSquared = 1.0 - (x / (4.0 * R0)) * (x / (4.0 * R0)) - (y / (2.0 * R0)) * (y / (2.0 * R0));
        if (zSquared < 0.5) {
            if (zSquared < 0.5 - LEMON_TOLERANCE) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            zSquared = 0.5;
        }
        final double z = Math.sqrt(zSquared);
        final double phiDeg = 2.0 * WcsMath.atan2d(z * x / (2.0 * R0), 2.0 * zSquared - 1.0);
        double sinTheta = y * z / R0;
        if (sinTheta > 1.0) sinTheta = 1.0;
        else if (sinTheta < -1.0) sinTheta = -1.0;
        output[outputOffset] = phiDeg;
        output[outputOffset + 1] = WcsMath.asind(sinTheta);
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double cosTheta = WcsMath.cosd(theta);
        final double gamma = R0 * Math.sqrt(2.0 / (1.0 + cosTheta * WcsMath.cosd(phi / 2.0)));
        output[outputOffset] = 2.0 * gamma * cosTheta * WcsMath.sind(phi / 2.0);
        output[outputOffset + 1] = gamma * WcsMath.sind(theta);
    }

    @Override
    protected Projection createInverse() {
        return new HammerAitoff(opposite(getDirection()));
    }
}
