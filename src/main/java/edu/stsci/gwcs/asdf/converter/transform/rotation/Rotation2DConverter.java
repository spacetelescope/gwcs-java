package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.Rotation2D;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class Rotation2DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/rotate2d-1.3.0",
            "tag:stsci.edu:asdf/transform/rotate2d-1.4.0"
    );

    public Rotation2DConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double angle = node.getDouble("angle");
        final Transform transform = new Rotation2D(angle);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
