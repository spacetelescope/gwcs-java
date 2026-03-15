package edu.stsci.gwcs.transform;

public class CartesianToSpherical implements Transform {

    private final boolean wrapLongitude;

    public CartesianToSpherical(final boolean wrapLongitude) {
        this.wrapLongitude = wrapLongitude;
    }

    @Override
    public int getInputCount() {
        return 3;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];
        final double y = inputs[inputOffset + 1];
        final double z = inputs[inputOffset + 2];

        final double r2 = (x * x) + (y * y);

        final double longitudeRad = Math.atan2(y, x);
        final double latitudeRad = Math.atan2(z, Math.sqrt(r2));

        double longitudeDeg = Math.toDegrees(longitudeRad);
        final double latitudeDeg = Math.toDegrees(latitudeRad);

        if (wrapLongitude) {
            longitudeDeg = longitudeDeg % 360.0;
            if (longitudeDeg < 0.0) {
                longitudeDeg += 360.0;
            }
        }

        outputs[outputOffset] = longitudeDeg;
        outputs[outputOffset + 1] = latitudeDeg;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new SphericalToCartesian(this.wrapLongitude);
    }
}
