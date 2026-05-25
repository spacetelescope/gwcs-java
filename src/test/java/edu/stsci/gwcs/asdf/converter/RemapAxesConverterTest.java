package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.RemapAxes;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemapAxesConverterTest {
    @Test
    void deserializeRemapAxesWithNInputs() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/remap_axes-1.3.0");
        when(node.getList("mapping", Integer.class)).thenReturn(List.of(1, 0, 1));

        final AsdfNode nInputsNode = mock(AsdfNode.class);
        when(nInputsNode.asInt()).thenReturn(3);
        when(node.getOptional("n_inputs")).thenReturn(Optional.of(nInputsNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(RemapAxes.class, transform);
        assertEquals(3, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
        assertArrayEquals(new double[]{20.0, 10.0, 20.0}, transform.evaluate(10.0, 20.0, 30.0), 1e-15);
    }

    @Test
    void deserializeRemapAxesWithoutNInputs() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/remap_axes-1.4.0");
        when(node.getList("mapping", Integer.class)).thenReturn(List.of(1, 0));
        when(node.getOptional("n_inputs")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(RemapAxes.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
        assertArrayEquals(new double[]{20.0, 10.0}, transform.evaluate(10.0, 20.0), 1e-15);
    }
}
