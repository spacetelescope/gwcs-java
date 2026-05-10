package edu.stsci.gwcs.transform;

public class FromDirectionCosines implements Transform {

    @Override
    public int getInputCount() {
        return 4;
    }

    @Override
    public int getOutputCount() {
        return 3;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double cosa = inputs[inputOffset];
        final double cosb = inputs[inputOffset + 1];
        final double cosc = inputs[inputOffset + 2];
        final double length = inputs[inputOffset + 3];

        outputs[outputOffset] = cosa * length;
        outputs[outputOffset + 1] = cosb * length;
        outputs[outputOffset + 2] = cosc * length;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new ToDirectionCosines();
    }
}
