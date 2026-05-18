package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Slant (off-axis) zenithal perspective projection — wcslib FITS CTYPE code {@code SZP}.
 * Compared to {@link ZenithalPerspective} ({@code AZP}), this projection's line of sight is
 * tilted away from the radial direction; the tilt is controlled by the {@code (phi0, theta0)}
 * pair in addition to the projection distance {@code mu}. Use this class when CTYPE is
 * {@code SZP}; use {@link ZenithalPerspective} when CTYPE is {@code AZP}.
 */
public class SlantZenithalPerspective extends ZenithalProjection {

    private final double mu;
    private final double phi0;
    private final double theta0;

    private final double negMuCosThSinPh;
    private final double muCosThCosPh;
    private final double muSinThPlus1;
    private final double r0NegMuCosThSinPh;
    private final double r0MuCosThCosPh;
    private final double r0MuSinThPlus1;

    public SlantZenithalPerspective(final double mu, final double phi0, final double theta0, final Direction direction) {
        super(direction);
        if (!Double.isFinite(mu) || !Double.isFinite(phi0) || !Double.isFinite(theta0)) {
            throw new IllegalArgumentException(
                    "SlantZenithalPerspective requires finite mu, phi0, theta0, got mu=" + mu
                            + ", phi0=" + phi0 + ", theta0=" + theta0
            );
        }
        this.mu = mu;
        this.phi0 = phi0;
        this.theta0 = theta0;

        final double cosTheta0 = WcsMath.cosd(theta0);
        final double sinTheta0 = WcsMath.sind(theta0);
        final double sinPhi0 = WcsMath.sind(phi0);
        final double cosPhi0 = WcsMath.cosd(phi0);

        muSinThPlus1 = mu * sinTheta0 + 1.0;
        if (muSinThPlus1 == 0.0) {
            throw new IllegalArgumentException("SlantZenithalPerspective is not defined for mu*sin(theta0) == -1");
        }

        negMuCosThSinPh = -mu * cosTheta0 * sinPhi0;
        muCosThCosPh = mu * cosTheta0 * cosPhi0;
        r0NegMuCosThSinPh = R0 * negMuCosThSinPh;
        r0MuCosThCosPh = R0 * muCosThCosPh;
        r0MuSinThPlus1 = R0 * muSinThPlus1;
    }

    public SlantZenithalPerspective(final double mu, final double phi0, final double theta0) {
        this(mu, phi0, theta0, Direction.PIX2SKY);
    }

    public SlantZenithalPerspective() {
        this(0.0, 0.0, 90.0, Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double xr = x / R0;
        final double yr = y / R0;
        final double r2 = xr * xr + yr * yr;

        if (r2 == 0.0) {
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = 90.0;
            return;
        }

        final double x1 = (xr - negMuCosThSinPh) / muSinThPlus1;
        final double y1 = (yr - muCosThCosPh) / muSinThPlus1;
        final double xy = xr * x1 + yr * y1;

        final double theta;
        final double z;
        if (r2 < 1.0e-10) {
            z = r2 / 2.0;
            theta = 90.0 - Math.toDegrees(Math.sqrt(r2 / (1.0 + xy)));
        } else {
            final double t = x1 * x1 + y1 * y1;
            final double a = t + 1.0;
            final double b = xy - t;
            final double c = r2 - 2.0 * xy + t - 1.0;

            final double sinthe = solveQuadraticSinTheta(a, b, c);
            if (Double.isNaN(sinthe)) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }

            theta = WcsMath.asind(sinthe);
            z = 1.0 - sinthe;
        }

        final double phi = WcsMath.atan2d(xr - x1 * z, -(yr - y1 * z));

        output[outputOffset] = phi;
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double sinthe = WcsMath.sind(theta);
        final double costhe = WcsMath.cosd(theta);
        final double sinphi = WcsMath.sind(phi);
        final double cosphi = WcsMath.cosd(phi);

        final double s = 1.0 - sinthe;
        final double t = muSinThPlus1 - s;

        if (t == 0.0) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }

        final double r = r0MuSinThPlus1 * costhe / t;
        final double u = r0NegMuCosThSinPh * s / t;
        final double v = r0MuCosThCosPh * s / t;

        output[outputOffset] = r * sinphi - u;
        output[outputOffset + 1] = -r * cosphi - v;
    }

    @Override
    protected Projection createInverse() {
        return new SlantZenithalPerspective(mu, phi0, theta0, opposite(getDirection()));
    }
}
