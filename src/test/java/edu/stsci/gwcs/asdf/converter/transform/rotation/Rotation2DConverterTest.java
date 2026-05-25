package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.Rotation2D;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Rotation2DConverterTest {
    @Test
    void deserializeRotation2D() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate2d-1.3.0");
        when(node.getDouble("angle")).thenReturn(45.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Rotation2D.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());

        final double cos45 = Math.cos(Math.toRadians(45.0));
        final double[] result = transform.evaluate(1.0, 0.0);
        assertEquals(cos45, result[0], 1e-12);
        assertEquals(cos45, result[1], 1e-12);
    }

    @Test
    void deserializeRotation2DVersion1_4_0() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate2d-1.4.0");
        when(node.getDouble("angle")).thenReturn(90.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Rotation2D.class, transform);
        final double[] result = transform.evaluate(1.0, 0.0);
        assertEquals(0.0, result[0], 1e-12);
        assertEquals(1.0, result[1], 1e-12);
    }
}
