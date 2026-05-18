package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Quadrilateralized spherical cube projection — FITS WCS CTYPE code {@code QSC}.
 */
public class QuadSphericalCube extends QuadCubeProjection {

    private static final double INV_SQRT2 = 1.0 / Math.sqrt(2.0);

    public QuadSphericalCube(final Direction direction) {
        super(direction);
    }

    public QuadSphericalCube() {
        this(Direction.PIX2SKY);
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double[] faceResult = pix2skyFaceDispatch(x, y);
        if (faceResult == null) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }

        final int face = (int) faceResult[0];
        final double xf = faceResult[1];
        final double yf = faceResult[2];

        double omega;
        double tau;
        double w;
        double zeta;
        double zeco;
        boolean direct = Math.abs(xf) > Math.abs(yf);

        if (direct) {
            if (xf == 0.0) {
                omega = 0.0;
                tau = 1.0;
                zeta = 1.0;
                zeco = 0.0;
            } else {
                w = 15.0 * yf / xf;
                double sinw = WcsMath.sind(w);
                double cosw = WcsMath.cosd(w);
                omega = sinw / (cosw - INV_SQRT2);
                tau = 1.0 + omega * omega;
                zeco = xf * xf * (1.0 - 1.0 / Math.sqrt(1.0 + tau));
                zeta = 1.0 - zeco;
            }
        } else {
            if (yf == 0.0) {
                omega = 0.0;
                tau = 1.0;
                zeta = 1.0;
                zeco = 0.0;
            } else {
                w = 15.0 * xf / yf;
                double sinw = WcsMath.sind(w);
                double cosw = WcsMath.cosd(w);
                omega = sinw / (cosw - INV_SQRT2);
                tau = 1.0 + omega * omega;
                zeco = yf * yf * (1.0 - 1.0 / Math.sqrt(1.0 + tau));
                zeta = 1.0 - zeco;
            }
        }

        if (zeta < -1.0) {
            if (zeta < -1.0 - 1.0e-12) {
                output[outputOffset] = Double.NaN;
                output[outputOffset + 1] = Double.NaN;
                return;
            }
            zeta = -1.0;
            zeco = 2.0;
            w = 0.0;
        } else {
            w = Math.sqrt(zeco * (2.0 - zeco) / tau);
        }

        double l;
        double m;
        double n;
        switch (face) {
        case 1:
            l = zeta;
            if (direct) {
                m = w;
                if (xf < 0.0) m = -m;
                n = m * omega;
            } else {
                n = w;
                if (yf < 0.0) n = -n;
                m = n * omega;
            }
            break;
        case 2:
            m = zeta;
            if (direct) {
                l = w;
                if (xf > 0.0) l = -l;
                n = -l * omega;
            } else {
                n = w;
                if (yf < 0.0) n = -n;
                l = -n * omega;
            }
            break;
        case 3:
            l = -zeta;
            if (direct) {
                m = w;
                if (xf > 0.0) m = -m;
                n = -m * omega;
            } else {
                n = w;
                if (yf < 0.0) n = -n;
                m = -n * omega;
            }
            break;
        case 4:
            m = -zeta;
            if (direct) {
                l = w;
                if (xf < 0.0) l = -l;
                n = l * omega;
            } else {
                n = w;
                if (yf < 0.0) n = -n;
                l = n * omega;
            }
            break;
        case 5:
            n = -zeta;
            if (direct) {
                m = w;
                if (xf < 0.0) m = -m;
                l = m * omega;
            } else {
                l = w;
                if (yf < 0.0) l = -l;
                m = l * omega;
            }
            break;
        default:
            n = zeta;
            if (direct) {
                m = w;
                if (xf < 0.0) m = -m;
                l = -m * omega;
            } else {
                l = w;
                if (yf > 0.0) l = -l;
                m = -l * omega;
            }
            break;
        }

        directionCosinesToPhiTheta(l, m, n, output, outputOffset);
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        double sinphi = WcsMath.sind(phi);
        double cosphi = WcsMath.cosd(phi);
        double sinthe = WcsMath.sind(theta);
        double costhe = WcsMath.cosd(theta);

