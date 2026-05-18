package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Tangential spherical cube projection — FITS WCS CTYPE code {@code TSC}.
 */
public class TangentialSphericalCube extends QuadCubeProjection {

    public TangentialSphericalCube(final Direction direction) {
        super(direction);
    }

    public TangentialSphericalCube() {
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
        double xf = faceResult[1];
        double yf = faceResult[2];

        double l;
        double m;
        double n;

        switch (face) {
        case 4:
            m = -1.0 / Math.sqrt(1.0 + xf * xf + yf * yf);
            l = -m * xf;
            n = -m * yf;
            break;
        case 3:
            l = -1.0 / Math.sqrt(1.0 + xf * xf + yf * yf);
            m = l * xf;
            n = -l * yf;
            break;
        case 2:
            m = 1.0 / Math.sqrt(1.0 + xf * xf + yf * yf);
            l = -m * xf;
            n = m * yf;
            break;
        case 0:
            n = 1.0 / Math.sqrt(1.0 + xf * xf + yf * yf);
            l = -n * yf;
            m = n * xf;
            break;
        case 5:
            n = -1.0 / Math.sqrt(1.0 + xf * xf + yf * yf);
            l = -n * yf;
            m = -n * xf;
            break;
        default:
            l = 1.0 / Math.sqrt(1.0 + xf * xf + yf * yf);
            m = l * xf;
            n = l * yf;
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

        double l = costhe * cosphi;
        double m = costhe * sinphi;
        double n = sinthe;

        final int face = classifyFace(l, m, n);
        final double zeta = zetaForFace(face, l, m, n);
        final double[] fc = sky2pixFaceCoordinates(face, l, m, n);
        final double x0 = fc[2];
        final double y0 = fc[3];

        double xf = fc[0] / zeta;
        double yf = fc[1] / zeta;

        if (Math.abs(xf) > 1.0) {
            xf = Math.copySign(1.0, xf);
        }
        if (Math.abs(yf) > 1.0) {
            yf = Math.copySign(1.0, yf);
        }

        output[outputOffset] = DEG_PER_FACE * (xf + x0);
        output[outputOffset + 1] = DEG_PER_FACE * (yf + y0);
    }

    @Override
    protected Projection createInverse() {
        return new TangentialSphericalCube(opposite(getDirection()));
    }
}
