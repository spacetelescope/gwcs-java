package edu.stsci.gwcs.transform.compound;

import lombok.NonNull;
import java.util.Map;
import edu.stsci.gwcs.transform.Transform;

public final class FixInputs implements Transform {
    private final Transform delegate;
    private final int delegateInputCount;
    private final int inputCount;

    private final int[] unfixedTargetIndex;
    private final double[] fixedTemplate;

    public FixInputs(@NonNull final Transform delegate, @NonNull final Map<Integer, Double> fixedInputs) {
        if (fixedInputs.isEmpty()) {
            throw new IllegalArgumentException("fixedInputs must not be empty");
        }

        this.delegateInputCount = delegate.getInputCount();

        if (fixedInputs.size() >= delegateInputCount) {
            throw new IllegalArgumentException(
                    "At least one input must remain unfixed"
            );
        }

        for (final Map.Entry<Integer, Double> entry : fixedInputs.entrySet()) {
            final Integer index = entry.getKey();
            if (index == null || index < 0 || index >= delegateInputCount) {
                throw new IllegalArgumentException(
                        "Fixed input index " + index + " is out of range [0, " + (delegateInputCount - 1) + "]"
                );
            }
            final Double value = entry.getValue();
            if (value == null) {
                throw new IllegalArgumentException(
                        "Fixed input value for index " + index + " must not be null"
                );
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Fixed input value for index " + index + " must be finite, got " + value
                );
            }
        }

        this.delegate = delegate;
        this.inputCount = delegateInputCount - fixedInputs.size();

        this.fixedTemplate = new double[delegateInputCount];
        this.unfixedTargetIndex = new int[inputCount];

        int unfixed = 0;
        for (int i = 0; i < delegateInputCount; i++) {
            final Double fixed = fixedInputs.get(i);
            if (fixed != null) {
                fixedTemplate[i] = fixed;
            } else {
                unfixedTargetIndex[unfixed++] = i;
            }
        }
    }

    @Override
    public int getInputCount() {
        return inputCount;
    }

    @Override
    public int getOutputCount() {
        return delegate.getOutputCount();
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double[] scratch = fixedTemplate.clone();
        for (int i = 0; i < unfixedTargetIndex.length; i++) {
            scratch[unfixedTargetIndex[i]] = inputs[inputOffset + i];
        }

        delegate.evaluate(scratch, 0, outputs, outputOffset);
    }
}
