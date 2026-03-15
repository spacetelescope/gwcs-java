package edu.stsci.gwcs.transform;

public interface Transform {
    int getInputCount();

    int getOutputCount();

    void evaluate(double[] inputs, int inputOffset, double[] outputs, int outputOffset);

    default double[] evaluate(double... inputs) {
        if (inputs.length != getInputCount()) {
            throw new IllegalArgumentException(
                    "Expected " + getInputCount() + " inputs, got " + inputs.length
            );
        }
        final double[] outputs = new double[getOutputCount()];
        evaluate(inputs, 0, outputs, 0);
        return outputs;
    }

    default boolean hasInverse() {
        return false;
    }

    default Transform getInverse() {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " does not have an analytical inverse"
        );
    }
}