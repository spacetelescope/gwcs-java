package edu.stsci.gwcs.transform.selector;

import java.util.Map;
import edu.stsci.gwcs.transform.Transform;

public class RegionsSelector implements Transform {
    private final Transform labelMapper;
    private final Map<Integer, Transform> selector;
    private final double undefinedTransformValue;
    private final int inputCount;
    private final int outputCount;


    public RegionsSelector(
            final Transform labelMapper,
            final Map<Integer, Transform> selector,
            final double undefinedTransformValue
    ) {
        if (selector == null || selector.isEmpty()) {
            throw new IllegalArgumentException("Selector map must not be empty");
        }
        if (labelMapper.getOutputCount() != 1) {
            throw new IllegalArgumentException("Label mapper must have exactly 1 output");
        }

        int expectedInputCount = -1;
        int expectedOutputCount = -1;
        for (final Transform transform : selector.values()) {
            if (expectedInputCount == -1) {
                expectedInputCount = transform.getInputCount();
                expectedOutputCount = transform.getOutputCount();
            } else {
                if (transform.getInputCount() != expectedInputCount || transform.getOutputCount() != expectedOutputCount) {
                    throw new IllegalArgumentException("All selector transforms must have the same input and output counts");
                }
            }
        }

        if (labelMapper.getInputCount() != expectedInputCount) {
            throw new IllegalArgumentException("Label mapper input count must match selector transforms' input count");
        }

        this.labelMapper = labelMapper;
        this.selector = Map.copyOf(selector);
        this.undefinedTransformValue = undefinedTransformValue;
        this.inputCount = expectedInputCount;
        this.outputCount = expectedOutputCount;
    }

    public RegionsSelector(final Transform labelMapper, final Map<Integer, Transform> selector) {
        this(labelMapper, selector, Double.NaN);
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
        final double[] labelResult = new double[1];
        labelMapper.evaluate(inputs, inputOffset, labelResult, 0);

        final Transform transform;
        if (!Double.isFinite(labelResult[0])) {
            transform = null;
        } else {
            transform = selector.get((int) Math.round(labelResult[0]));
        }
        if (transform != null) {
            transform.evaluate(inputs, inputOffset, outputs, outputOffset);
        } else {
            for (int i = 0; i < outputCount; i++) {
                outputs[outputOffset + i] = undefinedTransformValue;
            }
        }
    }
}
