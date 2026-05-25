package edu.stsci.gwcs.asdf.converter.transform.polynomial;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.polynomial.Polynomial1D;
import edu.stsci.gwcs.transform.polynomial.Polynomial2D;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Optional;
import java.util.Set;

public class PolynomialConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/polynomial-1.2.0"
    );

    public PolynomialConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final NdArray<?> coefficientsNd = node.getNdArray("coefficients");

        if (coefficientsNd.getShape().getRank() == 2) {
            return deserializePolynomial2D(coefficientsNd, node);
        }

        return deserializePolynomial1D(coefficientsNd, node);
    }

    private Transform deserializePolynomial1D(final NdArray<?> coefficientsNd, final AsdfNode node) {
        final int size = coefficientsNd.getShape().get(0);
        final double[] coefficients = coefficientsNd.toArray(new double[size]);

        final double[] domain = readOptionalDoublePair(node, "domain");
        final double[] window = readOptionalDoublePair(node, "window");

        final Transform transform = new Polynomial1D(coefficients, domain, window);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }

    private Transform deserializePolynomial2D(final NdArray<?> coefficientsNd, final AsdfNode node) {
        final int n = coefficientsNd.getShape().get(0);
        final double[][] coefficients = coefficientsNd.toArray(new double[n][n]);

        final double[] xDomain = readOptionalDomainElement(node, "domain", 0);
        final double[] yDomain = readOptionalDomainElement(node, "domain", 1);
        final double[] xWindow = readOptionalDomainElement(node, "window", 0);
        final double[] yWindow = readOptionalDomainElement(node, "window", 1);

        final Transform transform = new Polynomial2D(coefficients, xDomain, yDomain, xWindow, yWindow);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }

    private static double[] readOptionalDoublePair(final AsdfNode node, final String key) {
        final Optional<AsdfNode> optional = node.getOptional(key);
        if (optional.isEmpty()) {
            return null;
        }
        final AsdfNode pairNode = optional.get();
        return new double[]{pairNode.get(0).asDouble(), pairNode.get(1).asDouble()};
    }

    private static double[] readOptionalDomainElement(final AsdfNode node, final String key, final int index) {
        final Optional<AsdfNode> optional = node.getOptional(key);
        if (optional.isEmpty()) {
            return null;
        }
        final AsdfNode element = optional.get().get(index);
        return new double[]{element.get(0).asDouble(), element.get(1).asDouble()};
    }
}
