package edu.stsci.gwcs.transform.compound;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;

public class Power implements Transform {
    private final Transform[] transforms;
    private final int inputCount;
    private final int outputCount;

    public Power(@NonNull final Transform[] transforms) {
        if (transforms.length != 2) {
            throw new IllegalArgumentException("Power requires exactly two transforms (base and exponent)");
        }

        for (int i = 0; i < transforms.length; i++) {
            if (transforms[i] == null) {
                throw new IllegalArgumentException("Transform at index " + i + " must not be null");
            }
        }

        this.inputCount = transforms[0].getInputCount();
        this.outputCount = transforms[0].getOutputCount();

        if (transforms[1].getInputCount() != inputCount || transforms[1].getOutputCount() != outputCount) {
            throw new IllegalArgumentException("Both transforms must have the same input and output counts");
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
        final double[] inputCopy = new double[inputCount];
        System.arraycopy(inputs, inputOffset, inputCopy, 0, inputCount);

        transforms[0].evaluate(inputCopy, 0, outputs, outputOffset);

        final double[] buffer = new double[outputCount];
        transforms[1].evaluate(inputCopy, 0, buffer, 0);

        for (int j = 0; j < outputCount; j++) {
            outputs[outputOffset + j] = Math.pow(outputs[outputOffset + j], buffer[j]);
        }
    }
}
