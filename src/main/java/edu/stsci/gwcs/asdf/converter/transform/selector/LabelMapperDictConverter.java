package edu.stsci.gwcs.asdf.converter.transform.selector;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.selector.LabelMapperDict;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LabelMapperDictConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/label_mapper-1.0.0"
    );

    public LabelMapperDictConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final AsdfNode mapperNode = node.get("mapper");
        final List<Double> labels = mapperNode.getList("labels", Double.class);
        final AsdfNode modelsNode = mapperNode.get("models");

        final Map<Double, Transform> labelMap = new LinkedHashMap<>();
        for (int i = 0; i < labels.size(); i++) {
            final Transform transform = support().deserializeTransform(modelsNode.get((long) i));
            labelMap.put(labels.get(i), transform);
        }

        final double atol = node.getOptional("atol")
                .map(AsdfNode::asDouble)
                .orElse(1e-8);
        final double noLabel = node.getOptional("no_label")
                .map(AsdfNode::asDouble)
                .orElse(Double.NaN);

        final Transform transform = new LabelMapperDict(labelMap, atol, noLabel);
        return transform;
    }
}
