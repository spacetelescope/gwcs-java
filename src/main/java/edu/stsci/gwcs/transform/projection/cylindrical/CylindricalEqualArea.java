package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Cylindrical equal-area projection — FITS WCS CTYPE code {@code CEA}.
 */
public class CylindricalEqualArea extends Projection {

    private final double lambda;

    public CylindricalEqualArea(final double lambda, final Direction direction) {
        super(direction);
        if (lambda <= 0.0 || lambda > 1.0) {
            throw new IllegalArgumentException("CEA projection requires 0 < lambda <= 1, got " + lambda);
        }
        this.lambda = lambda;
    }

    public CylindricalEqualArea(final double lambda) {
        this(lambda, Direction.PIX2SKY);
    }

    public CylindricalEqualArea() {
        this(1.0, Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double phi = x;
        final double asinArg = y * lambda / R0;
        final double theta = WcsMath.asind(asinArg);
        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double x = phi;
        final double y = R0 * WcsMath.sind(theta) / lambda;
        output[outputOffset] = x;
        output[outputOffset + 1] = y;
    }

    @Override
    protected Projection createInverse() {
        return new CylindricalEqualArea(lambda, opposite(getDirection()));
    }
}
