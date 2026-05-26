package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.EulerAngleRotation;
import edu.stsci.gwcs.transform.rotation.RotateCelestial2Native;
import edu.stsci.gwcs.transform.rotation.RotateNative2Celestial;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class Rotate3DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/rotate3d-1.3.0",
            "tag:stsci.edu:asdf/transform/rotate3d-1.4.0"
    );

    public Rotate3DConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final String direction = node.getString("direction");

        final Transform transform;
        if ("native2celestial".equals(direction)) {
            transform = new RotateNative2Celestial(
                    node.getDouble("phi"),
                    node.getDouble("theta"),
                    node.getDouble("psi")
            );
        } else if ("celestial2native".equals(direction)) {
            transform = new RotateCelestial2Native(
                    node.getDouble("phi"),
                    node.getDouble("theta"),
                    node.getDouble("psi")
            );
        } else {
            transform = new EulerAngleRotation(
                    node.getDouble("phi"),
                    node.getDouble("theta"),
                    node.getDouble("psi"),
                    direction
            );
        }
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
