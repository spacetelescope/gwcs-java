package edu.stsci.gwcs.transform;

import java.util.Map;
import java.util.TreeMap;

public class LabelMapperDict implements Transform {
    private final double[] keys;
    private final int[] labels;
    private final double tolerance;
    private final double noLabel;

    public LabelMapperDict(final Map<Double, Integer> labelMap, final double tolerance, final double noLabel) {
        this.tolerance = tolerance;
        this.noLabel = noLabel;

        final TreeMap<Double, Integer> sorted = new TreeMap<>(labelMap);
        this.keys = new double[sorted.size()];
        this.labels = new int[sorted.size()];
        int i = 0;
        for (final Map.Entry<Double, Integer> entry : sorted.entrySet()) {
            keys[i] = entry.getKey();
            labels[i] = entry.getValue();
            i++;
        }
    }

    public LabelMapperDict(final Map<Double, Integer> labelMap) {
        this(labelMap, 1e-8, Double.NaN);
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
        final double input = inputs[inputOffset];
        for (int i = 0; i < keys.length; i++) {
            if (Math.abs(input - keys[i]) <= tolerance) {
                outputs[outputOffset] = labels[i];
                return;
            }
        }
        outputs[outputOffset] = noLabel;
    }
}