        if (Math.abs(theta) == 90.0) {
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = Math.copySign(2.0 * DEG_PER_FACE, theta);
            return;
        }

        double l = costhe * cosphi;
        double m = costhe * sinphi;
        double n = sinthe;

        final int face = classifyFace(l, m, n);
        final double zeta = zetaForFace(face, l, m, n);
        final double[] fc = sky2pixFaceCoordinates(face, l, m, n);
        final double xi = fc[0];
        final double eta = fc[1];
        final double x0 = fc[2];
        final double y0 = fc[3];

        double zeco = 1.0 - zeta;

        if (zeco < 1.0e-8) {
            zeco = computeSmallZeco(face, phi, theta, sinphi, cosphi);
        }

        double xf = 0.0;
        double yf = 0.0;
        if (xi != 0.0 || eta != 0.0) {
            double omega;
            double tau;
            if (-xi > Math.abs(eta)) {
                omega = eta / xi;
                tau = 1.0 + omega * omega;
                xf = -Math.sqrt(zeco / (1.0 - 1.0 / Math.sqrt(1.0 + tau)));
                yf = (xf / 15.0) * (WcsMath.atan2d(omega, 1.0) - WcsMath.asind(omega / Math.sqrt(tau + tau)));
            } else if (xi > Math.abs(eta)) {
                omega = eta / xi;
                tau = 1.0 + omega * omega;
                xf = Math.sqrt(zeco / (1.0 - 1.0 / Math.sqrt(1.0 + tau)));
                yf = (xf / 15.0) * (WcsMath.atan2d(omega, 1.0) - WcsMath.asind(omega / Math.sqrt(tau + tau)));
            } else if (-eta >= Math.abs(xi)) {
                omega = xi / eta;
                tau = 1.0 + omega * omega;
                yf = -Math.sqrt(zeco / (1.0 - 1.0 / Math.sqrt(1.0 + tau)));
                xf = (yf / 15.0) * (WcsMath.atan2d(omega, 1.0) - WcsMath.asind(omega / Math.sqrt(tau + tau)));
            } else if (eta >= Math.abs(xi)) {
                omega = xi / eta;
                tau = 1.0 + omega * omega;
                yf = Math.sqrt(zeco / (1.0 - 1.0 / Math.sqrt(1.0 + tau)));
                xf = (yf / 15.0) * (WcsMath.atan2d(omega, 1.0) - WcsMath.asind(omega / Math.sqrt(tau + tau)));
            }
        }

        if (Math.abs(xf) > 1.0) {
            xf = Math.copySign(1.0, xf);
        }
        if (Math.abs(yf) > 1.0) {
            yf = Math.copySign(1.0, yf);
        }

        output[outputOffset] = DEG_PER_FACE * (xf + x0);
        output[outputOffset + 1] = DEG_PER_FACE * (yf + y0);
    }

    private static double computeSmallZeco(final int face, final double phi, final double theta,
                                           final double sinphi, final double cosphi) {
        double t;
        double p;
        switch (face) {
            case 1:
                t = Math.toRadians(theta);
                p = Math.atan2(sinphi, cosphi);
                return (p * p + t * t) / 2.0;
            case 2:
                t = Math.toRadians(theta);
                p = Math.atan2(sinphi, cosphi) - Math.PI / 2.0;
                return (p * p + t * t) / 2.0;
            case 3:
                t = Math.toRadians(theta);
                p = Math.atan2(sinphi, cosphi);
                p -= Math.copySign(Math.PI, p);
                return (p * p + t * t) / 2.0;
            case 4:
                t = Math.toRadians(theta);
                p = Math.atan2(sinphi, cosphi) + Math.PI / 2.0;
                return (p * p + t * t) / 2.0;
            case 5:
                t = Math.toRadians(theta + 90.0);
                return t * t / 2.0;
            default:
                t = Math.toRadians(90.0 - theta);
                return t * t / 2.0;
        }
    }

    @Override
    protected Projection createInverse() {
        return new QuadSphericalCube(opposite(getDirection()));
    }
}
