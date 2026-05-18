package edu.stsci.gwcs.transform.spectroscopy;

import edu.stsci.gwcs.transform.Transform;

/** 3D grating equation solving for diffracted angles. Wavelength in meters; groove density in lines/meter. */
public class AnglesFromGratingEquation3D implements Transform {
    private final double grooveDensity;
    private final int spectralOrder;

    public AnglesFromGratingEquation3D(final double grooveDensity, final int spectralOrder) {
        if (spectralOrder == 0) {
            throw new IllegalArgumentException("Spectral order must not be zero");
        }
        this.grooveDensity = grooveDensity;
        this.spectralOrder = spectralOrder;
    }

    @Override
    public int getInputCount() {
        return 3;
    }

    @Override
    public int getOutputCount() {
        return 3;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double wavelength = inputs[inputOffset];
        final double alphaIn = inputs[inputOffset + 1];
        final double betaIn = inputs[inputOffset + 2];

        final double alphaOut = -grooveDensity * spectralOrder * wavelength + alphaIn;
        final double betaOut = -betaIn;
        final double gammaSquared = 1.0 - alphaOut * alphaOut - betaOut * betaOut;
        final double gammaOut = gammaSquared < 0.0 ? Double.NaN : Math.sqrt(gammaSquared);

        outputs[outputOffset] = alphaOut;
        outputs[outputOffset + 1] = betaOut;
        outputs[outputOffset + 2] = gammaOut;
    }
}
