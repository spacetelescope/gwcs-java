package edu.stsci.gwcs.coordinate;

public interface Frame {
    String getName();

    int getAxisCount();

    int[] getAxisOrder();

    String[] getAxisNames();

    String[] getAxisPhysicalTypes();

    String[] getUnits();
}
