package edu.stsci.gwcs.asdf.converter.transform;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.RemapAxes;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class RemapAxesConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/remap_axes-1.3.0",
            "tag:stsci.edu:asdf/transform/remap_axes-1.4.0"
    );

    public RemapAxesConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final int[] mapping = AsdfNodeUtils.readIntArray(node, "mapping");

        int maxIndex = 0;
        for (final int m : mapping) {
            if (m > maxIndex) {
                maxIndex = m;
            }
        }
        final int nInputs = node.getOptional("n_inputs")
                .map(AsdfNode::asInt)
                .orElse(maxIndex + 1);

        return new RemapAxes(mapping, nInputs);
    }
}
