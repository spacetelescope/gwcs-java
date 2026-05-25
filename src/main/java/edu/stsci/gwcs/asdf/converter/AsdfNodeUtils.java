package edu.stsci.gwcs.asdf.converter;

import org.asdfformat.asdf.node.AsdfNode;

import java.util.List;

class AsdfNodeUtils {
    private AsdfNodeUtils() {
    }

    static String[] readStringArray(final AsdfNode node, final String key) {
        final List<String> list = node.getList(key, String.class);
        return list.toArray(new String[0]);
    }

    static int[] readIntArray(final AsdfNode node, final String key) {
        final List<Integer> list = node.getList(key, Integer.class);
        final int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
