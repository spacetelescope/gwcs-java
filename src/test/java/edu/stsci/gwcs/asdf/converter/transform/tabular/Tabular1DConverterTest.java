package edu.stsci.gwcs.asdf.converter.transform.tabular;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.tabular.Tabular1D;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.ndarray.Shape;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Tabular1DConverterTest {
    @Test
    void deserializeTabular1DWithBoundsError() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/tabular-1.2.0");

        final AsdfNode pointsListNode = mock(AsdfNode.class);
        final NdArray<?> pointsNd = mockNdArray(3);
        doReturn(new double[]{0.0, 1.0, 2.0}).when(pointsNd).toArray(any(double[].class));
        doReturn(pointsNd).when(pointsListNode).getNdArray(0L);
        when(node.get("points")).thenReturn(pointsListNode);

        final NdArray<?> lookupNd = mockNdArray(3);
        doReturn(new double[]{10.0, 20.0, 30.0}).when(lookupNd).toArray(any(double[].class));
        doReturn(lookupNd).when(node).getNdArray("lookup_table");

        final AsdfNode boundsNode = mock(AsdfNode.class);
        when(boundsNode.asBoolean()).thenReturn(true);
        when(node.getOptional("bounds_error")).thenReturn(Optional.of(boundsNode));
        when(node.getOptional("fill_value")).thenReturn(Optional.empty());

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Tabular1D.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
        // interpolate at x=0.5: 10 + (20-10)*0.5 = 15
        assertArrayEquals(new double[]{15.0}, transform.evaluate(0.5), 1e-15);
        // exact point
        assertArrayEquals(new double[]{20.0}, transform.evaluate(1.0), 1e-15);
    }

    @Test
    void deserializeTabular1DWithFillValue() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/tabular-1.2.0");

        final AsdfNode pointsListNode = mock(AsdfNode.class);
        final NdArray<?> pointsNd = mockNdArray(3);
        doReturn(new double[]{0.0, 1.0, 2.0}).when(pointsNd).toArray(any(double[].class));
        doReturn(pointsNd).when(pointsListNode).getNdArray(0L);
        when(node.get("points")).thenReturn(pointsListNode);

        final NdArray<?> lookupNd = mockNdArray(3);
        doReturn(new double[]{10.0, 20.0, 30.0}).when(lookupNd).toArray(any(double[].class));
        doReturn(lookupNd).when(node).getNdArray("lookup_table");

        final AsdfNode boundsNode = mock(AsdfNode.class);
        when(boundsNode.asBoolean()).thenReturn(false);
        when(node.getOptional("bounds_error")).thenReturn(Optional.of(boundsNode));

        final AsdfNode fillNode = mock(AsdfNode.class);
        when(fillNode.asDouble()).thenReturn(Double.NaN);
        when(node.getOptional("fill_value")).thenReturn(Optional.of(fillNode));

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Tabular1D.class, transform);
        final Tabular1D tabular = (Tabular1D) transform;
        assertEquals(Tabular1D.OutOfBoundsMode.FILL, tabular.getMode());
        assertTrue(Double.isNaN(tabular.getFillValue()));
        assertTrue(Double.isNaN(transform.evaluate(-1.0)[0]));
        // in range still works
        assertArrayEquals(new double[]{15.0}, transform.evaluate(0.5), 1e-15);
    }

    @Test
    void deserializeTabular1DDefaultBoundsError() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/tabular-1.2.0");

        final AsdfNode pointsListNode = mock(AsdfNode.class);
        final NdArray<?> pointsNd = mockNdArray(2);
        doReturn(new double[]{0.0, 1.0}).when(pointsNd).toArray(any(double[].class));
        doReturn(pointsNd).when(pointsListNode).getNdArray(0L);
        when(node.get("points")).thenReturn(pointsListNode);

        final NdArray<?> lookupNd = mockNdArray(2);
        doReturn(new double[]{100.0, 200.0}).when(lookupNd).toArray(any(double[].class));
        doReturn(lookupNd).when(node).getNdArray("lookup_table");

        when(node.getOptional("bounds_error")).thenReturn(Optional.empty());
        when(node.getOptional("fill_value")).thenReturn(Optional.empty());

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Tabular1D.class, transform);
        final Tabular1D tabular = (Tabular1D) transform;
        assertEquals(Tabular1D.OutOfBoundsMode.ERROR, tabular.getMode());
        assertTrue(Double.isNaN(tabular.getFillValue()));
        assertEquals(Tabular1D.InterpolationMethod.LINEAR, tabular.getMethod());
        assertThrows(IllegalArgumentException.class, () -> transform.evaluate(-1.0));
    }

    @Test
    void deserializeTabular1DWithNamedTransform() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/tabular-1.2.0");

        final AsdfNode pointsListNode = mock(AsdfNode.class);
        final NdArray<?> pointsNd = mockNdArray(2);
        doReturn(new double[]{0.0, 1.0}).when(pointsNd).toArray(any(double[].class));
        doReturn(pointsNd).when(pointsListNode).getNdArray(0L);
        when(node.get("points")).thenReturn(pointsListNode);

        final NdArray<?> lookupNd = mockNdArray(2);
        doReturn(new double[]{100.0, 200.0}).when(lookupNd).toArray(any(double[].class));
        doReturn(lookupNd).when(node).getNdArray("lookup_table");

        when(node.getOptional("bounds_error")).thenReturn(Optional.empty());
        when(node.getOptional("fill_value")).thenReturn(Optional.empty());

        final AsdfNode nameNode = mock(AsdfNode.class);
        when(nameNode.asString()).thenReturn("mytab");
        when(node.getOptional("name")).thenReturn(Optional.of(nameNode));

        final AsdfNode inputsNode = mock(AsdfNode.class);
        when(inputsNode.asList(String.class)).thenReturn(List.of("x"));
        when(node.getOptional("inputs")).thenReturn(Optional.of(inputsNode));

        final AsdfNode outputsNode = mock(AsdfNode.class);
        when(outputsNode.asList(String.class)).thenReturn(List.of("y"));
        when(node.getOptional("outputs")).thenReturn(Optional.of(outputsNode));

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(NamedTransform.class, transform);
        final NamedTransform named = (NamedTransform) transform;
        assertEquals("mytab", named.getName());
        assertArrayEquals(new String[]{"x"}, named.getInputNames());
        assertArrayEquals(new String[]{"y"}, named.getOutputNames());
        assertInstanceOf(Tabular1D.class, named.getDelegate());
    }

    private static NdArray<?> mockNdArray(final int size) {
        final NdArray<?> ndArray = mock(NdArray.class);
        final Shape shape = mock(Shape.class);
        when(shape.get(0)).thenReturn(size);
        doReturn(shape).when(ndArray).getShape();
        return ndArray;
    }
}
