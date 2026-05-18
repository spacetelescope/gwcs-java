package edu.stsci.gwcs.transform.projection;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;

public abstract class Projection implements Transform {
    /** Conventional WCS scaling factor R_0 = 180°/π that converts between radians and degrees. */
    protected static final double R0 = 180.0 / Math.PI;

    /**
     * Direction in which the projection maps. {@link #PIX2SKY} is the FITS WCS forward direction
     * (intermediate world coordinates to native sky), {@link #SKY2PIX} is the inverse.
     */
    public enum Direction {
        PIX2SKY,
        SKY2PIX
    }

    private final Direction direction;
    private volatile Projection cachedInverse;

    protected Projection(@NonNull final Direction direction) {
        this.direction = direction;
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double a = inputs[inputOffset];
        final double b = inputs[inputOffset + 1];
        if (anyNaN(a, b)) {
            outputs[outputOffset] = Double.NaN;
            outputs[outputOffset + 1] = Double.NaN;
            return;
        }
        if (direction == Direction.SKY2PIX) {
            evaluateSky2Pix(a, b, outputs, outputOffset);
        } else {
            evaluatePix2Sky(a, b, outputs, outputOffset);
        }
    }

    protected abstract void evaluatePix2Sky(double x, double y, double[] output, int outputOffset);

    protected abstract void evaluateSky2Pix(double phi, double theta, double[] output, int outputOffset);

    protected abstract Projection createInverse();

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        Projection result = cachedInverse;
        if (result == null) {
            synchronized (this) {
                result = cachedInverse;
                if (result == null) {
                    result = createInverse();
                    result.cachedInverse = this;
                    cachedInverse = result;
                }
            }
        }
        return result;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Native longitude of the projection's fiducial point. Always 0.0 in the FITS WCS standard.
     * Matches wcslib's {@code prj.phi0}.
     */
    public double getPhi0() {
        return 0.0;
    }

    /**
     * Native latitude of the projection's fiducial point. Zenithal projections use 90.0;
     * most others use 0.0; conic projections use sigma. Matches wcslib's {@code prj.theta0}.
     */
    public double getTheta0() {
        return 0.0;
    }

    /**
     * Returns the opposite of the supplied {@link Direction}. Used by subclasses inside
     * {@code createInverse()} to flip direction with a single call.
     */
    protected static Direction opposite(final Direction direction) {
        return direction == Direction.PIX2SKY ? Direction.SKY2PIX : Direction.PIX2SKY;
    }

    protected static boolean anyNaN(final double a, final double b) {
        return Double.isNaN(a) || Double.isNaN(b);
    }

    /**
     * Solves the quadratic {@code a * sinθ² + 2*b * sinθ + c = 0} that arises in the slant
     * zenithal pix2Sky path, picks the in-range root, and clamps to ±1 within tolerance.
     * Returns {@link Double#NaN} when both roots are out of [-1, 1] beyond tolerance, or when
     * the discriminant {@code b² - a*c} is negative.
     */
    protected static double solveQuadraticSinTheta(final double a, final double b, final double c) {
        final double tol = 1.0e-13;
        final double d = b * b - a * c;
        if (d < 0.0) {
            return Double.NaN;
        }
        final double sqrtD = Math.sqrt(d);
        final double sinth1 = (-b + sqrtD) / a;
        final double sinth2 = (-b - sqrtD) / a;
        double sinthe = sinth1 > sinth2 ? sinth1 : sinth2;
        if (sinthe > 1.0) {
            if (sinthe - 1.0 < tol) {
                sinthe = 1.0;
            } else {
                sinthe = sinth1 < sinth2 ? sinth1 : sinth2;
            }
        }
        if (sinthe < -1.0) {
            if (sinthe + 1.0 > -tol) {
                sinthe = -1.0;
            }
        }
        if (sinthe > 1.0 || sinthe < -1.0) {
            return Double.NaN;
        }
        return sinthe;
    }
}
