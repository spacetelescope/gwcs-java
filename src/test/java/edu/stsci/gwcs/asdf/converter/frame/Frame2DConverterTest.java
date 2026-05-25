package edu.stsci.gwcs.asdf.converter.frame;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.frame.Frame;
import edu.stsci.gwcs.frame.Frame2D;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Frame2DConverterTest {
    @Test
    void deserializeFrame2D() {
        final AsdfNode node = mockFrame2DNode(
                "tag:stsci.edu:gwcs/frame2d-1.0.0",
                "detector",
                List.of("x", "y"),
                List.of(0, 1),
                List.of("custom:x", "custom:y"),
                List.of("pixel", "pixel")
        );

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Frame frame = support.deserializeFrame(node);

        assertInstanceOf(Frame2D.class, frame);
        assertEquals("detector", frame.getName());
        assertArrayEquals(new String[]{"x", "y"}, frame.getAxisNames());
        assertArrayEquals(new int[]{0, 1}, frame.getAxisOrder());
        assertArrayEquals(new String[]{"custom:x", "custom:y"}, frame.getAxisPhysicalTypes());
        assertArrayEquals(new String[]{"pixel", "pixel"}, frame.getUnits());
    }

    @Test
    void deserializeFrame2DVersion1_2_0() {
        final AsdfNode node = mockFrame2DNode(
                "tag:stsci.edu:gwcs/frame2d-1.2.0",
                "detector",
                List.of("x", "y"),
                List.of(0, 1),
                List.of("custom:x", "custom:y"),
                List.of("pixel", "pixel")
        );

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Frame frame = support.deserializeFrame(node);

        assertInstanceOf(Frame2D.class, frame);
        assertEquals("detector", frame.getName());
    }

    public static AsdfNode mockFrame2DNode(final String tag,
                                    final String name,
                                    final List<String> axisNames,
                                    final List<Integer> axisOrder,
                                    final List<String> axisPhysicalTypes,
                                    final List<String> units) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn(tag);
        when(node.getString("name")).thenReturn(name);
        when(node.getList("axes_names", String.class)).thenReturn(axisNames);
        when(node.getList("axes_order", Integer.class)).thenReturn(axisOrder);
        when(node.getList("axis_physical_types", String.class)).thenReturn(axisPhysicalTypes);
        when(node.getList("unit", String.class)).thenReturn(units);
        return node;
    }
}
