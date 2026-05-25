package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.RotateSequence3D;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.List;
import java.util.Set;

public class RotateSequence3DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/rotate_sequence_3d-1.1.0"
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

        final List<Double> anglesList = node.getList("angles", Double.class);
        final double[] angles = new double[anglesList.size()];
        for (int i = 0; i < angles.length; i++) {
            angles[i] = anglesList.get(i);
        }
        final String axesOrder = node.getString("axes_order");
        final Transform transform = new RotateSequence3D(angles, axesOrder);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
