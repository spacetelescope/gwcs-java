package edu.stsci.gwcs.transform.projection.pseudoconic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Bonne equal-area pseudo-conic projection — FITS WCS CTYPE code {@code BON}.
 */
public class BonneEqualArea extends Projection {

    private final double theta1;
    private final double y0;

    public BonneEqualArea(final double theta1, final Direction direction) {
        super(direction);
        if (!Double.isFinite(theta1)) {
            throw new IllegalArgumentException("BonneEqualArea theta1 must be finite, got " + theta1);
        }
        if (theta1 == 0.0) {
            throw new IllegalArgumentException("theta1 must not be 0 (use SansonFlamsteed for theta1=0)");
        }
        this.theta1 = theta1;
        this.y0 = R0 * WcsMath.cosd(theta1) / WcsMath.sind(theta1) + theta1;
    }

    public BonneEqualArea(final double theta1) {
        this(theta1, Direction.PIX2SKY);
    }

    public BonneEqualArea() {
        this(45.0);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        double dy = y0 - y;
        double rTheta = Math.sqrt(x * x + dy * dy);
        if (theta1 < 0.0) {
            rTheta = -rTheta;
        }

        double alpha;
        if (rTheta == 0.0) {
            alpha = 0.0;
        } else {
            alpha = WcsMath.atan2d(x / rTheta, dy / rTheta);
        }

        double theta = y0 - rTheta;
        if (theta < -90.0 || theta > 90.0) {
            // Match wcslib bonx2s PRJERR_BAD_PIX semantics: out-of-disk -> NaN.
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        double cosTheta = WcsMath.cosd(theta);
        double phi;
        if (cosTheta == 0.0) {
            phi = 0.0;
        } else {
            phi = alpha * (rTheta / R0) / cosTheta;
        }

        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        double rTheta = y0 - theta;
        if (rTheta == 0.0) {
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = y0;
            return;
        }
        double s = WcsMath.cosd(theta) / rTheta;
        double alpha = R0 * phi * s;
        double sinAlpha = WcsMath.sind(alpha);
        double cosAlpha = WcsMath.cosd(alpha);
        double x = rTheta * sinAlpha;
        double y = -rTheta * cosAlpha + y0;

        output[outputOffset] = x;
        output[outputOffset + 1] = y;
    }

    @Override
    protected Projection createInverse() {
        return new BonneEqualArea(theta1, opposite(getDirection()));
    }
}
