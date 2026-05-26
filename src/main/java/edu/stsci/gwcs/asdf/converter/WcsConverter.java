package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.Step;
import edu.stsci.gwcs.Wcs;
import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class WcsConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/wcs-1.2.0",
            "tag:stsci.edu:gwcs/wcs-1.4.0"
    );

    public WcsConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Wcs fromAsdfNode(final AsdfNode node) {
        final String name = node.getString("name");

        final AsdfNode stepsNode = node.get("steps");
        final Step[] steps = new Step[stepsNode.size()];
        for (int i = 0; i < steps.length; i++) {
            steps[i] = support().deserializeStep(stepsNode.get((long) i));
        }

        final int[] pixelShape = node.getOptional("pixel_shape").isPresent()
                ? AsdfNodeUtils.readIntArray(node, "pixel_shape")
                : null;

        return new Wcs(name, steps, pixelShape, null);
    }
}
