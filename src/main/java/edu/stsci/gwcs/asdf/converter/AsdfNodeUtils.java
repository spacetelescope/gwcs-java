package edu.stsci.gwcs.asdf.converter;

import org.asdfformat.asdf.ndarray.DoubleNdArray;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.List;

public class AsdfNodeUtils {
    private AsdfNodeUtils() {
    }

    public static String[] readStringArray(final AsdfNode node, final String key) {
        final List<String> list = node.getList(key, String.class);
        return list.toArray(new String[0]);
    }

    public static int[] readIntArray(final AsdfNode node, final String key) {
        final AsdfNode child = node.get(key);
        if (child.isNdArray()) {
            final DoubleNdArray ndArray = child.asNdArray().asDoubleNdArray();
            final int length = ndArray.getShape().get(0);
            final int[] result = new int[length];
            for (int i = 0; i < length; i++) {
                result[i] = (int) ndArray.get(i);
            }
            return result;
        }
        final List<Integer> list = child.asList(Integer.class);
        final int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static double[] readDoubleArray(final AsdfNode node, final String key) {
        final AsdfNode child = node.get(key);
        if (child.isNdArray()) {
            final DoubleNdArray ndArray = child.asNdArray().asDoubleNdArray();
            final int length = ndArray.getShape().get(0);
            final double[] result = new double[length];
            for (int i = 0; i < length; i++) {
                result[i] = ndArray.get(i);
            }
            return result;
        }
        final List<Double> list = child.asList(Double.class);
        final double[] result = new double[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
