package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.List;
import java.util.Optional;

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

    public static Transform wrapWithNamedTransform(final Transform transform, final AsdfNode node) {
        final Optional<AsdfNode> nameNode = node.getOptional("name");
        final Optional<AsdfNode> inputsNode = node.getOptional("inputs");
        final Optional<AsdfNode> outputsNode = node.getOptional("outputs");

        if (nameNode.isEmpty() && inputsNode.isEmpty() && outputsNode.isEmpty()) {
            return transform;
        }

        final String name = nameNode.map(AsdfNode::asString).orElse(null);
        final String[] inputs = inputsNode
                .map(n -> n.asList(String.class).toArray(new String[0]))
                .orElse(null);
        final String[] outputs = outputsNode
                .map(n -> n.asList(String.class).toArray(new String[0]))
                .orElse(null);

        return new NamedTransform(transform, name, inputs, outputs);
    }
}
