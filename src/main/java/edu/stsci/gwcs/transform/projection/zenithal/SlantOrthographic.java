package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Slant orthographic (sine) projection — FITS WCS CTYPE code {@code SIN}.
 *
 * <p>The general "synthesis" form (with non-zero {@code xi} and {@code eta}) is handled by
 * overriding the obliquity hooks {@link #thetaFromXY(double, double, double)},
 * {@link #phiFromXY(double, double, double)}, and the sky2pix correction terms; the
 * orthographic special case ({@code xi == 0 && eta == 0}) falls through the standard
 * {@link ZenithalProjection} radial path via {@link #thetaFromR(double)} and
 * {@link #rFromTheta(double)}.
 */
public class SlantOrthographic extends ZenithalProjection {

    private final double xi;
    private final double eta;
    private final boolean general;
    private final double xiEta2;
    private final double a;

    public SlantOrthographic(final double xi, final double eta, final Direction direction) {
        super(direction);
        this.xi = xi;
        this.eta = eta;
        this.general = xi != 0.0 || eta != 0.0;
        this.xiEta2 = xi * xi + eta * eta;
        this.a = xiEta2 + 1.0;
    }

    public SlantOrthographic(final double xi, final double eta) {
        this(xi, eta, Direction.PIX2SKY);
    }

    public SlantOrthographic() {
        this(0.0, 0.0, Direction.PIX2SKY);
    }

    @Override
    protected double thetaFromR(final double rTheta) {
        final double s = rTheta / R0;
        if (s * s < 0.5) {
            return Math.toDegrees(Math.acos(s));
        }
        return WcsMath.asind(Math.sqrt(1.0 - s * s));
    }

    @Override
    protected double rFromTheta(final double theta) {
        return R0 * WcsMath.cosd(theta);
    }

    @Override
    protected double thetaFromXY(final double x, final double y, final double rTheta) {
        if (!general) {
            return thetaFromR(rTheta);
        }

        final double x0 = x / R0;
        final double y0 = y / R0;
        final double r2 = x0 * x0 + y0 * y0;

        final double xy = x0 * xi + y0 * eta;

        if (r2 < 1.0e-10) {
            return 90.0 - Math.toDegrees(Math.sqrt(r2 / (1.0 + xy)));
        }

        final double b = xy - xiEta2;
        final double c = r2 - 2.0 * xy + xiEta2 - 1.0;

        final double sinthe = solveQuadraticSinTheta(a, b, c);
        if (Double.isNaN(sinthe)) {
            return Double.NaN;
        }
        return WcsMath.asind(sinthe);
    }

    @Override
    protected double phiFromXY(final double x, final double y, final double theta) {
        if (!general) {
            return WcsMath.atan2d(x, -y);
        }

        final double x0 = x / R0;
        final double y0 = y / R0;
        final double sinthe = WcsMath.sind(theta);
        final double z = 1.0 - sinthe;

        final double x1 = -y0 + eta * z;
        final double y1 = x0 - xi * z;
        if (x1 == 0.0 && y1 == 0.0) {
            return 0.0;
        }
        return WcsMath.atan2d(y1, x1);
    }

    @Override
    protected double sky2pixCorrectionX(final double phi, final double theta) {
        if (!general) {
            return 0.0;
        }
        return R0 * xi * (1.0 - WcsMath.sind(theta));
    }

    @Override
    protected double sky2pixCorrectionY(final double phi, final double theta) {
        if (!general) {
            return 0.0;
        }
        return R0 * eta * (1.0 - WcsMath.sind(theta));
    }

    @Override
    protected Projection createInverse() {
        return new SlantOrthographic(xi, eta, opposite(getDirection()));
    }
}
