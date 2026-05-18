package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection;

/**
 * Plate carrée (equirectangular) projection — FITS WCS CTYPE code {@code CAR}.
 */
public class PlateCarree extends Projection {

    public PlateCarree(final Direction direction) {
        super(direction);
    }

    public PlateCarree() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        output[outputOffset] = x;
        output[outputOffset + 1] = y;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected Projection createInverse() {
        return new PlateCarree(opposite(getDirection()));
    }
}
