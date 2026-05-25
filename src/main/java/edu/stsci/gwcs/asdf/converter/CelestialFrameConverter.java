package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.coordinate.CelestialFrame;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class CelestialFrameConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/celestial_frame-1.0.0",
            "tag:stsci.edu:gwcs/celestial_frame-1.2.0"
    );

    public CelestialFrameConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public CelestialFrame fromAsdfNode(final AsdfNode node) {
        final String name = node.getString("name");
        final String[] axisNames = AsdfNodeUtils.readStringArray(node, "axes_names");
        final int[] axisOrder = AsdfNodeUtils.readIntArray(node, "axes_order");
        final String[] axisPhysicalTypes = AsdfNodeUtils.readStringArray(node, "axis_physical_types");
        final String[] units = AsdfNodeUtils.readStringArray(node, "unit");
        final String referenceFrame = parseReferenceFrame(node.get("reference_frame").getTag());
        return new CelestialFrame(name, axisNames, axisOrder, axisPhysicalTypes, units, referenceFrame);
    }

    static String parseReferenceFrame(final String tag) {
        final String segment = tag.substring(tag.lastIndexOf('/') + 1);
        final int dashIndex = segment.lastIndexOf('-');
        if (dashIndex < 0) {
            throw new IllegalArgumentException("Cannot parse reference frame from tag: " + tag);
        }
        return segment.substring(0, dashIndex).toUpperCase();
    }
}
