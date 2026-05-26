package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.SellmeierZemax;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class SellmeierZemaxConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/sellmeier_zemax-1.0.0"
    );

    public SellmeierZemaxConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final double temperature = node.getDouble("temperature");
        final double refTemperature = node.getDouble("ref_temperature");
        final double refPressure = node.getDouble("ref_pressure");
        final double pressure = node.getDouble("pressure");
        final double[] bCoefficients = AsdfNodeUtils.readDoubleArray(node, "B_coef");
        final double[] cCoefficients = AsdfNodeUtils.readDoubleArray(node, "C_coef");
        final double[] dCoefficients = AsdfNodeUtils.readDoubleArray(node, "D_coef");
        final double[] eCoefficients = AsdfNodeUtils.readDoubleArray(node, "E_coef");
        final Transform transform = new SellmeierZemax(
                temperature, refTemperature, refPressure, pressure,
                bCoefficients, cCoefficients, dCoefficients, eCoefficients
        );
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
