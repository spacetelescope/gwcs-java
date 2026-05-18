package edu.stsci.gwcs.transform.projection.zenithal;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Base class for zenithal projections.
 *
 * <p>Subclasses customize the family by overriding the radial-plus-correction hooks:
 * <ul>
 *   <li>{@link #thetaFromR(double)} — pix2sky theta from radius. Override for radial projections.</li>
 *   <li>{@link #rFromTheta(double)} — sky2pix radius from theta. Override for radial projections.</li>
 *   <li>{@link #thetaFromXY(double, double, double)} — pix2sky theta from {@code (x, y, rTheta)}.
 *       Defaults to {@code thetaFromR(rTheta)}. Override when theta depends on both axes
 *       (oblique projections such as {@link SlantOrthographic}).</li>
 *   <li>{@link #phiFromXY(double, double, double)} — pix2sky phi. Defaults to
 *       {@code atan2d(x, -y)}. Override for oblique projections that introduce theta-dependent
 *       corrections to phi.</li>
 *   <li>{@link #sky2pixCorrectionX(double, double)} / {@link #sky2pixCorrectionY(double, double)}
 *       — additive corrections to the standard {@code (r·sin(phi), -r·cos(phi))} sky2pix form.
 *       Both default to zero; override for oblique projections.</li>
 * </ul>
 *
 * <p>The hook-based mechanism above is the preferred extension point — it slots every variant
 * into a uniform radial-plus-correction template. Subclasses whose geometry cannot be expressed
 * as a radial-plus-correction (e.g. {@link ZenithalPerspective}, {@link SlantZenithalPerspective})
 * override {@link #evaluatePix2Sky} and {@link #evaluateSky2Pix} directly instead of implementing
 * the radial hooks.
 */
public abstract class ZenithalProjection extends Projection {

    protected ZenithalProjection(final Direction direction) {
        super(direction);
    }

    @Override
    public double getTheta0() {
        return 90.0;
    }

    @Override
    protected void evaluatePix2Sky(final double x, final double y, final double[] output, final int outputOffset) {
        final double rTheta = Math.sqrt(x * x + y * y);
        if (rTheta == 0.0) {
            output[outputOffset] = 0.0;
            output[outputOffset + 1] = 90.0;
            return;
        }
        final double theta = thetaFromXY(x, y, rTheta);
        if (Double.isNaN(theta)) {
            output[outputOffset] = Double.NaN;
            output[outputOffset + 1] = Double.NaN;
            return;
        }
        output[outputOffset] = phiFromXY(x, y, theta);
        output[outputOffset + 1] = theta;
    }

    @Override
    protected void evaluateSky2Pix(final double phi, final double theta, final double[] output, final int outputOffset) {
        final double rTheta = rFromTheta(theta);
        final double baseX = rTheta * WcsMath.sind(phi);
        final double baseY = -rTheta * WcsMath.cosd(phi);
        output[outputOffset] = baseX + sky2pixCorrectionX(phi, theta);
        output[outputOffset + 1] = baseY + sky2pixCorrectionY(phi, theta);
    }

    protected double thetaFromR(double rTheta) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not use the radial hook");
    }

    protected double rFromTheta(double theta) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not use the radial hook");
    }

    /**
     * Compute theta for pix2sky given the input {@code (x, y)} and precomputed
     * {@code rTheta = sqrt(x*x + y*y)}. Default delegates to {@link #thetaFromR(double)} for
     * purely radial projections.
     */
    protected double thetaFromXY(final double x, final double y, final double rTheta) {
        return thetaFromR(rTheta);
    }

    /**
     * Compute phi for pix2sky given the input {@code (x, y)} and the resolved {@code theta}.
     * Default {@code atan2d(x, -y)} is the canonical zenithal form; override for projections
     * with theta-dependent obliquity corrections to phi (e.g. {@link SlantOrthographic}).
     */
    protected double phiFromXY(final double x, final double y, final double theta) {
        return WcsMath.atan2d(x, -y);
    }

    /**
     * Additive correction to the X coordinate in the standard zenithal sky2pix form
     * {@code x = r·sin(phi) + correction}. Defaults to 0; override for oblique projections.
     */
    protected double sky2pixCorrectionX(final double phi, final double theta) {
        return 0.0;
    }

    /**
     * Additive correction to the Y coordinate in the standard zenithal sky2pix form
     * {@code y = -r·cos(phi) + correction}. Defaults to 0; override for oblique projections.
     */
    protected double sky2pixCorrectionY(final double phi, final double theta) {
        return 0.0;
    }

}
