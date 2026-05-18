package edu.stsci.gwcs.transform.compound;

import edu.stsci.gwcs.transform.Transform;

import lombok.NonNull;
import java.util.stream.Stream;

public class Concatenate implements Transform {
    private final Transform[] transforms;
    private final int inputCount;
    private final int outputCount;

    public Concatenate(@NonNull final Transform[] transforms) {
        if (transforms.length == 0) {
            throw new IllegalArgumentException("Concatenate requires at least one child transform");
        }
        for (int i = 0; i < transforms.length; i++) {
            if (transforms[i] == null) {
                throw new IllegalArgumentException("Transform at index " + i + " must not be null");
            }
        }
        this.transforms = transforms.clone();
        this.inputCount = Stream.of(transforms).mapToInt(Transform::getInputCount).sum();
        this.outputCount = Stream.of(transforms).mapToInt(Transform::getOutputCount).sum();
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
        final double[] localInputs = new double[inputCount];
        System.arraycopy(inputs, inputOffset, localInputs, 0, inputCount);

        int currentInputOffset = 0;
        int currentOutputOffset = outputOffset;

        for (final Transform transform : transforms) {
            transform.evaluate(localInputs, currentInputOffset, outputs, currentOutputOffset);

            currentInputOffset += transform.getInputCount();
            currentOutputOffset += transform.getOutputCount();
        }
    }

    @Override
    public boolean hasInverse() {
        return Stream.of(transforms).allMatch(Transform::hasInverse);
    }

    @Override
    public Transform getInverse() {
        if (!hasInverse()) {
            throw new UnsupportedOperationException(
                    "Concatenate does not have an analytical inverse"
            );
        }
        return new Concatenate(
                Stream.of(transforms).map(Transform::getInverse).toArray(Transform[]::new)
        );
    }
}
