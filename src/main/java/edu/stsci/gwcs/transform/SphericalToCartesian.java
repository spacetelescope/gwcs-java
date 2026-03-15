package edu.stsci.gwcs.transform;

public class SphericalToCartesian implements Transform {
    private final boolean wrapLongitude;

    public SphericalToCartesian() {
        this(true);
    }

    public SphericalToCartesian(final boolean wrapLongitude) {
        this.wrapLongitude = wrapLongitude;
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 3;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double longitudeRad = Math.toRadians(inputs[inputOffset]);
        final double latitudeRad = Math.toRadians(inputs[inputOffset + 1]);

        final double cosLatitude = Math.cos(latitudeRad);

        outputs[outputOffset] = cosLatitude * Math.cos(longitudeRad);
        outputs[outputOffset + 1] = cosLatitude * Math.sin(longitudeRad);
        outputs[outputOffset + 2] = Math.sin(latitudeRad);
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new CartesianToSpherical(this.wrapLongitude);
    }
}