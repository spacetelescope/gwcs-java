package edu.stsci.gwcs.transform.geometry;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

public class SphericalToCartesian implements Transform {
    private final int wrapLonAt;

    public SphericalToCartesian() {
        this(360);
    }

    public SphericalToCartesian(final int wrapLonAt) {
        if (wrapLonAt != 180 && wrapLonAt != 360) {
            throw new IllegalArgumentException("wrapLonAt must be 180 or 360, got " + wrapLonAt);
        }
        this.wrapLonAt = wrapLonAt;
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
        final double longitudeDeg = inputs[inputOffset];
        final double latitudeDeg = inputs[inputOffset + 1];

        final double cosLatitude = WcsMath.cosd(latitudeDeg);

        outputs[outputOffset] = cosLatitude * WcsMath.cosd(longitudeDeg);
        outputs[outputOffset + 1] = cosLatitude * WcsMath.sind(longitudeDeg);
        outputs[outputOffset + 2] = WcsMath.sind(latitudeDeg);
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new CartesianToSpherical(this.wrapLonAt);
    }
}