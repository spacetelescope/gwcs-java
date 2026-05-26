package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class ExplicitInverseWrapper implements DelegatingTransform {
    private final Transform delegate;
    private final Transform inverse;

    public Transform getDelegate() {
        return delegate;
    }

    public ExplicitInverseWrapper(@NonNull final Transform delegate, @NonNull final Transform inverse) {
        if (delegate.getOutputCount() != inverse.getInputCount()
                || inverse.getOutputCount() != delegate.getInputCount()) {
            throw new IllegalArgumentException("Dimension mismatch between forward and inverse models");
        }
        this.delegate = delegate;
        this.inverse = inverse;
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
        delegate.evaluate(inputs, inputOffset, outputs, outputOffset);
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new ExplicitInverseWrapper(inverse, delegate);
    }
}
