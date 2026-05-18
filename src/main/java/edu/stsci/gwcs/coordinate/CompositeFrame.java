package edu.stsci.gwcs.coordinate;

import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

public class CompositeFrame implements Frame {
    private final String name;
    private final Frame[] frames;
    private final int axisCount;
    private final int[] axisOrder;
    private final String[] axisNames;
    private final String[] axisPhysicalTypes;
    private final String[] units;

    public CompositeFrame(@NonNull final String name, @NonNull final Frame[] frames) {
        if (frames.length == 0) {
            throw new IllegalArgumentException("CompositeFrame requires at least one sub-frame");
        }

        this.name = name;
        this.frames = frames.clone();
        this.axisCount = computeAxisCount(frames);

        final int[] concatenatedOrder = new int[axisCount];
        final String[] concatenatedNames = new String[axisCount];
        final String[] concatenatedPhysicalTypes = new String[axisCount];
        final String[] concatenatedUnits = new String[axisCount];

        final Set<Integer> seenPositions = new HashSet<>();
        int offset = 0;
        for (final Frame frame : frames) {
            final int[] frameOrder = frame.getAxisOrder();
            for (final int position : frameOrder) {
                if (!seenPositions.add(position)) {
                    throw new IllegalArgumentException(
                            "Overlapping axisOrder value: " + position
                    );
                }
            }
            System.arraycopy(frameOrder, 0, concatenatedOrder, offset, frame.getAxisCount());
            System.arraycopy(frame.getAxisNames(), 0, concatenatedNames, offset, frame.getAxisCount());
            System.arraycopy(frame.getAxisPhysicalTypes(), 0, concatenatedPhysicalTypes, offset, frame.getAxisCount());
            System.arraycopy(frame.getUnits(), 0, concatenatedUnits, offset, frame.getAxisCount());
            offset += frame.getAxisCount();
        }

        for (int i = 0; i < axisCount; i++) {
            if (!seenPositions.contains(i)) {
                throw new IllegalArgumentException(
                        "axisOrder values must form a contiguous range [0, " + (axisCount - 1)
                                + "] but value " + i + " is missing"
                );
            }
        }

        final int[] sortIndices = sortedIndices(concatenatedOrder);
        this.axisOrder = reorder(concatenatedOrder, sortIndices);
        this.axisNames = reorder(concatenatedNames, sortIndices);
        this.axisPhysicalTypes = reorder(concatenatedPhysicalTypes, sortIndices);
        this.units = reorder(concatenatedUnits, sortIndices);
    }

    private static int computeAxisCount(final Frame[] frames) {
        int total = 0;
        for (final Frame frame : frames) {
            total += frame.getAxisCount();
        }
        return total;
    }

    private static int[] sortedIndices(final int[] values) {
        final int[] indices = new int[values.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        for (int i = 0; i < indices.length - 1; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                if (values[indices[j]] < values[indices[i]]) {
                    final int temp = indices[i];
                    indices[i] = indices[j];
                    indices[j] = temp;
                }
            }
        }
        return indices;
    }

    private static int[] reorder(final int[] values, final int[] indices) {
        final int[] result = new int[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = values[indices[i]];
        }
        return result;
    }

    private static String[] reorder(final String[] values, final int[] indices) {
        final String[] result = new String[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = values[indices[i]];
        }
        return result;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAxisCount() {
        return axisCount;
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

    public Frame[] getFrames() {
        return frames.clone();
    }
}
