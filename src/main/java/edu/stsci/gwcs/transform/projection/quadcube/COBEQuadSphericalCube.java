package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * COBE Quadrilateralized Spherical Cube projection — FITS WCS CTYPE code {@code CSC}. The forward (pix2Sky) polynomial
 * coefficients are from Chan &amp; O'Neill (1975) as reproduced in the wcslib reference
 * implementation in {@code C/prj.c} (see {@code cscx2s}/{@code csccsc}). The inverse
 * (sky2Pix) coefficients are the published Sky2Pix expansion used by astropy.
 */
public class COBEQuadSphericalCube extends QuadCubeProjection {

    private static final double P00 = -0.27292696;
    private static final double P10 = -0.07629969;
    private static final double P20 = -0.22797056;
    private static final double P30 =  0.54852384;
    private static final double P40 = -0.62930065;
    private static final double P50 =  0.25795794;
    private static final double P60 =  0.02584375;
    private static final double P01 = -0.02819452;
    private static final double P11 = -0.01471565;
    private static final double P21 =  0.48051509;
    private static final double P31 = -1.74114454;
    private static final double P41 =  1.71547508;
    private static final double P51 = -0.53022337;
    private static final double P02 =  0.27058160;
    private static final double P12 = -0.56800938;
    private static final double P22 =  0.30803317;
    private static final double P32 =  0.98938102;
    private static final double P42 = -0.83180469;
    private static final double P03 = -0.60441560;
    private static final double P13 =  1.50880086;
    private static final double P23 = -0.93678576;
    private static final double P33 =  0.08693841;
    private static final double P04 =  0.93412077;
    private static final double P14 = -1.41601920;
    private static final double P24 =  0.33887446;
    private static final double P05 = -0.63915306;
    private static final double P15 =  0.52032238;
    private static final double P06 =  0.14381585;

    private static final double GSTAR  =  1.37484847732;
    private static final double MM     =  0.004869491981;
    private static final double GAMMA  = -0.13161671474;
    private static final double OMEGA1 = -0.159596235474;
    private static final double D0  =  0.0759196200467;
    private static final double D1  = -0.0217762490699;
    private static final double C00 =  0.141189631152;
    private static final double C10 =  0.0809701286525;
    private static final double C01 = -0.281528535557;
    private static final double C11 =  0.15384112876;
    private static final double C20 = -0.178251207466;
    private static final double C02 =  0.106959469314;

    public COBEQuadSphericalCube(final Direction direction) {
        super(direction);
    }

    public COBEQuadSphericalCube() {
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

        final double xx = xf * xf;
        final double yy = yf * yf;

        double z0 = P00 + xx * (P10 + xx * (P20 + xx * (P30 + xx * (P40 + xx * (P50 + xx * P60)))));
        double z1 = P01 + xx * (P11 + xx * (P21 + xx * (P31 + xx * (P41 + xx * P51))));
        double z2 = P02 + xx * (P12 + xx * (P22 + xx * (P32 + xx * P42)));
        double z3 = P03 + xx * (P13 + xx * (P23 + xx * P33));
        double z4 = P04 + xx * (P14 + xx * P24);
        double z5 = P05 + xx * P15;
        double z6 = P06;

        double chi = z0 + yy * (z1 + yy * (z2 + yy * (z3 + yy * (z4 + yy * (z5 + yy * z6)))));
        chi = xf + xf * (1.0 - xx) * chi;

        z0 = P00 + yy * (P10 + yy * (P20 + yy * (P30 + yy * (P40 + yy * (P50 + yy * P60)))));
        z1 = P01 + yy * (P11 + yy * (P21 + yy * (P31 + yy * (P41 + yy * P51))));
        z2 = P02 + yy * (P12 + yy * (P22 + yy * (P32 + yy * P42)));
        z3 = P03 + yy * (P13 + yy * (P23 + yy * P33));
        z4 = P04 + yy * (P14 + yy * P24);
        z5 = P05 + yy * P15;
        z6 = P06;

        double psi = z0 + xx * (z1 + xx * (z2 + xx * (z3 + xx * (z4 + xx * (z5 + xx * z6)))));
        psi = yf + yf * (1.0 - yy) * psi;

        final double t = 1.0 / Math.sqrt(chi * chi + psi * psi + 1.0);

        double l;
        double m;
        double n;
        switch (face) {
        case 1:
            l = t;
            m = chi * l;
            n = psi * l;
            break;
        case 2:
            m = t;
            l = -chi * m;
            n = psi * m;
            break;
        case 3:
            l = -t;
            m = chi * l;
            n = -psi * l;
            break;
        case 4:
            m = -t;
            l = -chi * m;
            n = -psi * m;
            break;
        case 5:
            n = -t;
            l = -psi * n;
            m = -chi * n;
            break;
        default:
            n = t;
            l = -psi * n;
            m = chi * n;
            break;
        }

        directionCosinesToPhiTheta(l, m, n, output, outputOffset);
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double sinphi = WcsMath.sind(phi);
        final double cosphi = WcsMath.cosd(phi);
        final double sinthe = WcsMath.sind(theta);
        final double costhe = WcsMath.cosd(theta);

        final double l = costhe * cosphi;
        final double m = costhe * sinphi;
        final double n = sinthe;

        final int face = classifyFace(l, m, n);
        final double zeta = zetaForFace(face, l, m, n);
        final double[] fc = sky2pixFaceCoordinates(face, l, m, n);
        final double xi = fc[0];
        final double eta = fc[1];
        final double x0 = fc[2];
        final double y0 = fc[3];

        double chi = xi / zeta;
        double psi = eta / zeta;

        double chi2 = chi * chi;
        double psi2 = psi * psi;
        double chi2co = 1.0 - chi2;
        double psi2co = 1.0 - psi2;

        double chipsi = Math.abs(chi * psi);
        double chi4 = (chi2 > 1.0e-16) ? chi2 * chi2 : 0.0;
        double psi4 = (psi2 > 1.0e-16) ? psi2 * psi2 : 0.0;
        double chi2psi2 = (chipsi > 1.0e-16) ? chi2 * psi2 : 0.0;

        double xf = chi * (chi2 + chi2co * (GSTAR + psi2 * (GAMMA * chi2co + MM * chi2 +
                psi2co * (C00 + C10 * chi2 + C01 * psi2 + C11 * chi2psi2 + C20 * chi4 +
                C02 * psi4)) + chi2 * (OMEGA1 - chi2co * (D0 + D1 * chi2))));
        double yf = psi * (psi2 + psi2co * (GSTAR + chi2 * (GAMMA * psi2co + MM * psi2 +
                chi2co * (C00 + C10 * psi2 + C01 * chi2 + C11 * chi2psi2 + C20 * psi4 +
                C02 * chi4)) + psi2 * (OMEGA1 - psi2co * (D0 + D1 * psi2))));

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
        return new COBEQuadSphericalCube(opposite(getDirection()));
    }
}
