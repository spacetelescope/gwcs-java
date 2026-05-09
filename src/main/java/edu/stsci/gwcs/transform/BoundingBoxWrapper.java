package edu.stsci.gwcs.transform;

import lombok.NonNull;

public final class BoundingBoxWrapper implements Transform {
    private final Transform delegate;
    private final double[][] intervals;
    private final double fillValue;

    public BoundingBoxWrapper(@NonNull final Transform delegate, @NonNull final double[][] intervals, final double fillValue) {
        if (intervals.length != delegate.getInputCount()) {
            throw new IllegalArgumentException(
                    "Intervals array length must exactly match the delegate's input count"
            );
        }

        this.delegate = delegate;
        this.fillValue = fillValue;

        this.intervals = new double[intervals.length][];
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i] == null || intervals[i].length != 2) {
                throw new IllegalArgumentException("Each interval must be a [min, max] pair");
            }
            if (intervals[i][0] > intervals[i][1]) {
                throw new IllegalArgumentException("Interval min must not exceed max");
            }
            this.intervals[i] = intervals[i].clone();
        }
    }

    @Override
    public int getInputCount() {
        return delegate.getInputCount();
    }

    @Override
    public int getOutputCount() {
        return delegate.getOutputCount();
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        for (int i = 0; i < intervals.length; i++) {
            final double val = inputs[inputOffset + i];

            if (val < intervals[i][0] || val > intervals[i][1] || Double.isNaN(val)) {
                fillOutputs(outputs, outputOffset);
                return;
            }
        }

        delegate.evaluate(inputs, inputOffset, outputs, outputOffset);
    }

    private void fillOutputs(final double[] outputs, final int outputOffset) {
        final int outCount = getOutputCount();
        for (int i = 0; i < outCount; i++) {
            outputs[outputOffset + i] = fillValue;
        }
    }

    @Override
    public boolean hasInverse() {
        return delegate.hasInverse();
    }

    @Override
    public Transform getInverse() {
        return delegate.getInverse();
    }
}