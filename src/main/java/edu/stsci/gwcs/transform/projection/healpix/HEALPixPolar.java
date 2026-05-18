package edu.stsci.gwcs.transform.projection.healpix;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * HEALPix polar (butterfly) projection — FITS WCS CTYPE code {@code XPH}.
 */
public class HEALPixPolar extends Projection {

    private static final double INV_SQRT2 = 1.0 / Math.sqrt(2.0);
    private static final double EQUATORIAL_BOUNDARY = 2.0 / 3.0;
    private static final double TRANSITION_TOLERANCE = 1e-4;
    private static final double SIGMA_TO_THETA_SCALE = Math.sqrt(EQUATORIAL_BOUNDARY) * (180.0 / Math.PI);
    private static final double SMALL_ANGLE_THRESHOLD = 90.0 - TRANSITION_TOLERANCE * SIGMA_TO_THETA_SCALE;
    private static final double THETA_TO_SIGMA_SCALE = Math.sqrt(1.5) * (Math.PI / 180.0);

    public HEALPixPolar(final Direction direction) {
        super(direction);
    }

    public HEALPixPolar() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double xr = x * INV_SQRT2;
        final double yr = y * INV_SQRT2;

        final double xi1;
        final double eta1;
        final double phiBase;

        if (xr <= 0.0 && 0.0 < yr) {
            xi1 = -xr - yr;
            eta1 = xr - yr;
            phiBase = -180.0;
        } else if (xr < 0.0 && yr <= 0.0) {
            xi1 = xr - yr;
            eta1 = xr + yr;
            phiBase = -90.0;
        } else if (0.0 <= xr && yr < 0.0) {
            xi1 = xr + yr;
            eta1 = -xr + yr;
            phiBase = 0.0;
        } else {
            xi1 = -xr + yr;
            eta1 = -xr - yr;
            phiBase = 90.0;
        }

        final double xi = xi1 + 45.0;
        final double eta = eta1 + 90.0;
        final double abseta = Math.abs(eta);

        if (abseta > 90.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }

        if (abseta <= 45.0) {
            output[outputOffset] = phiBase + xi;
            output[outputOffset + 1] = WcsMath.asind(eta / 67.5);
        } else {
            final double sigma = (90.0 - abseta) / 45.0;

            final double phi;
            if (xr == 0.0) {
                if (yr <= 0.0) {
                    phi = 0.0;
                } else {
                    phi = 180.0;
                }
            } else if (yr == 0.0) {
                if (xr < 0.0) {
                    phi = -90.0;
                } else {
                    phi = 90.0;
                }
            } else if (sigma == 0.0) {
                // Pole singularity with neither xr nor yr at zero: xi1 / sigma diverges.
                // Match wcslib by returning NaN longitude alongside the pole latitude.
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = eta < 0.0 ? -90.0 : 90.0;
                return;
            } else {
                phi = phiBase + 45.0 + xi1 / sigma;
            }

            double theta;
            if (sigma < TRANSITION_TOLERANCE) {
                theta = 90.0 - sigma * SIGMA_TO_THETA_SCALE;
            } else {
                theta = WcsMath.asind(1.0 - sigma * sigma / 3.0);
            }
            if (eta < 0.0) theta = -theta;

            output[outputOffset] = phi;
            output[outputOffset + 1] = theta;
        }
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        double chi = phi;
        if (180.0 <= Math.abs(chi)) {
            chi = chi % 360.0;
            if (chi < -180.0) {
                chi += 360.0;
            } else if (180.0 <= chi) {
                chi -= 360.0;
            }
        }

        chi += 180.0;
        final double psi = chi % 90.0;

        final double roundedPhi = chi - 180.0;

        final double sinthe = WcsMath.sind(theta);
        final double absSin = Math.abs(sinthe);

        double xi;
        double eta;
        if (absSin <= EQUATORIAL_BOUNDARY) {
            xi = psi;
            eta = 67.5 * sinthe;
        } else {
            final double sigma;
            final double absTheta = Math.abs(theta);
            if (absTheta < SMALL_ANGLE_THRESHOLD) {
                sigma = Math.sqrt(3.0 * (1.0 - absSin));
            } else {
                sigma = (90.0 - absTheta) * THETA_TO_SIGMA_SCALE;
            }

            xi = 45.0 + (psi - 45.0) * sigma;
            eta = 45.0 * (2.0 - sigma);
            if (theta < 0.0) eta = -eta;
        }

        xi -= 45.0;
        eta -= 90.0;

        final double xResult;
        final double yResult;
        if (roundedPhi < -90.0) {
            xResult = INV_SQRT2 * (-xi + eta);
            yResult = INV_SQRT2 * (-xi - eta);
        } else if (roundedPhi < 0.0) {
            xResult = INV_SQRT2 * (xi + eta);
            yResult = INV_SQRT2 * (-xi + eta);
        } else if (roundedPhi < 90.0) {
            xResult = INV_SQRT2 * (xi - eta);
            yResult = INV_SQRT2 * (xi + eta);
        } else {
            xResult = INV_SQRT2 * (-xi - eta);
            yResult = INV_SQRT2 * (xi - eta);
        }

        output[outputOffset] = xResult;
        output[outputOffset + 1] = yResult;
    }

    @Override
    protected Projection createInverse() {
        return new HEALPixPolar(opposite(getDirection()));
    }
}
