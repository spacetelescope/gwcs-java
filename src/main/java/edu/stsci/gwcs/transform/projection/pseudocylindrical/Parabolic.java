package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Parabolic (Craster) projection — FITS WCS CTYPE code {@code PAR}.
 */
public class Parabolic extends Projection {

    public Parabolic(final Direction direction) {
        super(direction);
    }

    public Parabolic() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double asinArg = y / 180.0;
        if (asinArg < -1.0 || asinArg > 1.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double theta = 3.0 * WcsMath.asind(asinArg);
        final double denom = 2.0 * WcsMath.cosd(2.0 * theta / 3.0) - 1.0;
        if (denom == 0.0) {
            if (x == 0.0) {
                output[outputOffset] = 0.0;
                output[outputOffset + 1] = theta;
                return;
            }
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double phi = x / denom;
        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        output[outputOffset] = phi * (2.0 * WcsMath.cosd(2.0 * theta / 3.0) - 1.0);
        output[outputOffset + 1] = 180.0 * WcsMath.sind(theta / 3.0);
    }

    @Override
    protected Projection createInverse() {
        return new Parabolic(opposite(getDirection()));
    }
}
