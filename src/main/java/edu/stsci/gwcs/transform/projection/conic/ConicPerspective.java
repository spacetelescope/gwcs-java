package edu.stsci.gwcs.transform.projection.conic;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Conic perspective projection — FITS WCS CTYPE code {@code COP}.
 */
public class ConicPerspective extends ConicProjection {

    private final double r0CosDelta;
    private final double invR0CosDelta;
    private final double cotSigma;

    public ConicPerspective(final double sigma, final double delta, final Direction direction) {
        super(sigma, delta, WcsMath.sind(checkFinite(sigma, delta)), computeY0(sigma, delta), direction);

        r0CosDelta = R0 * WcsMath.cosd(delta);
        if (r0CosDelta == 0.0) {
            throw new IllegalArgumentException(
                    "ConicPerspective undefined for |delta| == 90 degrees, got " + delta
            );
        }
        invR0CosDelta = 1.0 / r0CosDelta;
        cotSigma = 1.0 / WcsMath.tand(sigma);
    }

    public ConicPerspective(final double sigma, final double delta) {
        this(sigma, delta, Direction.PIX2SKY);
    }

    private static double checkFinite(final double sigma, final double delta) {
        if (!Double.isFinite(sigma) || !Double.isFinite(delta)) {
            throw new IllegalArgumentException(
                    "ConicPerspective requires finite sigma and delta, got sigma=" + sigma + ", delta=" + delta
            );
        }
        return sigma;
    }

    private static double computeY0(final double sigma, final double delta) {
        final double r0CosDelta = R0 * WcsMath.cosd(delta);
        final double cotSigma = 1.0 / WcsMath.tand(sigma);
        return r0CosDelta * cotSigma;
    }

    @Override
    protected double thetaFromR(final double r) {
        return sigma + Math.toDegrees(Math.atan(cotSigma - r * invR0CosDelta));
    }

    @Override
    protected double rFromTheta(final double theta) {
        if (Math.abs(theta) == 90.0) {
            if ((theta < 0.0) != (sigma < 0.0)) {
                return Double.NaN;
            }
            return 0.0;
        }
        final double t = theta - sigma;
        final double s = WcsMath.cosd(t);
        if (s == 0.0) {
            return Double.NaN;
        }
        return y0 - r0CosDelta * WcsMath.sind(t) / s;
    }

    @Override
    protected Projection createInverse() {
        return new ConicPerspective(sigma, delta, opposite(getDirection()));
    }
}
