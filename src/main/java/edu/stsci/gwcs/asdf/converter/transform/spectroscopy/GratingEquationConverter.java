package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.AnglesFromGratingEquation3D;
import edu.stsci.gwcs.transform.spectroscopy.WavelengthFromGratingEquation;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class GratingEquationConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/grating_equation-1.0.0"
    );

    public GratingEquationConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double grooveDensity = node.getDouble("groove_density");
        final int order = node.getInt("order");
        final String output = node.getString("output");

        final Transform transform;
        if ("angle".equals(output)) {
            transform = new AnglesFromGratingEquation3D(grooveDensity, order);
        } else if ("wavelength".equals(output)) {
            transform = new WavelengthFromGratingEquation(grooveDensity, order);
        } else {
            throw new IllegalArgumentException("Unknown grating_equation output: " + output);
        }
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
