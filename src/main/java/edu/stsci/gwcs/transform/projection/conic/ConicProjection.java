package edu.stsci.gwcs.transform.projection.conic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

public abstract class ConicProjection extends Projection {
    protected final double sigma;
    protected final double delta;
    protected final double c;
    private final double invC;
    protected final double y0;

    protected ConicProjection(final double sigma, final double delta, final double c, final double y0, final Direction direction) {
        super(direction);
        if (c == 0.0) {
            throw new IllegalArgumentException("Conic projection is not defined for c == 0 (sigma=" + sigma + ", delta=" + delta + ")");
        }
        this.sigma = sigma;
        this.delta = delta;
        this.c = c;
        this.invC = 1.0 / c;
        this.y0 = y0;
    }

    protected final double invC() {
        return invC;
    }

    @Override
    public double getPhi0() {
        return 0.0;
    }

    @Override
    public double getTheta0() {
        return sigma;
    }

    protected abstract double thetaFromR(double r);

    protected abstract double rFromTheta(double theta);

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double dy = y0 - y;
        double r = Math.sqrt(x * x + dy * dy);
        if (sigma < 0.0) r = -r;

        double alpha;
        if (r == 0.0) {
            alpha = 0.0;
        } else {
            alpha = WcsMath.atan2d(x / r, dy / r);
        }

        final double phi = alpha * invC;
        final double theta = thetaFromR(r);

        if (Double.isNaN(theta)) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
        } else {
            output[outputOffset] = phi;
            output[outputOffset + 1] = theta;
        }
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double alpha = c * phi;
        final double r = rFromTheta(theta);

        if (Double.isNaN(r)) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
        } else {
            output[outputOffset] = r * WcsMath.sind(alpha);
            output[outputOffset + 1] = -r * WcsMath.cosd(alpha) + y0;
        }
    }
}
