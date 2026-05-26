package edu.stsci.gwcs.asdf.converter.transform.geometry;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.geometry.CartesianToSpherical;
import edu.stsci.gwcs.transform.geometry.SphericalToCartesian;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class SphericalCartesianConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/spherical_cartesian-1.0.0",
            "tag:stsci.edu:gwcs/spherical_cartesian-1.3.0"
    );

    public SphericalCartesianConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final String transformType = node.getString("transform_type");
        final int wrapLonAt = node.getOptional("wrap_lon_at")
                .map(AsdfNode::asInt)
                .orElse(360);

        final Transform transform;
        if ("spherical_to_cartesian".equals(transformType)) {
            transform = new SphericalToCartesian(wrapLonAt);
        } else if ("cartesian_to_spherical".equals(transformType)) {
            transform = new CartesianToSpherical(wrapLonAt);
        } else {
            throw new IllegalArgumentException("Unknown spherical_cartesian transform_type: " + transformType);
        }
        return transform;
    }
}
