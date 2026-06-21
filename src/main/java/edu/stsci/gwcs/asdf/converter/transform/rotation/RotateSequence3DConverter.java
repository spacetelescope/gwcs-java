package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.RotateSequence3D;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class RotateSequence3DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/rotate_sequence_3d-1.1.0",
            "tag:stsci.edu:asdf/transform/rotate_sequence_3d-1.2.0"
    );

    public RotateSequence3DConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final String rotationType = node.getString("rotation_type");
        if (!"cartesian".equals(rotationType)) {
            throw new IllegalArgumentException("Unsupported rotation_type: " + rotationType);
        }

        final double[] angles = AsdfNodeUtils.readDoubleArray(node, "angles");
        final String axesOrder = node.getString("axes_order");
        return new RotateSequence3D(angles, axesOrder);
    }
}
