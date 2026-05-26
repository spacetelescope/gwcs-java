package edu.stsci.gwcs.asdf.converter.transform.selector;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.selector.LabelMapperDict;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LabelMapperDictConverterTest {
    @Test
    void deserializeLabelMapperDict() {
        final AsdfNode shiftNode1 = mock(AsdfNode.class);
        when(shiftNode1.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(shiftNode1.getDouble("offset")).thenReturn(10.0);
        when(shiftNode1.getOptional("name")).thenReturn(Optional.empty());
        when(shiftNode1.getOptional("inputs")).thenReturn(Optional.empty());
        when(shiftNode1.getOptional("outputs")).thenReturn(Optional.empty());

        final AsdfNode shiftNode2 = mock(AsdfNode.class);
        when(shiftNode2.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(shiftNode2.getDouble("offset")).thenReturn(20.0);
        when(shiftNode2.getOptional("name")).thenReturn(Optional.empty());
        when(shiftNode2.getOptional("inputs")).thenReturn(Optional.empty());
        when(shiftNode2.getOptional("outputs")).thenReturn(Optional.empty());

        final AsdfNode modelsNode = mock(AsdfNode.class);
        when(modelsNode.get(0L)).thenReturn(shiftNode1);
        when(modelsNode.get(1L)).thenReturn(shiftNode2);

        final AsdfNode mapperNode = mock(AsdfNode.class);
        when(mapperNode.getList("labels", Double.class)).thenReturn(List.of(1.0, 2.0));
        when(mapperNode.get("models")).thenReturn(modelsNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/label_mapper-1.0.0");
        when(node.get("mapper")).thenReturn(mapperNode);
        when(node.getOptional("atol")).thenReturn(Optional.empty());
        when(node.getOptional("no_label")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(LabelMapperDict.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());

        assertArrayEquals(new double[]{11.0}, transform.evaluate(1.0), 1e-15);
        assertArrayEquals(new double[]{22.0}, transform.evaluate(2.0), 1e-15);
    }

    @Test
    void deserializeLabelMapperDictWithCustomTolerance() {
        final AsdfNode shiftNode = mock(AsdfNode.class);
        when(shiftNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(shiftNode.getDouble("offset")).thenReturn(5.0);
        when(shiftNode.getOptional("name")).thenReturn(Optional.empty());
        when(shiftNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(shiftNode.getOptional("outputs")).thenReturn(Optional.empty());

        final AsdfNode modelsNode = mock(AsdfNode.class);
        when(modelsNode.get(0L)).thenReturn(shiftNode);

        final AsdfNode mapperNode = mock(AsdfNode.class);
        when(mapperNode.getList("labels", Double.class)).thenReturn(List.of(1.0));
        when(mapperNode.get("models")).thenReturn(modelsNode);

        final AsdfNode atolNode = mock(AsdfNode.class);
        when(atolNode.asDouble()).thenReturn(0.1);

        final AsdfNode noLabelNode = mock(AsdfNode.class);
        when(noLabelNode.asDouble()).thenReturn(0.0);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/label_mapper-1.0.0");
        when(node.get("mapper")).thenReturn(mapperNode);
        when(node.getOptional("atol")).thenReturn(Optional.of(atolNode));
        when(node.getOptional("no_label")).thenReturn(Optional.of(noLabelNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertArrayEquals(new double[]{6.05}, transform.evaluate(1.05), 1e-15);
        assertArrayEquals(new double[]{0.0}, transform.evaluate(99.0), 1e-15);
    }
}
