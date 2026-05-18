package edu.stsci.gwcs.transform.projection.pseudocylindrical;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Mollweide equal-area projection — FITS WCS CTYPE code {@code MOL}. The sky-to-pixel direction
 * solves {@code 2θ + sin 2θ = π sin δ} using bisection (no closed-form solution exists).
 * Follows wcslib {@code C/prj.c:molx2s} / {@code mols2x}.
 */
public class Mollweide extends Projection {

    private static final double SQRT2 = Math.sqrt(2.0);
    private static final int MAX_ITERATIONS = 100;
    private static final double ITERATION_TOLERANCE = 1e-13;

    public Mollweide(final Direction direction) {
        super(direction);
    }

    public Mollweide() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double gammaArg = y / (SQRT2 * R0);
        final double gammaDeg = WcsMath.asind(gammaArg);
        if (Double.isNaN(gammaDeg)) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double gamma = Math.toRadians(gammaDeg);
        final double sinTheta = (2.0 * gamma + Math.sin(2.0 * gamma)) / Math.PI;
        final double thetaDeg = WcsMath.asind(sinTheta);
        final double cosGamma = Math.cos(gamma);
        final double phiRad;
        if (Math.abs(cosGamma) < 1e-12) {
            phiRad = 0.0;
        } else {
            phiRad = Math.PI * x / (2.0 * SQRT2 * R0 * cosGamma);
        }
        output[outputOffset] = Math.toDegrees(phiRad);
        output[outputOffset + 1] = thetaDeg;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double phiRad = Math.toRadians(phi);

        double gamma;
        if (Math.abs(theta) == 90.0) {
            gamma = Math.copySign(Math.PI / 2.0, theta);
        } else if (theta == 0.0) {
            gamma = 0.0;
        } else {
            final double u = Math.PI * WcsMath.sind(theta);
            double v0 = -Math.PI;
            double v1 = Math.PI;
            double v = u;
            boolean converged = false;
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                final double resid = (v - u) + Math.sin(v);
                if (resid < 0.0) {
                    if (resid > -ITERATION_TOLERANCE) {
                        converged = true;
                        break;
                    }
                    v0 = v;
                } else {
                    if (resid < ITERATION_TOLERANCE) {
                        converged = true;
                        break;
                    }
                    v1 = v;
                }
                v = (v0 + v1) / 2.0;
            }
            if (!converged) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            gamma = v / 2.0;
        }

        output[outputOffset] = (2.0 * SQRT2 / Math.PI) * R0 * phiRad * Math.cos(gamma);
        output[outputOffset + 1] = SQRT2 * R0 * Math.sin(gamma);
    }

    @Override
    protected Projection createInverse() {
        return new Mollweide(opposite(getDirection()));
    }
}
