package edu.stsci.gwcs.transform.rotation;

import edu.stsci.gwcs.transform.Transform;

public class RotateCelestial2Native extends AbstractCelestialNativeRotation {

    public RotateCelestial2Native(final double lon, final double lat, final double lonPole) {
        super(lon, lat, lonPole, new EulerAngleRotation(90.0 + lon, 90.0 - lat, -(lonPole - 90.0), "zxz"));
    }

    @Override
    public Transform getInverse() {
        return new RotateNative2Celestial(getLon(), getLat(), getLonPole());
    }
}
