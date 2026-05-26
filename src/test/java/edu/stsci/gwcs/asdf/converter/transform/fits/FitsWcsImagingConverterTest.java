package edu.stsci.gwcs.asdf.converter.transform.fits;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.fits.FitsWcsImaging;
import edu.stsci.gwcs.transform.projection.zenithal.Gnomonic;
import edu.stsci.gwcs.transform.projection.zenithal.Stereographic;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.ndarray.Shape;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FitsWcsImagingConverterTest {

    private final GwcsAsdfSupport support = new GwcsAsdfSupport();

    private static AsdfNode createProjectionNode(final String tagName, final String direction) {
        final AsdfNode dirNode = mock(AsdfNode.class);
        when(dirNode.asString()).thenReturn(direction);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/" + tagName);
        doReturn(Optional.of(dirNode)).when(node).getOptional("direction");
        doReturn(Optional.empty()).when(node).getOptional("name");
        doReturn(Optional.empty()).when(node).getOptional("inputs");
        doReturn(Optional.empty()).when(node).getOptional("outputs");
        return node;
    }

    private static NdArray<?> mockNdArray1D(final double[] values) {
        final NdArray<?> ndArray = mock(NdArray.class);
        final Shape shape = mock(Shape.class);
        when(shape.get(0)).thenReturn(values.length);
        doReturn(shape).when(ndArray).getShape();
        doReturn(values).when(ndArray).toArray(any(double[].class));
        return ndArray;
    }

    private static NdArray<?> mockNdArray2D(final double[][] values) {
        final NdArray<?> ndArray = mock(NdArray.class);
        final Shape shape = mock(Shape.class);
        when(shape.get(0)).thenReturn(values.length);
        when(shape.get(1)).thenReturn(values[0].length);
        doReturn(shape).when(ndArray).getShape();
        doReturn(values).when(ndArray).toArray(any(double[][].class));
        return ndArray;
    }

    private static AsdfNode createFitsWcsImagingNode(
            final double[] crpix, final double[] crval, final double[] cdelt,
            final double[][] pc, final AsdfNode projectionNode) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/fitswcs_imaging-1.0.0");

        doReturn(mockNdArray1D(crpix)).when(node).getNdArray("crpix");
        doReturn(mockNdArray1D(crval)).when(node).getNdArray("crval");
        doReturn(mockNdArray1D(cdelt)).when(node).getNdArray("cdelt");
        doReturn(mockNdArray2D(pc)).when(node).getNdArray("pc");
        doReturn(projectionNode).when(node).get("projection");

        doReturn(Optional.empty()).when(node).getOptional("name");
        doReturn(Optional.empty()).when(node).getOptional("inputs");
        doReturn(Optional.empty()).when(node).getOptional("outputs");
        return node;
    }

    @Test
    void deserializeFitsWcsImaging_crpixAdjustedToZeroBased() {
        final AsdfNode projNode = createProjectionNode("gnomonic-1.2.0", "pix2sky");
        final AsdfNode node = createFitsWcsImagingNode(
                new double[]{2048.0, 2048.0},
                new double[]{45.0, 30.0},
                new double[]{1e-5, 1e-5},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}},
                projNode);

        final Transform transform = support.deserializeTransform(node);
        assertInstanceOf(FitsWcsImaging.class, transform);

        final FitsWcsImaging expected = new FitsWcsImaging(
                new Gnomonic(),
                new double[]{2047.0, 2047.0},
                new double[]{45.0, 30.0},
                new double[]{1e-5, 1e-5},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}});

        assertArrayEquals(expected.evaluate(2047.0, 2047.0),
                transform.evaluate(2047.0, 2047.0), 1e-12);
    }

    @Test
    void deserializeFitsWcsImaging_withStereographicProjection() {
        final AsdfNode projNode = createProjectionNode("stereographic-1.2.0", "pix2sky");
        final AsdfNode node = createFitsWcsImagingNode(
                new double[]{1024.0, 1024.0},
                new double[]{90.0, 0.0},
                new double[]{-1e-4, 1e-4},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}},
                projNode);

        final Transform transform = support.deserializeTransform(node);
        assertInstanceOf(FitsWcsImaging.class, transform);

        final FitsWcsImaging expected = new FitsWcsImaging(
                new Stereographic(),
                new double[]{1023.0, 1023.0},
                new double[]{90.0, 0.0},
                new double[]{-1e-4, 1e-4},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}});

        assertArrayEquals(expected.evaluate(500.0, 500.0),
                transform.evaluate(500.0, 500.0), 1e-12);
    }

    @Test
    void deserializeFitsWcsImaging_unwrapsNamedTransformFromProjection() {
        final AsdfNode nameNode = mock(AsdfNode.class);
        when(nameNode.asString()).thenReturn("my_gnomonic");

        final AsdfNode dirNode = mock(AsdfNode.class);
        when(dirNode.asString()).thenReturn("pix2sky");

        final AsdfNode projNode = mock(AsdfNode.class);
        when(projNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/gnomonic-1.2.0");
        doReturn(Optional.of(dirNode)).when(projNode).getOptional("direction");
        doReturn(Optional.of(nameNode)).when(projNode).getOptional("name");
        doReturn(Optional.empty()).when(projNode).getOptional("inputs");
        doReturn(Optional.empty()).when(projNode).getOptional("outputs");

        // Verify the projection node deserializes to a NamedTransform wrapping a Gnomonic
        final Transform projTransform = support.deserializeTransform(projNode);
        assertInstanceOf(NamedTransform.class, projTransform);

        final AsdfNode node = createFitsWcsImagingNode(
                new double[]{2048.0, 2048.0},
                new double[]{45.0, 30.0},
                new double[]{1e-5, 1e-5},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}},
                projNode);

        final Transform transform = support.deserializeTransform(node);
        assertInstanceOf(FitsWcsImaging.class, transform);

        final FitsWcsImaging expected = new FitsWcsImaging(
                new Gnomonic(),
                new double[]{2047.0, 2047.0},
                new double[]{45.0, 30.0},
                new double[]{1e-5, 1e-5},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}});

        assertArrayEquals(expected.evaluate(2047.0, 2047.0),
                transform.evaluate(2047.0, 2047.0), 1e-12);
    }

    @Test
    void deserializeFitsWcsImaging_evaluateAndInverse() {
        final AsdfNode projNode = createProjectionNode("gnomonic-1.2.0", "pix2sky");
        final AsdfNode node = createFitsWcsImagingNode(
                new double[]{2048.0, 2048.0},
                new double[]{45.0, 30.0},
                new double[]{1e-5, 1e-5},
                new double[][]{{1.0, 0.0}, {0.0, 1.0}},
                projNode);

        final Transform transform = support.deserializeTransform(node);
        assertTrue(transform.hasInverse());

        final double[] sky = transform.evaluate(100.0, 200.0);
        final double[] pixel = transform.getInverse().evaluate(sky);
        assertEquals(100.0, pixel[0], 1e-8);
        assertEquals(200.0, pixel[1], 1e-8);
    }
}
