package edu.stsci.gwcs.transform.geometry;

import edu.stsci.gwcs.transform.Transform;

/**
 * Converts tangent-plane coordinates {@code (x, y)} to direction cosines.
 *
 * <p>The third input (z) is accepted for pipeline compatibility but ignored;
 * the optical-axis component is always assumed to be 1.0.
 */
public class ToDirectionCosines implements Transform {

    @Override
    public int getInputCount() {
        return 3;
    }

    @Override
    public int getOutputCount() {
        return 4;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];
        final double y = inputs[inputOffset + 1];

        final double vabs = Math.sqrt(1.0 + x * x + y * y);

        outputs[outputOffset] = x / vabs;
        outputs[outputOffset + 1] = y / vabs;
        outputs[outputOffset + 2] = 1.0 / vabs;
        outputs[outputOffset + 3] = vabs;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new FromDirectionCosines();
    }
}
