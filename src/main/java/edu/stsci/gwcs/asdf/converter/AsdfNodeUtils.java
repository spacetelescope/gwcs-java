package edu.stsci.gwcs.asdf.converter;

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
        final List<Integer> list = node.getList(key, Integer.class);
        final int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static double[] readDoubleArray(final AsdfNode node, final String key) {
        final List<Double> list = node.getList(key, Double.class);
        final double[] result = new double[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
