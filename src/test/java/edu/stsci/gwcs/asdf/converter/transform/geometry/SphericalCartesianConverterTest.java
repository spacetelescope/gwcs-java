package edu.stsci.gwcs.asdf.converter.transform.geometry;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.geometry.CartesianToSpherical;
import edu.stsci.gwcs.transform.geometry.SphericalToCartesian;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SphericalCartesianConverterTest {
    @Test
    void deserializeSphericalToCartesian() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/spherical_cartesian-1.0.0");
        when(node.getString("transform_type")).thenReturn("spherical_to_cartesian");
        final AsdfNode wrapNode = mock(AsdfNode.class);
        when(wrapNode.asInt()).thenReturn(360);
        when(node.getOptional("wrap_lon_at")).thenReturn(Optional.of(wrapNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(SphericalToCartesian.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());

        final double[] result = transform.evaluate(0.0, 0.0);
        assertEquals(1.0, result[0], 1e-12);
        assertEquals(0.0, result[1], 1e-12);
        assertEquals(0.0, result[2], 1e-12);
    }

    @Test
    void deserializeCartesianToSpherical() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/spherical_cartesian-1.3.0");
        when(node.getString("transform_type")).thenReturn("cartesian_to_spherical");
        when(node.getOptional("wrap_lon_at")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(CartesianToSpherical.class, transform);
        assertEquals(3, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());

        final double[] result = transform.evaluate(1.0, 0.0, 0.0);
        assertEquals(0.0, result[0], 1e-12);
        assertEquals(0.0, result[1], 1e-12);
    }

    @Test
    void deserializeSphericalToCartesianDefaultWrapLonAt() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/spherical_cartesian-1.0.0");
        when(node.getString("transform_type")).thenReturn("spherical_to_cartesian");
        when(node.getOptional("wrap_lon_at")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(SphericalToCartesian.class, transform);
    }

    @Test
    void unknownTransformTypeThrows() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/spherical_cartesian-1.0.0");
        when(node.getString("transform_type")).thenReturn("invalid_type");
        when(node.getOptional("wrap_lon_at")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        assertThrows(IllegalArgumentException.class, () -> support.deserializeTransform(node));
    }
}
