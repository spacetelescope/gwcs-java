package edu.stsci.gwcs.transform;

public class Identity implements Transform {
    private final int dimensionCount;

    public Identity(final int dimensionCount) {
        if (dimensionCount <= 0) {
            throw new IllegalArgumentException("Identity transform must have at least 1 dimension");
        }
        this.dimensionCount = dimensionCount;
    }

    @Override
    public int getInputCount() {
        return dimensionCount;
    }

    @Override
    public int getOutputCount() {
        return dimensionCount;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        System.arraycopy(inputs, inputOffset, outputs, outputOffset, dimensionCount);
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return this;
    }
}
