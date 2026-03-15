package edu.stsci.gwcs.transform;

public class Shift implements Transform {
    private final double offset;

    public Shift(final double offset) {
        this.offset = offset;
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
        outputs[outputOffset] = inputs[inputOffset] + offset;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new Shift(-offset);
    }
}
