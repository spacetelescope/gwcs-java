package edu.stsci.gwcs.transform;

public class WavelengthFromGratingEquation implements Transform {
    private final double grooveDensity;
    private final int spectralOrder;

    public WavelengthFromGratingEquation(final double grooveDensity, final int spectralOrder) {
        if (spectralOrder == 0) {
            throw new IllegalArgumentException("Spectral order must not be zero");
        }
        if (grooveDensity == 0) {
            throw new IllegalArgumentException("Groove density must not be zero");
        }
        this.grooveDensity = grooveDensity;
        this.spectralOrder = spectralOrder;
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 1;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double alphaIn = inputs[inputOffset];
        final double alphaOut = inputs[inputOffset + 1];
        outputs[outputOffset] = (alphaIn + alphaOut) / (grooveDensity * spectralOrder);
    }
}
