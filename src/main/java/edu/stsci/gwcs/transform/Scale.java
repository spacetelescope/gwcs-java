package edu.stsci.gwcs.transform;

public class Scale implements Transform {
    private final double factor;

    public Scale(final double factor) {
        this.factor = factor;
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
        outputs[outputOffset] = inputs[inputOffset] * factor;
    }

    @Override
    public boolean hasInverse() {
        return factor != 0.0;
    }

    @Override
    public Transform getInverse() {
        if (!hasInverse()) {
            throw new UnsupportedOperationException("Cannot invert a Scale transform with a factor of 0.0");
        }

        return new Scale(1.0 / factor);
    }
}
