package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Affine;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.ndarray.Shape;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AffineConverterTest {
    @Test
    void deserializeAffineIdentity() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/affine-1.3.0");

        final NdArray<?> matrixNd = mockNdArray(2);
        doReturn(new double[][]{{1.0, 0.0}, {0.0, 1.0}}).when(matrixNd).toArray(any(double[][].class));
        doReturn(matrixNd).when(node).getNdArray("matrix");

        final NdArray<?> translationNd = mock(NdArray.class);
        doReturn(new double[]{0.0, 0.0}).when(translationNd).toArray(any(double[].class));
        doReturn(translationNd).when(node).getNdArray("translation");

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Affine.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
        assertArrayEquals(new double[]{3.0, 7.0}, transform.evaluate(3.0, 7.0), 1e-15);
    }

    @Test
    void deserializeAffineWithTranslation() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/affine-1.4.0");

        final NdArray<?> matrixNd = mockNdArray(2);
        doReturn(new double[][]{{2.0, 0.0}, {0.0, 3.0}}).when(matrixNd).toArray(any(double[][].class));
        doReturn(matrixNd).when(node).getNdArray("matrix");

        final NdArray<?> translationNd = mock(NdArray.class);
        doReturn(new double[]{1.0, -1.0}).when(translationNd).toArray(any(double[].class));
        doReturn(translationNd).when(node).getNdArray("translation");

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Affine.class, transform);
        assertArrayEquals(new double[]{11.0, 14.0}, transform.evaluate(5.0, 5.0), 1e-15);
    }

    private static NdArray<?> mockNdArray(final int size) {
        final NdArray<?> ndArray = mock(NdArray.class);
        final Shape shape = mock(Shape.class);
        when(shape.get(0)).thenReturn(size);
        doReturn(shape).when(ndArray).getShape();
        return ndArray;
    }
}
