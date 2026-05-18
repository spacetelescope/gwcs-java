package edu.stsci.gwcs.transform.projection.quadcube;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

public abstract class QuadCubeProjection extends Projection {

    protected static final double DEG_PER_FACE = 45.0;
    protected static final double INV_DEG_PER_FACE = 1.0 / 45.0;

    protected QuadCubeProjection(final Direction direction) {
        super(direction);
    }

    /**
     * Classify a unit-sphere direction (l, m, n) into one of the six cube faces.
     *
     * <p>Faces are indexed 0..5 following the wcslib convention:
     * 0 = +z (north), 1 = +x, 2 = +y, 3 = -x, 4 = -y, 5 = -z (south).
     * Ties prefer the earlier face (the iteration uses strict {@code >}), keeping
     * the dispatch deterministic on face boundaries.
     */
    protected static int classifyFace(final double l, final double m, final double n) {
        int face = 0;
        double zeta = n;
        if (l > zeta) { face = 1; zeta = l; }
        if (m > zeta) { face = 2; zeta = m; }
        if (-l > zeta) { face = 3; zeta = -l; }
        if (-m > zeta) { face = 4; zeta = -m; }
        if (-n > zeta) { face = 5; }
        return face;
    }

    /**
     * Return the dominant-axis magnitude (signed) selected by {@link #classifyFace}.
     */
    protected static double zetaForFace(final int face, final double l, final double m, final double n) {
        switch (face) {
            case 0: return n;
            case 1: return l;
            case 2: return m;
            case 3: return -l;
            case 4: return -m;
            case 5: return -n;
            default: throw new IllegalStateException("Invalid quad-cube face index: " + face);
        }
    }

    /**
     * Perform the pix2sky boundary validation and face dispatch common to all quad-cube
     * projections. Returns a 3-element array: [face, xf, yf] where xf/yf are the
     * face-local coordinates in [-1, 1]. Returns null if the input is out of range.
     */
    protected static double[] pix2skyFaceDispatch(final double x, final double y) {
        double xf = x * INV_DEG_PER_FACE;
        double yf = y * INV_DEG_PER_FACE;

        if (Math.abs(xf) <= 1.0) {
            if (Math.abs(yf) > 3.0) {
                return null;
            }
        } else if (Math.abs(xf) > 7.0 || Math.abs(yf) > 1.0) {
            return null;
        }

        if (xf < -1.0) xf += 8.0;

        int face;
        if (xf > 5.0) {
            face = 4;
            xf -= 6.0;
        } else if (xf > 3.0) {
            face = 3;
            xf -= 4.0;
        } else if (xf > 1.0) {
            face = 2;
            xf -= 2.0;
        } else if (yf > 1.0) {
            face = 0;
            yf -= 2.0;
        } else if (yf < -1.0) {
            face = 5;
            yf += 2.0;
        } else {
            face = 1;
        }

        return new double[]{face, xf, yf};
    }

    /**
     * Convert direction cosines (l, m, n) to (phi, theta) in degrees and write to output.
     */
    protected static void directionCosinesToPhiTheta(final double l, final double m, final double n,
                                                     final double[] output, final int outputOffset) {
        if (l == 0.0 && m == 0.0) {
            output[outputOffset] = 0.0;
        } else {
            output[outputOffset] = WcsMath.atan2d(m, l);
        }
        output[outputOffset + 1] = WcsMath.asind(n);
    }

    /**
     * For sky2pix: given face and direction cosines, return {xi, eta, x0, y0}.
     */
    protected static double[] sky2pixFaceCoordinates(final int face, final double l, final double m, final double n) {
        switch (face) {
            case 1: return new double[]{m, n, 0.0, 0.0};
            case 2: return new double[]{-l, n, 2.0, 0.0};
            case 3: return new double[]{-m, n, 4.0, 0.0};
            case 4: return new double[]{l, n, 6.0, 0.0};
            case 5: return new double[]{m, l, 0.0, -2.0};
            default: return new double[]{m, -l, 0.0, 2.0};
        }
    }
}
