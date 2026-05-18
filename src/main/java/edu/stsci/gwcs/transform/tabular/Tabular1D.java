package edu.stsci.gwcs.transform.tabular;

import java.util.Arrays;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;

/**
 * 1-D piecewise-linear interpolation over a sorted table.
 *
 * <p>All post-construction state is immutable (the input arrays are defensively cloned), so
 * {@code Tabular1D} is safe for concurrent {@code evaluate()} calls from multiple threads.
 *
 * <p>Out-of-range inputs are handled per the {@link OutOfBoundsMode} configured on the instance:
 * {@link OutOfBoundsMode#ERROR} throws (matching astropy's {@code Tabular1D} default with
 * {@code bounds_error=True}), {@link OutOfBoundsMode#FILL} returns the configured fill value
 * (which may be {@code NaN}), and {@link OutOfBoundsMode#EXTRAPOLATE} performs linear
 * extrapolation from the two nearest breakpoints.
 *
 * <p>An analytical {@link #getInverse()} is available when the {@code values} array is strictly
 * monotonic; the inverse swaps {@code points} and {@code values} and is cached.
 */
public class Tabular1D implements Transform {

    /** How to handle inputs outside the {@code points} range. */
    public enum OutOfBoundsMode {
        /** Throw {@link IllegalArgumentException} on out-of-range input. */
        ERROR,
        /** Return the configured {@code fillValue} (may be {@code NaN}) on out-of-range input. */
        FILL,
        /** Linearly extrapolate using the first/last two breakpoints. */
        EXTRAPOLATE
    }

    /**
     * Interpolation method for the lookup table. Only {@link #LINEAR} is implemented; the ASDF
     * schema also permits {@code "nearest"} and {@code "cubic"} but those are deferred.
     */
    public enum InterpolationMethod {
        LINEAR
    }

    private static final int MONOTONIC_INCREASING = 1;
    private static final int MONOTONIC_DECREASING = -1;
    private static final int MONOTONIC_NONE = 0;

    private final double[] points;
    private final double[] values;
    private final OutOfBoundsMode mode;
    private final double fillValue;
    private final InterpolationMethod method;
    private final int valuesMonotonicity;

    private volatile Tabular1D cachedInverse;

