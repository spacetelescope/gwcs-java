package edu.stsci.gwcs.frame;

import lombok.NonNull;

public class CelestialFrame extends Frame2D {
    private final String referenceFrame;

    public CelestialFrame(@NonNull final String name,
                          @NonNull final String[] axisNames,
                          @NonNull final int[] axisOrder,
                          @NonNull final String[] axisPhysicalTypes,
                          @NonNull final String[] units,
                          @NonNull final String referenceFrame) {
        super(name, axisNames, axisOrder, axisPhysicalTypes, units);
        this.referenceFrame = referenceFrame;
    }

    public String getReferenceFrame() {
        return referenceFrame;
    }
}
