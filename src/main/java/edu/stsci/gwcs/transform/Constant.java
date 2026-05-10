package edu.stsci.gwcs.transform;

public class Constant implements Transform {
    private final int inputCount;
    private final double value;

    public Constant(final int inputCount, final double value) {
        if (inputCount <= 0) {
            throw new IllegalArgumentException("Constant transform must have at least 1 input");
        }
        this.inputCount = inputCount;
        this.value = value;
    }

    @Override
    public int getInputCount() {
        return inputCount;
    }

    @Override
    public int getOutputCount() {
        return 1;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        outputs[outputOffset] = value;
    }
}
