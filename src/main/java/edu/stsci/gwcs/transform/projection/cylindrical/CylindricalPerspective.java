package edu.stsci.gwcs.transform.projection.cylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Cylindrical perspective projection — FITS WCS CTYPE code {@code CYP}.
 */
public class CylindricalPerspective extends Projection {

    private final double mu;
    private final double lambda;
    private final double r0MuPlusLambda;

    public CylindricalPerspective(final double mu, final double lambda, final Direction direction) {
        super(direction);
        if (!Double.isFinite(mu) || !Double.isFinite(lambda)) {
            throw new IllegalArgumentException(
                    "CylindricalPerspective requires finite mu and lambda, got mu=" + mu + ", lambda=" + lambda
            );
        }
        if (lambda == 0.0) {
            throw new IllegalArgumentException("CylindricalPerspective requires lambda != 0");
        }
        if (mu + lambda == 0.0) {
            throw new IllegalArgumentException("CYP projection is not defined for mu = -lambda");
        }
        this.mu = mu;
        this.lambda = lambda;
        this.r0MuPlusLambda = R0 * (mu + lambda);
    }

    public CylindricalPerspective(final double mu, final double lambda) {
        this(mu, lambda, Direction.PIX2SKY);
    }

    public CylindricalPerspective() {
        this(1.0, 1.0, Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double phi = x / lambda;
        final double eta = y / r0MuPlusLambda;
        final double theta = WcsMath.atan2d(eta, 1.0)
                + WcsMath.asind(eta * mu / Math.sqrt(eta * eta + 1.0));
        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double x = lambda * phi;
        final double cosTheta = WcsMath.cosd(theta);
        final double denom = mu + cosTheta;
        if (denom == 0.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double y = r0MuPlusLambda / denom * WcsMath.sind(theta);
        output[outputOffset] = x;
        output[outputOffset + 1] = y;
    }

    @Override
    protected Projection createInverse() {
        return new CylindricalPerspective(mu, lambda, opposite(getDirection()));
    }
}
