package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * The standard (axial) zenithal perspective projection — wcslib FITS CTYPE code {@code AZP}.
 * Use this class when the projection axis is purely radial about the reference point. For
 * projections where the line of sight is tilted off-axis (controlled by a {@code (phi0, theta0)}
 * pair in addition to {@code mu}), use {@link SlantZenithalPerspective} instead — CTYPE {@code SZP}.
 */
public class ZenithalPerspective extends ZenithalProjection {

    private final double mu;
    private final double gamma;
    private final double cosGamma;
    private final double sinGamma;
    private final double tanGamma;
    private final double r0MuPlus1;

    public ZenithalPerspective(final double mu, final double gamma, final Direction direction) {
        super(direction);
        if (!Double.isFinite(mu) || !Double.isFinite(gamma)) {
            throw new IllegalArgumentException(
                    "ZenithalPerspective requires finite mu and gamma, got mu=" + mu + ", gamma=" + gamma
            );
        }
        if (mu == -1.0) {
            throw new IllegalArgumentException("ZenithalPerspective is not defined for mu == -1");
        }
        this.cosGamma = WcsMath.cosd(gamma);
        if (cosGamma == 0.0) {
            throw new IllegalArgumentException("ZenithalPerspective is not defined for |gamma| == 90");
        }
        this.mu = mu;
        this.gamma = gamma;
        this.sinGamma = WcsMath.sind(gamma);
        this.tanGamma = sinGamma / cosGamma;
        this.r0MuPlus1 = R0 * (mu + 1.0);
    }

    public ZenithalPerspective(final double mu, final double gamma) {
        this(mu, gamma, Direction.PIX2SKY);
    }

    public ZenithalPerspective() {
        this(0.0, 0.0, Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double yc = y * cosGamma;
        final double r = Math.sqrt(x * x + yc * yc);

        if (r == 0.0) {
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = 90.0;
            return;
        }

        final double phi = WcsMath.atan2d(x, -yc);

        final double q = r0MuPlus1 + y * sinGamma;
        if (q == 0.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        final double rho = r / q;
        final double psi = WcsMath.atan2d(1.0, rho);
        final double tVal = rho * mu / Math.sqrt(rho * rho + 1.0);

        final double omega;
        if (Math.abs(tVal) > 1.0) {
            if (Math.abs(tVal) > 1.0 + 1e-13) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            omega = Math.copySign(90.0, tVal);
        } else {
            omega = WcsMath.asind(tVal);
        }

        double a = psi - omega;
        double b = psi + omega + 180.0;

        if (a > 90.0) a -= 360.0;
        if (b > 90.0) b -= 360.0;

        final double theta = Math.max(a, b);

        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double sinthe = WcsMath.sind(theta);
        final double costhe = WcsMath.cosd(theta);
        final double sinphi = WcsMath.sind(phi);
        final double cosphi = WcsMath.cosd(phi);

        final double s = tanGamma * cosphi;
        final double t = (mu + sinthe) + costhe * s;

        if (t == 0.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }

        final double r = r0MuPlus1 * costhe / t;

        output[outputOffset] = r * sinphi;
        output[outputOffset + 1] = -r * cosphi / cosGamma;
    }

    @Override
    protected Projection createInverse() {
        return new ZenithalPerspective(mu, gamma, opposite(getDirection()));
    }
}
