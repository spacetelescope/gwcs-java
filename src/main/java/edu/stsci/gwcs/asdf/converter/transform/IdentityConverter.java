package edu.stsci.gwcs.asdf.converter.transform;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Identity;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class IdentityConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/identity-1.2.0",
            "tag:stsci.edu:asdf/transform/identity-1.3.0"
    );

    public IdentityConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final int nDims = node.getOptional("n_dims")
                .map(AsdfNode::asInt)
                .orElse(1);
        return new Identity(nDims);
    }
}
