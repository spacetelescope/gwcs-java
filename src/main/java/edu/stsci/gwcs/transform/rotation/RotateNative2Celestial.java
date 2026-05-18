package edu.stsci.gwcs.transform.rotation;

import edu.stsci.gwcs.transform.Transform;

public class RotateNative2Celestial extends AbstractCelestialNativeRotation {

    public RotateNative2Celestial(final double lon, final double lat, final double lonPole) {
        super(lon, lat, lonPole, new EulerAngleRotation(lonPole - 90.0, -(90.0 - lat), -(90.0 + lon), "zxz"));
    }

    @Override
    public Transform getInverse() {
        return new RotateCelestial2Native(getLon(), getLat(), getLonPole());
    }
}
