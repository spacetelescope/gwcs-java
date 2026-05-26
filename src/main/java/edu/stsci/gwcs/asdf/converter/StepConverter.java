package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.Step;
import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.frame.Frame;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Optional;
import java.util.Set;

public class StepConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/step-1.1.0",
            "tag:stsci.edu:gwcs/step-1.3.0"
    );

    public StepConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Step fromAsdfNode(final AsdfNode node) {
        final Frame frame = support().deserializeFrame(node.get("frame"));

        final Optional<AsdfNode> transformNode = node.getOptional("transform");
        final Transform transform = transformNode
                .map(n -> support().deserializeTransform(n))
                .orElse(null);

        return new Step(frame, transform);
    }
}
