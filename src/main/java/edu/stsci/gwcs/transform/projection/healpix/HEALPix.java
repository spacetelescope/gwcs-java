package edu.stsci.gwcs.transform.projection.healpix;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * HEALPix projection — FITS WCS CTYPE code {@code HPX}.
 */
public class HEALPix extends Projection {

    private final double h;
    private final double k;
    private final int hOdd;
    private final int kOdd;
    private final double equatorialLimit;
    private final double latScale;
    private final double halfKPlus1;
    private final double polarBoundary;
    private final double facetWidth;
    private final double facetsPerDegree;
    private final double slim;

    private static final double SLIM_TOLERANCE = 1.0e-12;
    // Large reciprocal used at the pole (sigma=0) so that any off-center point
    // exceeds slim and is correctly rejected as out-of-domain.
    private static final double POLE_RECIP_SIGMA = 1e9;

    public HEALPix(final double h, final double k, final Direction direction) {
        super(direction);
        if (!Double.isFinite(h) || !Double.isFinite(k)) {
            throw new IllegalArgumentException("HEALPix projection requires finite h and k, got h=" + h + ", k=" + k);
        }
        if (h <= 0.0) {
            throw new IllegalArgumentException("HEALPix projection h must be > 0, got " + h);
        }
        if (k <= 0.0) {
            throw new IllegalArgumentException("HEALPix projection k must be > 0, got " + k);
        }
        this.h = h;
        this.k = k;
        this.hOdd = ((int) (h + 0.5)) % 2;
        this.kOdd = ((int) (k + 0.5)) % 2;
        this.equatorialLimit = (k - 1.0) / k;
        this.latScale = 90.0 * k / h;
        this.halfKPlus1 = (k + 1.0) / 2.0;
        this.polarBoundary = 90.0 * (k - 1.0) / h;
        this.facetWidth = 180.0 / h;
        this.facetsPerDegree = h / 360.0;
        this.slim = facetWidth + SLIM_TOLERANCE;
    }

    public HEALPix(final double h, final double k) {
        this(h, k, Direction.PIX2SKY);
    }

    public HEALPix() {
        this(4.0, 3.0, Direction.PIX2SKY);
    }

    private double facetCenter(final double val) {
        return -180.0 + (2.0 * Math.floor((val + 180.0) * facetsPerDegree) + 1.0) * facetWidth;
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double absY = Math.abs(y);

        if (absY <= polarBoundary) {
            output[outputOffset] = x;
            output[outputOffset + 1] = WcsMath.asind(y / latScale);
            return;
        }

        final double ylim = facetWidth * halfKPlus1;
        if (absY > ylim) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }

        final int offset = (kOdd != 0 || y > 0.0) ? 0 : 1;

        final double sigma = halfKPlus1 - absY / facetWidth;

        double recipSigma;
        double theta;
        if (sigma == 0.0) {
            recipSigma = POLE_RECIP_SIGMA;
            theta = 90.0;
        } else {
            final double tVal = 1.0 - sigma * sigma / k;
            if (tVal < -1.0) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            recipSigma = 1.0 / sigma;
            theta = WcsMath.asind(tVal);
        }
        if (y < 0.0) theta = -theta;

        final double xc = facetCenter(x);
        double xMinusXc = x - xc;

        if (offset != 0) {
            final int hInt = (int) Math.floor(x / facetWidth) + hOdd;
            if (hInt % 2 != 0) {
                xMinusXc -= facetWidth;
            } else {
                xMinusXc += facetWidth;
            }
        }

        final double r = recipSigma * xMinusXc;
        if (Math.abs(r) >= slim) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double phi;
        if (r != 0.0) {
            phi = x + r - xMinusXc;
        } else {
            phi = x;
        }

        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double sinthe = WcsMath.sind(theta);
        final double absSin = Math.abs(sinthe);

        if (absSin <= equatorialLimit) {
            output[outputOffset] = phi;
            output[outputOffset + 1] = latScale * sinthe;
            return;
        }

        final int offset = (kOdd != 0 || theta > 0.0) ? 0 : 1;

        final double sigma = Math.sqrt(k * (1.0 - absSin));
        final double xi = sigma - 1.0;

        double eta = facetWidth * (halfKPlus1 - sigma);
        if (theta < 0.0) eta = -eta;

        final double phic = facetCenter(phi);
        double phiMinusPhiC = phi - phic;

        if (offset != 0) {
            final int hInt = (int) Math.floor(phi / facetWidth) + hOdd;
            if (hInt % 2 != 0) {
                phiMinusPhiC -= facetWidth;
            } else {
                phiMinusPhiC += facetWidth;
            }
        }

        double x = phi + phiMinusPhiC * xi;

        // Reflect x back into [-180, 180] to match wcslib hpxs2x longitude wrapping
        if (x > 180.0) {
            x = 360.0 - x;
        }

        output[outputOffset] = x;
        output[outputOffset + 1] = eta;
    }

    @Override
    protected Projection createInverse() {
        return new HEALPix(h, k, opposite(getDirection()));
    }
}
