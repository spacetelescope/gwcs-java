package edu.stsci.gwcs.transform.rotation;

import edu.stsci.gwcs.transform.Transform;

abstract class AbstractCelestialNativeRotation implements Transform {

    private final double lon;
    private final double lat;
    private final double lonPole;
    private final EulerAngleRotation rotation;

    AbstractCelestialNativeRotation(final double lon, final double lat, final double lonPole,
                                    final EulerAngleRotation rotation) {
        this.lon = lon;
        this.lat = lat;
        this.lonPole = lonPole;
        this.rotation = rotation;
    }

    protected double getLon() {
        return lon;
    }

    protected double getLat() {
        return lat;
    }

    protected double getLonPole() {
        return lonPole;
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        rotation.evaluate(inputs, inputOffset, outputs, outputOffset);
        final double a = outputs[outputOffset] % 360.0;
        outputs[outputOffset] = (a < 0.0 ? a + 360.0 : a) + 0.0;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public abstract Transform getInverse();
}
