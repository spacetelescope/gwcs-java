package edu.stsci.gwcs.asdf.converter.transform.selector;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.selector.RegionsSelector;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegionsSelectorConverterTest {
    @Test
    void deserializeRegionsSelector() {
        // Label mapper: a Constant transform that always returns 1.0 (selecting region 1)
        // We use an Identity(1) as the label mapper since it passes through input directly
        final AsdfNode labelMapperNode = mock(AsdfNode.class);
        when(labelMapperNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/constant-1.4.0");
        when(labelMapperNode.getDouble("value")).thenReturn(1.0);
        when(labelMapperNode.getInt("dimensions")).thenReturn(1);
        when(labelMapperNode.getOptional("name")).thenReturn(Optional.empty());
        when(labelMapperNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(labelMapperNode.getOptional("outputs")).thenReturn(Optional.empty());

        // Region 1 transform: Shift(10.0)
        final AsdfNode shiftNode1 = mock(AsdfNode.class);
        when(shiftNode1.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(shiftNode1.getDouble("offset")).thenReturn(10.0);
        when(shiftNode1.getOptional("name")).thenReturn(Optional.empty());
        when(shiftNode1.getOptional("inputs")).thenReturn(Optional.empty());
        when(shiftNode1.getOptional("outputs")).thenReturn(Optional.empty());

        // Region 2 transform: Shift(20.0)
        final AsdfNode shiftNode2 = mock(AsdfNode.class);
        when(shiftNode2.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(shiftNode2.getDouble("offset")).thenReturn(20.0);
        when(shiftNode2.getOptional("name")).thenReturn(Optional.empty());
        when(shiftNode2.getOptional("inputs")).thenReturn(Optional.empty());
        when(shiftNode2.getOptional("outputs")).thenReturn(Optional.empty());

        final AsdfNode transformsNode = mock(AsdfNode.class);
        when(transformsNode.get(0L)).thenReturn(shiftNode1);
        when(transformsNode.get(1L)).thenReturn(shiftNode2);

        final AsdfNode selectorNode = mock(AsdfNode.class);
        when(selectorNode.getList("labels", Integer.class)).thenReturn(List.of(1, 2));
        when(selectorNode.get("transforms")).thenReturn(transformsNode);

        final AsdfNode undefinedNode = mock(AsdfNode.class);
        when(undefinedNode.asDouble()).thenReturn(Double.NaN);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/regions_selector-1.0.0");
        when(node.get("label_mapper")).thenReturn(labelMapperNode);
        when(node.get("selector")).thenReturn(selectorNode);
        when(node.getOptional("undefined_transform_value")).thenReturn(Optional.of(undefinedNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(RegionsSelector.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());

        // The constant label mapper always outputs 1.0, so region 1 (Shift(10.0)) is used
        assertArrayEquals(new double[]{15.0}, transform.evaluate(5.0), 1e-15);
    }

    @Test
    void deserializeRegionsSelectorDefaultUndefinedTransformValue() {
        final AsdfNode labelMapperNode = mock(AsdfNode.class);
        when(labelMapperNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/constant-1.4.0");
        when(labelMapperNode.getDouble("value")).thenReturn(99.0);
        when(labelMapperNode.getInt("dimensions")).thenReturn(1);
        when(labelMapperNode.getOptional("name")).thenReturn(Optional.empty());
        when(labelMapperNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(labelMapperNode.getOptional("outputs")).thenReturn(Optional.empty());

        final AsdfNode shiftNode = mock(AsdfNode.class);
        when(shiftNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(shiftNode.getDouble("offset")).thenReturn(10.0);
        when(shiftNode.getOptional("name")).thenReturn(Optional.empty());
        when(shiftNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(shiftNode.getOptional("outputs")).thenReturn(Optional.empty());

        final AsdfNode transformsNode = mock(AsdfNode.class);
        when(transformsNode.get(0L)).thenReturn(shiftNode);

        final AsdfNode selectorNode = mock(AsdfNode.class);
        when(selectorNode.getList("labels", Integer.class)).thenReturn(List.of(1));
        when(selectorNode.get("transforms")).thenReturn(transformsNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/regions_selector-1.0.0");
        when(node.get("label_mapper")).thenReturn(labelMapperNode);
        when(node.get("selector")).thenReturn(selectorNode);
        when(node.getOptional("undefined_transform_value")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        // Label mapper returns 99.0, which doesn't match any selector label (only 1)
        // Default undefined_transform_value should be NaN
        final double[] result = transform.evaluate(5.0);
        assertTrue(Double.isNaN(result[0]), "Undefined label should produce NaN by default");
    }
}
