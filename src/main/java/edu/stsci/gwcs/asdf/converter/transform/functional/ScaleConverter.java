package edu.stsci.gwcs.asdf.converter.transform.functional;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Scale;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class ScaleConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/scale-1.2.0",
            "tag:stsci.edu:asdf/transform/scale-1.3.0"
    );

    public ScaleConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double factor = node.getDouble("factor");
        return new Scale(factor);
    }
}
