package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class Divide implements Transform {
    private final Transform[] transforms;
    private final int inputCount;
    private final int outputCount;

    public Divide(@NonNull final Transform[] transforms) {
        if (transforms.length < 2) {
            throw new IllegalArgumentException("At least two transforms required");
        }

        this.inputCount = transforms[0].getInputCount();
        this.outputCount = transforms[0].getOutputCount();

        for (int i = 1; i < transforms.length; i++) {
            if (transforms[i].getInputCount() != inputCount || transforms[i].getOutputCount() != outputCount) {
                throw new IllegalArgumentException("All transforms must have the same input and output counts");
            }
        }

        this.transforms = transforms.clone();
    }

    @Override
    public int getInputCount() {
        return inputCount;
    }

    @Override
    public int getOutputCount() {
        return outputCount;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        transforms[0].evaluate(inputs, inputOffset, outputs, outputOffset);

        final double[] buffer = new double[outputCount];
        for (int i = 1; i < transforms.length; i++) {
            transforms[i].evaluate(inputs, inputOffset, buffer, 0);

            for (int j = 0; j < outputCount; j++) {
                outputs[outputOffset + j] /= buffer[j];
            }
        }
    }
}
