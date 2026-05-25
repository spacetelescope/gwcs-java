package edu.stsci.gwcs.frame;

public interface Frame {
    String getName();

    int getAxisCount();

    int[] getAxisOrder();

    String[] getAxisNames();

    String[] getAxisPhysicalTypes();

    String[] getUnits();
}