    public Tabular1D(@NonNull final double[] points, @NonNull final double[] values,
                     @NonNull final OutOfBoundsMode mode, final double fillValue,
                     @NonNull final InterpolationMethod method) {
        if (points.length != values.length) {
            throw new IllegalArgumentException("Points and values arrays must have the same length");
        }

        if (points.length < 2) {
            throw new IllegalArgumentException("Points and values arrays must have at least 2 elements");
        }

        for (int i = 0; i < points.length; i++) {
            if (!Double.isFinite(points[i])) {
                throw new IllegalArgumentException("Points must all be finite; index " + i + " is " + points[i]);
            }
        }

        for (int i = 0; i < values.length; i++) {
            if (!Double.isFinite(values[i])) {
                throw new IllegalArgumentException("Values must all be finite; index " + i + " is " + values[i]);
            }
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i] <= points[i - 1]) {
                throw new IllegalArgumentException("Points must be strictly increasing");
            }
        }

        if (!Double.isFinite(fillValue) && !Double.isNaN(fillValue)) {
            throw new IllegalArgumentException("fillValue must be finite or NaN, got " + fillValue);
        }

        this.points = points.clone();
        this.values = values.clone();
        this.mode = mode;
        this.fillValue = fillValue;
        this.method = method;
        this.valuesMonotonicity = monotonicity(this.values);
    }

    /** Convenience: defaults to {@link OutOfBoundsMode#ERROR}, {@code NaN} fill, {@link InterpolationMethod#LINEAR}. */
    public Tabular1D(@NonNull final double[] points, @NonNull final double[] values) {
        this(points, values, OutOfBoundsMode.ERROR, Double.NaN, InterpolationMethod.LINEAR);
    }

    /** Convenience: defaults to {@link InterpolationMethod#LINEAR}. */
    public Tabular1D(@NonNull final double[] points, @NonNull final double[] values,
                     @NonNull final OutOfBoundsMode mode, final double fillValue) {
        this(points, values, mode, fillValue, InterpolationMethod.LINEAR);
    }

    private static int monotonicity(final double[] arr) {
        boolean increasing = true;
        boolean decreasing = true;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] <= arr[i - 1]) {
                increasing = false;
            }
            if (arr[i] >= arr[i - 1]) {
                decreasing = false;
            }
        }
        if (increasing) {
            return MONOTONIC_INCREASING;
        }
        if (decreasing) {
            return MONOTONIC_DECREASING;
        }
        return MONOTONIC_NONE;
    }

    @Override
    public int getInputCount() {
        return 1;
    }

    @Override
    public int getOutputCount() {
        return 1;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];

        if (Double.isNaN(x)) {
            outputs[outputOffset] = Double.NaN;
            return;
        }

        final int index = Arrays.binarySearch(points, x);

        if (index >= 0) {
            outputs[outputOffset] = values[index];
            return;
        }

        final int insertionPoint = -index - 1;

        if (insertionPoint == 0 || insertionPoint == points.length) {
            switch (mode) {
                case ERROR:
                    throw new IllegalArgumentException("Input value " + x + " is outside the range of the points array");
                case FILL:
                    outputs[outputOffset] = fillValue;
                    return;
                case EXTRAPOLATE: {
                    final int i0;
                    final int i1;
                    if (insertionPoint == 0) {
                        i0 = 0;
                        i1 = 1;
                    } else {
                        i0 = points.length - 2;
                        i1 = points.length - 1;
                    }
                    final double x0 = points[i0];
                    final double x1 = points[i1];
                    final double v0 = values[i0];
                    final double v1 = values[i1];
                    outputs[outputOffset] = v0 + (v1 - v0) * (x - x0) / (x1 - x0);
                    return;
                }
                default:
                    throw new IllegalStateException("Unhandled OutOfBoundsMode: " + mode);
            }
        }

        final double x0 = points[insertionPoint - 1];
        final double x1 = points[insertionPoint];
        final double v0 = values[insertionPoint - 1];
        final double v1 = values[insertionPoint];

        outputs[outputOffset] = v0 + (v1 - v0) * (x - x0) / (x1 - x0);
    }

    @Override
    public boolean hasInverse() {
        return valuesMonotonicity != MONOTONIC_NONE;
    }

    /**
     * Returns a {@link Tabular1D} that maps {@code values -> points}. Requires {@code values} to
     * be strictly monotonic. For descending {@code values}, both arrays are reversed so the
     * inverse's {@code points} are strictly increasing.
     *
     * <p>The inverse instance is cached.
     */
    @Override
    public Tabular1D getInverse() {
        if (valuesMonotonicity == MONOTONIC_NONE) {
            throw new UnsupportedOperationException(
                    "Tabular1D inverse requires strictly monotonic values; got non-monotonic values"
            );
        }

        Tabular1D inverse = cachedInverse;
        if (inverse != null) {
            return inverse;
        }

        synchronized (this) {
            inverse = cachedInverse;
            if (inverse != null) {
                return inverse;
            }

            final double[] newPoints;
            final double[] newValues;
            if (valuesMonotonicity == MONOTONIC_INCREASING) {
                newPoints = values.clone();
                newValues = points.clone();
            } else {
                final int n = values.length;
                newPoints = new double[n];
                newValues = new double[n];
                for (int i = 0; i < n; i++) {
                    newPoints[i] = values[n - 1 - i];
                    newValues[i] = points[n - 1 - i];
                }
            }
            inverse = new Tabular1D(newPoints, newValues, mode, fillValue, method);
            cachedInverse = inverse;
            return inverse;
        }
    }

    public OutOfBoundsMode getMode() {
        return mode;
    }

    public double getFillValue() {
        return fillValue;
    }

    public InterpolationMethod getMethod() {
        return method;
    }
}
