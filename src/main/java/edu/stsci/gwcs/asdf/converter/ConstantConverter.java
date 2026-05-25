package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Constant;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class ConstantConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/constant-1.4.0",
            "tag:stsci.edu:asdf/transform/constant-1.5.0"
    );

    public ConstantConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double value = node.getDouble("value");
        final int dimensions = node.getInt("dimensions");
        final Transform transform = new Constant(dimensions, value);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
