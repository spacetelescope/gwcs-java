package edu.stsci.gwcs.transform.geometry;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

public class CartesianToSpherical implements Transform {

    private final int wrapLonAt;

    public CartesianToSpherical() {
        this(360);
    }

    public CartesianToSpherical(final int wrapLonAt) {
        if (wrapLonAt != 180 && wrapLonAt != 360) {
            throw new IllegalArgumentException("wrapLonAt must be 180 or 360, got " + wrapLonAt);
        }
        this.wrapLonAt = wrapLonAt;
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

        final double r = Math.hypot(x, y);

        double longitudeDeg = WcsMath.atan2d(y, x);
        final double latitudeDeg = WcsMath.atan2d(z, r);

        if (wrapLonAt == 360) {
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
        return new SphericalToCartesian(this.wrapLonAt);
    }
}
