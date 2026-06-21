package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.SellmeierGlass;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class SellmeierGlassConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/sellmeier_glass-1.0.0",
            "tag:stsci.edu:gwcs/sellmeier_glass-1.1.0",
            "tag:stsci.edu:gwcs/sellmeier_glass-1.2.0",
            "tag:stsci.edu:gwcs/sellmeier_glass-1.3.0"
    );

    public SellmeierGlassConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double[] bCoefficients = AsdfNodeUtils.readDoubleArray(node, "B_coef");
        final double[] cCoefficients = AsdfNodeUtils.readDoubleArray(node, "C_coef");
        return new SellmeierGlass(bCoefficients, cCoefficients);
    }
}
