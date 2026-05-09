package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class RemapAxes implements Transform {
    private final int[] mapping;
    private final int inputCount;
    private final int outputCount;

    public RemapAxes(@NonNull final int[] mapping, final int inputCount) {
        if (mapping.length == 0) {
            throw new IllegalArgumentException("Mapping array cannot be empty");
        }

        if (inputCount <= 0) {
            throw new IllegalArgumentException("Input count must be greater than zero");
        }

        this.mapping = mapping.clone();
        this.inputCount = inputCount;
        this.outputCount = this.mapping.length;

        for (final int m : this.mapping) {
            if (m < 0 || m >= this.inputCount) {
                throw new IllegalArgumentException(
                        "Mapping index " + m + " is out of bounds for input count " + this.inputCount
                );
            }
        }
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

        for (int i = 0; i < outputCount; i++) {
            outputs[outputOffset + i] = localInputs[mapping[i]];
        }
    }

    @Override
    public boolean hasInverse() {
        if (inputCount != outputCount) {
            return false;
        }

       final boolean[] seen = new boolean[inputCount];
        for (final int m : mapping) {
            if (seen[m]) {
                return false;
            }
            seen[m] = true;
        }
        return true;
    }

    @Override
    public Transform getInverse() {
        if (!hasInverse()) {
            throw new UnsupportedOperationException(
                    "This RemapAxes cannot be analytically inverted (axes were dropped or duplicated)"
            );
        }

        final int[] inverseMapping = new int[inputCount];
        for (int i = 0; i < outputCount; i++) {
            inverseMapping[mapping[i]] = i;
        }

        return new RemapAxes(inverseMapping, inputCount);
    }
}