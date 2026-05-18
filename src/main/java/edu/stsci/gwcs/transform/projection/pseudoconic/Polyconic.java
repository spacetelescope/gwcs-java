package edu.stsci.gwcs.transform.projection.pseudoconic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Polyconic projection (CTYPE {@code PCO}). The pix-to-sky direction has no closed-form solution
 * and is solved iteratively following wcslib {@code C/prj.c:pcox2s}.
 */
public class Polyconic extends Projection {

    private static final double CONVERGENCE_TOL = 1.0e-12;
    private static final int MAX_ITERATIONS = 64;
    private static final double TWO_R0 = 2.0 * R0;
    private static final double SMALL_ANGLE_COEFF = Math.toRadians(1.0) / TWO_R0;

    public Polyconic(final Direction direction) {
        super(direction);
    }

    public Polyconic() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double w = Math.abs(y);

        if (w < CONVERGENCE_TOL) {
            output[outputOffset] = x;
            output[outputOffset + 1] = 0.0;
            return;
        }

        if (Math.abs(w - 90.0) < CONVERGENCE_TOL) {
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = Math.copySign(90.0, y);
            return;
        }

        if (x == 0.0) {
            if (w > 90.0) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = y;
            return;
        }

        if (w > 90.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }

        double the;
        double ymthe;
        double tanthe;

        if (w < 1.0e-4) {
            // x-y coupling matches wcslib pcox2s small-angle approximation
            the = y / (1.0 + SMALL_ANGLE_COEFF * x * x);
            ymthe = y - the;
            tanthe = WcsMath.tand(the);
        } else {
            double thepos = y;
            double theneg = 0.0;

            final double xx = x * x;
            if (xx == 0.0) {
                output[outputOffset] = 0.0;
                output[outputOffset + 1] = (Math.abs(y) <= 90.0) ? y : Double.NaN;
                return;
            }
            double fpos = xx;
            double fneg = -xx;

            boolean converged = false;
            the = thepos;
            for (int k = 0; k < MAX_ITERATIONS; k++) {
                double lambda = fpos / (fpos - fneg);
                if (lambda < 0.1) {
                    lambda = 0.1;
                } else if (lambda > 0.9) {
                    lambda = 0.9;
                }
                the = thepos - lambda * (thepos - theneg);

                ymthe = y - the;
                tanthe = WcsMath.tand(the);
                final double f = xx + ymthe * (ymthe - TWO_R0 / tanthe);

                if (Math.abs(f) < CONVERGENCE_TOL) { converged = true; break; }
                if (Math.abs(thepos - theneg) < CONVERGENCE_TOL) { converged = true; break; }

                if (f > 0.0) {
                    thepos = the;
                    fpos = f;
                } else {
                    theneg = the;
                    fneg = f;
                }
            }

            if (!converged) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }

            ymthe = y - the;
            tanthe = WcsMath.tand(the);
        }

        final double x1 = R0 - ymthe * tanthe;
        final double y1 = x * tanthe;
        final double phi;
        final double sinthe = WcsMath.sind(the);
        if (Math.abs(sinthe) < CONVERGENCE_TOL) {
            phi = x;
        } else if (x1 == 0.0 && y1 == 0.0) {
            phi = 0.0;
        } else {
            phi = WcsMath.atan2d(y1, x1) / sinthe;
        }

        output[outputOffset] = phi;
        output[outputOffset + 1] = the;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        if (theta == 0.0) {
            output[outputOffset] = phi;
            output[outputOffset + 1] = 0.0;
            return;
        }

        if (Math.abs(theta) < 1.0e-4) {
            final double x = phi * WcsMath.cosd(theta);
            final double y = (1.0 + SMALL_ANGLE_COEFF * x * x) * theta;
            output[outputOffset] = x;
            output[outputOffset + 1] = y;
            return;
        }

        final double sinthe = WcsMath.sind(theta);
        final double costhe = WcsMath.cosd(theta);
        final double cotthe = costhe / sinthe;

        final double psi = phi * sinthe;
        final double sinpsi = WcsMath.sind(psi);
        final double cospsi = WcsMath.cosd(psi);

        final double x = R0 * cotthe * sinpsi;
        final double y = R0 * (cotthe * (1.0 - cospsi) + Math.toRadians(theta));

        output[outputOffset] = x;
        output[outputOffset + 1] = y;
    }

    @Override
    protected Projection createInverse() {
        return new Polyconic(opposite(getDirection()));
    }
}
