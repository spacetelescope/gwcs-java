package edu.stsci.gwcs.asdf.converter.transform.geometry;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.geometry.FromDirectionCosines;
import edu.stsci.gwcs.transform.geometry.ToDirectionCosines;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class DirectionCosinesConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/direction_cosines-1.0.0",
            "tag:stsci.edu:gwcs/direction_cosines-1.1.0",
            "tag:stsci.edu:gwcs/direction_cosines-1.2.0",
            "tag:stsci.edu:gwcs/direction_cosines-1.3.0"
    );

    public DirectionCosinesConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final String transformType = node.getString("transform_type");

        final Transform transform;
        if ("to_direction_cosines".equals(transformType)) {
            transform = new ToDirectionCosines();
        } else if ("from_direction_cosines".equals(transformType)) {
            transform = new FromDirectionCosines();
        } else {
            throw new IllegalArgumentException("Unknown direction_cosines transform_type: " + transformType);
        }
        return transform;
    }
}
