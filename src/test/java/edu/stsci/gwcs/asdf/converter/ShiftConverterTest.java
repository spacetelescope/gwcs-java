package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.functional.Shift;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShiftConverterTest {
    @Test
    void deserializeShift() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(3.5);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Shift.class, transform);
        assertArrayEquals(new double[]{4.5}, transform.evaluate(1.0), 1e-15);
    }

    @Test
    void deserializeShiftVersion1_2_0() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.2.0");
        when(node.getDouble("offset")).thenReturn(-1.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Shift.class, transform);
        assertArrayEquals(new double[]{4.0}, transform.evaluate(5.0), 1e-15);
    }

    @Test
    void deserializeShiftWithNamedTransform() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(3.5);

        final AsdfNode nameNode = mock(AsdfNode.class);
        when(nameNode.asString()).thenReturn("myshift");
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
        assertEquals("myshift", named.getName());
        assertArrayEquals(new String[]{"x"}, named.getInputNames());
        assertArrayEquals(new String[]{"y"}, named.getOutputNames());
        assertInstanceOf(Shift.class, named.getDelegate());
        assertArrayEquals(new double[]{4.5}, transform.evaluate(1.0), 1e-15);
    }
}
