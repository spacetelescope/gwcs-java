package edu.stsci.gwcs.asdf.converter.transform.functional;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Shift;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class ShiftConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/shift-1.2.0",
            "tag:stsci.edu:asdf/transform/shift-1.3.0"
    );

    public ShiftConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double offset = node.getDouble("offset");
        final Transform transform = new Shift(offset);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
