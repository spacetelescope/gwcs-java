package edu.stsci.gwcs.coordinate;

import lombok.NonNull;

public class Frame2D implements Frame {
    private final String name;
    private final String[] axisNames;
    private final int[] axisOrder;
    private final String[] axisPhysicalTypes;
    private final String[] units;

    public Frame2D(@NonNull final String name,
                   @NonNull final String[] axisNames,
                   @NonNull final int[] axisOrder,
                   @NonNull final String[] axisPhysicalTypes,
                   @NonNull final String[] units) {
        if (axisNames.length != 2) {
            throw new IllegalArgumentException("axisNames must have exactly 2 elements");
        }
        if (axisOrder.length != 2) {
            throw new IllegalArgumentException("axisOrder must have exactly 2 elements");
        }
        if (axisPhysicalTypes.length != 2) {
            throw new IllegalArgumentException("axisPhysicalTypes must have exactly 2 elements");
        }
        if (units.length != 2) {
            throw new IllegalArgumentException("units must have exactly 2 elements");
        }
        if (axisOrder[0] < 0 || axisOrder[1] < 0) {
            throw new IllegalArgumentException("axisOrder values must be non-negative");
        }
        if (axisOrder[0] == axisOrder[1]) {
            throw new IllegalArgumentException("axisOrder values must be unique");
        }

        this.name = name;
        this.axisOrder = axisOrder.clone();
        this.axisNames = axisNames.clone();
        this.axisPhysicalTypes = axisPhysicalTypes.clone();
        this.units = units.clone();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAxisCount() {
        return 2;
    }

    @Override
    public int[] getAxisOrder() {
        return axisOrder.clone();
    }

    @Override
    public String[] getAxisNames() {
        return axisNames.clone();
    }

    @Override
    public String[] getAxisPhysicalTypes() {
        return axisPhysicalTypes.clone();
    }

    @Override
    public String[] getUnits() {
        return units.clone();
    }

}
