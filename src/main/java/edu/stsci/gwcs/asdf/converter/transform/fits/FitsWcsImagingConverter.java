package edu.stsci.gwcs.asdf.converter.transform.fits;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.DelegatingTransform;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.fits.FitsWcsImaging;
import edu.stsci.gwcs.transform.projection.Projection;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class FitsWcsImagingConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/fitswcs_imaging-1.0.0"
    );

    public FitsWcsImagingConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final NdArray<?> crpixNd = node.getNdArray("crpix");
        final NdArray<?> crvalNd = node.getNdArray("crval");
        final NdArray<?> cdeltNd = node.getNdArray("cdelt");
        final NdArray<?> pcNd = node.getNdArray("pc");

        final int n = crpixNd.getShape().get(0);
        final double[] crpix = crpixNd.toArray(new double[n]);
        final double[] crval = crvalNd.toArray(new double[n]);
        final double[] cdelt = cdeltNd.toArray(new double[n]);
        final int pcRows = pcNd.getShape().get(0);
        final int pcCols = pcNd.getShape().get(1);
        final double[][] pc = pcNd.toArray(new double[pcRows][pcCols]);

        // ASDF stores crpix in FITS 1-based convention; FitsWcsImaging uses 0-based
        for (int i = 0; i < crpix.length; i++) {
            crpix[i] -= 1;
        }

        final AsdfNode projectionNode = node.get("projection");
        final Projection projection = unwrapProjection(support().deserializeTransform(projectionNode));

        return AsdfNodeUtils.wrapWithNamedTransform(
                new FitsWcsImaging(projection, crpix, crval, cdelt, pc), node);
    }

    private static Projection unwrapProjection(Transform transform) {
        while (!(transform instanceof Projection)) {
            if (transform instanceof DelegatingTransform) {
                transform = ((DelegatingTransform) transform).getDelegate();
            } else {
                throw new IllegalArgumentException(
                        "fitswcs_imaging 'projection' must be a Projection, got: " + transform.getClass().getName());
            }
        }
        return (Projection) transform;
    }
}
