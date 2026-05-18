package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Mercator projection — FITS WCS CTYPE code {@code MER}.
 */
public class Mercator extends Projection {

    public Mercator(final Direction direction) {
        super(direction);
    }

    public Mercator() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double phi = x;
        final double theta = Math.toDegrees(2.0 * Math.atan(Math.exp(Math.toRadians(y)))) - 90.0;
        if (Math.abs(theta) >= 90.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double x = phi;
        if (Math.abs(theta) >= 90.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double y = R0 * Math.log(WcsMath.tand((90.0 + theta) / 2.0));
        output[outputOffset] = x;
        output[outputOffset + 1] = y;
    }

    @Override
    protected Projection createInverse() {
        return new Mercator(opposite(getDirection()));
    }
}
