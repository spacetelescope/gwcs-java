package edu.stsci.gwcs.transform.selector;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import edu.stsci.gwcs.transform.Transform;

public class LabelMapperDict implements Transform {
    private final double[] keys;
    private final Transform[] transforms;
    private final double tolerance;
    private final double noLabel;
    private final int inputCount;
    private final int outputCount;

    public LabelMapperDict(final Map<Double, Transform> labelMap, final double tolerance, final double noLabel) {
        if (!(tolerance >= 0.0)) {
            throw new IllegalArgumentException("Tolerance must be non-negative, got " + tolerance);
        }
        if (labelMap.isEmpty()) {
            throw new IllegalArgumentException("Label map must not be empty");
        }
        final Transform first = labelMap.values().iterator().next();
        this.inputCount = first.getInputCount();
        this.outputCount = first.getOutputCount();
        for (final Map.Entry<Double, Transform> entry : labelMap.entrySet()) {
            final Transform t = entry.getValue();
            if (t.getInputCount() != inputCount || t.getOutputCount() != outputCount) {
                throw new IllegalArgumentException(
                        "All label mapper transforms must have the same input and output counts, but transform at key "
                                + entry.getKey() + " has " + t.getInputCount() + " inputs and " + t.getOutputCount()
                                + " outputs (expected " + inputCount + " inputs and " + outputCount + " outputs)"
                );
            }
        }
        this.tolerance = tolerance;
        this.noLabel = noLabel;

        final TreeMap<Double, Transform> sorted = new TreeMap<>(labelMap);
        this.keys = new double[sorted.size()];
        this.transforms = new Transform[sorted.size()];
        int i = 0;
        for (final Map.Entry<Double, Transform> entry : sorted.entrySet()) {
            keys[i] = entry.getKey();
            transforms[i] = entry.getValue();
            i++;
        }
    }

    public LabelMapperDict(final Map<Double, Transform> labelMap) {
        this(labelMap, 1e-8, Double.NaN);
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
        final double input = inputs[inputOffset];
        if (Double.isNaN(input)) {
            for (int i = 0; i < outputCount; i++) {
                outputs[outputOffset + i] = noLabel;
            }
            return;
        }
        int idx = Arrays.binarySearch(keys, input);
        if (idx >= 0) {
            transforms[idx].evaluate(inputs, inputOffset, outputs, outputOffset);
            return;
        }
        final int insertionPoint = -idx - 1;
        int bestIdx = -1;
        double bestDist = Double.MAX_VALUE;
        if (insertionPoint < keys.length) {
            final double dist = Math.abs(input - keys[insertionPoint]);
            if (dist <= tolerance) {
                bestIdx = insertionPoint;
                bestDist = dist;
            }
        }
        if (insertionPoint > 0) {
            final double dist = Math.abs(input - keys[insertionPoint - 1]);
            if (dist <= tolerance && dist < bestDist) {
                bestIdx = insertionPoint - 1;
            }
        }
        if (bestIdx >= 0) {
            transforms[bestIdx].evaluate(inputs, inputOffset, outputs, outputOffset);
        } else {
            for (int i = 0; i < outputCount; i++) {
                outputs[outputOffset + i] = noLabel;
            }
        }
    }
}
