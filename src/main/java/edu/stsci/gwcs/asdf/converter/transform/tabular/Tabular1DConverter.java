package edu.stsci.gwcs.asdf.converter.transform.tabular;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.tabular.Tabular1D;
import edu.stsci.gwcs.transform.tabular.Tabular1D.InterpolationMethod;
import edu.stsci.gwcs.transform.tabular.Tabular1D.OutOfBoundsMode;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Optional;
import java.util.Set;

public class Tabular1DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/tabular-1.2.0"
    );

    public Tabular1DConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final NdArray<?> pointsNd = node.get("points").getNdArray(0L);
        final int pointsSize = pointsNd.getShape().get(0);
        final double[] points = pointsNd.toArray(new double[pointsSize]);

        final NdArray<?> lookupTableNd = node.getNdArray("lookup_table");
        final int valuesSize = lookupTableNd.getShape().get(0);
        final double[] values = lookupTableNd.toArray(new double[valuesSize]);

        final Optional<AsdfNode> boundsErrorNode = node.getOptional("bounds_error");
        final boolean boundsError = boundsErrorNode.map(AsdfNode::asBoolean).orElse(true);
        final OutOfBoundsMode mode = boundsError ? OutOfBoundsMode.ERROR : OutOfBoundsMode.FILL;

        final Optional<AsdfNode> fillValueNode = node.getOptional("fill_value");
        final double fillValue = fillValueNode.map(AsdfNode::asDouble).orElse(Double.NaN);

        return new Tabular1D(points, values, mode, fillValue, InterpolationMethod.LINEAR);
    }
}
