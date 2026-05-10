package edu.stsci.gwcs.transform;

public class Snell3D implements Transform {

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
        final double n = inputs[inputOffset];
        if (n == 0) {
            throw new IllegalArgumentException("Refractive index must not be zero");
        }
        final double alphaIn = inputs[inputOffset + 1];
        final double betaIn = inputs[inputOffset + 2];

        final double alphaOut = alphaIn / n;
        final double betaOut = betaIn / n;
        final double gammaOut = Math.sqrt(1.0 - alphaOut * alphaOut - betaOut * betaOut);

        outputs[outputOffset] = alphaOut;
        outputs[outputOffset + 1] = betaOut;
        outputs[outputOffset + 2] = gammaOut;
    }
}
