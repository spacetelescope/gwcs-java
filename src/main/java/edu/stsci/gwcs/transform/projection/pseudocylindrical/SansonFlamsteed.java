package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Sanson-Flamsteed (sinusoidal) projection — FITS WCS CTYPE code {@code SFL}.
 */
public class SansonFlamsteed extends Projection {

    public SansonFlamsteed(final Direction direction) {
        super(direction);
    }

    public SansonFlamsteed() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double cosY = WcsMath.cosd(y);
        final double phi;
        if (cosY == 0.0) {
            // At the pole, only x == 0 corresponds to a valid sky point.
            // Match wcslib sflx2s PRJERR_BAD_PIX semantics for any nonzero x.
            if (x != 0.0) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            phi = 0.0;
        } else {
            phi = x / cosY;
        }
        output[outputOffset] = phi;
        output[outputOffset + 1] = y;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        output[outputOffset] = phi * WcsMath.cosd(theta);
        output[outputOffset + 1] = theta;
    }

    @Override
    protected Projection createInverse() {
        return new SansonFlamsteed(opposite(getDirection()));
    }
}
