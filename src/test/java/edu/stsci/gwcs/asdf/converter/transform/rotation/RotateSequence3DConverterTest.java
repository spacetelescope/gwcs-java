package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.RotateSequence3D;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RotateSequence3DConverterTest {
    @Test
    void deserializeRotateSequence3D() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate_sequence_3d-1.1.0");
        when(node.getString("rotation_type")).thenReturn("cartesian");
        when(node.getList("angles", Double.class)).thenReturn(List.of(90.0, 0.0, 0.0));
        when(node.getString("axes_order")).thenReturn("zyx");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(RotateSequence3D.class, transform);
        assertEquals(3, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());

        assertTrue(transform.hasInverse());
        final double[] result = transform.evaluate(1.0, 2.0, 3.0);
        final double[] roundTrip = transform.getInverse().evaluate(result);
        assertEquals(1.0, roundTrip[0], 1e-12);
        assertEquals(2.0, roundTrip[1], 1e-12);
        assertEquals(3.0, roundTrip[2], 1e-12);
    }

    @Test
    void deserializeIdentityRotation() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate_sequence_3d-1.1.0");
        when(node.getList("angles", Double.class)).thenReturn(List.of(0.0, 0.0, 0.0));
        when(node.getString("axes_order")).thenReturn("xyz");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        final double[] result = transform.evaluate(1.0, 2.0, 3.0);
        assertEquals(1.0, result[0], 1e-12);
        assertEquals(2.0, result[1], 1e-12);
        assertEquals(3.0, result[2], 1e-12);
    }
}
