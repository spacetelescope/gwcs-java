package edu.stsci.gwcs.asdf.converter.frame;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.frame.Frame2D;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class Frame2DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/frame2d-1.0.0",
            "tag:stsci.edu:gwcs/frame2d-1.2.0"
    );

    public Frame2DConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Frame2D fromAsdfNode(final AsdfNode node) {
        final String name = node.getString("name");
        final String[] axisNames = AsdfNodeUtils.readStringArray(node, "axes_names");
        final int[] axisOrder = AsdfNodeUtils.readIntArray(node, "axes_order");
        final String[] axisPhysicalTypes = AsdfNodeUtils.readStringArray(node, "axis_physical_types");
        final String[] units = AsdfNodeUtils.readStringArray(node, "unit");
        return new Frame2D(name, axisNames, axisOrder, axisPhysicalTypes, units);
    }
}
