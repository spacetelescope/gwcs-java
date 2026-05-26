package edu.stsci.gwcs.asdf.converter.transform.selector;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.selector.RegionsSelector;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionsSelectorConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/regions_selector-1.0.0"
    );

    public RegionsSelectorConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final Transform labelMapper = support().deserializeTransform(node.get("label_mapper"));

        final AsdfNode selectorNode = node.get("selector");
        final List<Integer> labels = selectorNode.getList("labels", Integer.class);
        final AsdfNode transformsNode = selectorNode.get("transforms");

        final Map<Integer, Transform> selector = new LinkedHashMap<>();
        for (int i = 0; i < labels.size(); i++) {
            final Transform transform = support().deserializeTransform(transformsNode.get((long) i));
            selector.put(labels.get(i), transform);
        }

        final double undefinedTransformValue = node.getOptional("undefined_transform_value")
                .map(AsdfNode::asDouble)
                .orElse(Double.NaN);

        final Transform transform = new RegionsSelector(labelMapper, selector, undefinedTransformValue);
        return transform;
    }
}
