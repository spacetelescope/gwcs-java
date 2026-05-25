package edu.stsci.gwcs.asdf.converter.transform.functional;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Affine;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class AffineConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/affine-1.3.0",
            "tag:stsci.edu:asdf/transform/affine-1.4.0"
    );

    public AffineConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final NdArray<?> matrixNd = node.getNdArray("matrix");
        final NdArray<?> translationNd = node.getNdArray("translation");

        final int n = matrixNd.getShape().get(0);
        final double[][] matrix = matrixNd.toArray(new double[n][n]);
        final double[] translation = translationNd.toArray(new double[n]);

        final Transform transform = new Affine(matrix, translation);
        return AsdfNodeUtils.wrapWithNamedTransform(transform, node);
    }
}
