package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class NamedTransform implements Transform {
    private final Transform delegate;
    private final String name;
    private final String[] inputNames;
    private final String[] outputNames;

    public NamedTransform(@NonNull final Transform delegate, final String name,
                          final String[] inputNames, final String[] outputNames) {
        this.delegate = delegate;
        this.name = name;
        this.inputNames = inputNames != null ? inputNames.clone() : null;
        this.outputNames = outputNames != null ? outputNames.clone() : null;
    }

    public String getName() {
        return name;
    }

    public String[] getInputNames() {
        return inputNames != null ? inputNames.clone() : null;
    }

    public String[] getOutputNames() {
        return outputNames != null ? outputNames.clone() : null;
    }

    public Transform getDelegate() {
        return delegate;
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
        return delegate.hasInverse();
    }

    @Override
    public Transform getInverse() {
        return new NamedTransform(delegate.getInverse(), name, outputNames, inputNames);
    }
}
