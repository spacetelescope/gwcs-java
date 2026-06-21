package edu.stsci.gwcs.asdf;

import edu.stsci.gwcs.Step;
import edu.stsci.gwcs.Wcs;
import edu.stsci.gwcs.frame.Frame;
import edu.stsci.gwcs.frame.Frame2D;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GwcsAsdfSupportTest {
    private final GwcsAsdfSupport support = new GwcsAsdfSupport();
    @Test
    void unknownTagThrowsIllegalArgumentException() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/spectral_frame-1.0.0");

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> support.deserializeWcs(node)
        );
        assertTrue(exception.getMessage().contains("tag:stsci.edu:gwcs/spectral_frame-1.0.0"));
    }

    @Test
    void registryDispatchesToRegisteredHandler() {
        final TagRegistry registry = new TagRegistry();
        final Object sentinel = new Object();
        registry.register("tag:stsci.edu:asdf/transform/shift-1.3.0", node -> sentinel);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");

        final Object result = registry.deserialize(node, Object.class);
        assertSame(sentinel, result);
    }

    @Test
    void customTagRegistrationWorksWithoutModifyingExistingCode() {
        final TagRegistry registry = new TagRegistry();
        final Object customResult = new Object();
        registry.register("tag:example.org:custom/widget-1.0.0", node -> customResult);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:example.org:custom/widget-1.0.0");

        final Object result = registry.deserialize(node, Object.class);
        assertSame(customResult, result);
    }

    @Test
    void hasHandlerReturnsTrueForRegisteredTag() {
        final TagRegistry registry = new TagRegistry();
        registry.register("tag:stsci.edu:asdf/transform/shift-1.3.0", node -> null);

        assertTrue(registry.hasHandler("tag:stsci.edu:asdf/transform/shift-1.3.0"));
        assertFalse(registry.hasHandler("tag:stsci.edu:asdf/transform/shift-9.9.9"));
    }

    @Test
    void deserializeThrowsClassCastExceptionOnTypeMismatch() {
        final TagRegistry registry = new TagRegistry();
        registry.register("tag:stsci.edu:gwcs/frame2d-1.0.0", node -> "not a frame");

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/frame2d-1.0.0");

        final ClassCastException exception = assertThrows(
                ClassCastException.class,
                () -> registry.deserialize(node, Frame.class)
        );
        assertTrue(exception.getMessage().contains("tag:stsci.edu:gwcs/frame2d-1.0.0"));
        assertTrue(exception.getMessage().contains(Frame.class.getName()));
    }

    @Test
    void unknownTagThrowsIllegalArgumentExceptionFromDeserializeTransform() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/unknown-1.0.0");

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> support.deserializeTransform(node)
        );
        assertTrue(exception.getMessage().contains("tag:stsci.edu:asdf/transform/unknown-1.0.0"));
    }

    @Test
    void deserializeStepWithFrameAndTransform() {
        final AsdfNode frameNode = mockFrame2DNode("detector", new String[]{"x", "y"});
        final AsdfNode transformNode = mockShiftNode(5.0);

        final AsdfNode stepNode = mock(AsdfNode.class);
        when(stepNode.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(stepNode.get("frame")).thenReturn(frameNode);
        when(stepNode.getOptional("transform")).thenReturn(Optional.of(transformNode));

        final Step step = support.deserializeStep(stepNode);

        assertInstanceOf(Frame2D.class, step.getFrame());
        assertEquals("detector", step.getFrameName());
        assertNotNull(step.getTransform());
        assertArrayEquals(new double[]{6.0}, step.getTransform().evaluate(1.0), 1e-15);
    }

    @Test
    void deserializeTerminalStepWithoutTransform() {
        final AsdfNode frameNode = mockFrame2DNode("world", new String[]{"lon", "lat"});

        final AsdfNode stepNode = mock(AsdfNode.class);
        when(stepNode.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.3.0");
        when(stepNode.get("frame")).thenReturn(frameNode);
        when(stepNode.getOptional("transform")).thenReturn(Optional.empty());

        final Step step = support.deserializeStep(stepNode);

        assertEquals("world", step.getFrameName());
        assertNull(step.getTransform());
    }

    @Test
    void deserializeWcsWithTwoSteps() {
        final AsdfNode detectorFrameNode = mockFrame2DNode("detector", new String[]{"x", "y"});
        final AsdfNode worldFrameNode = mockFrame2DNode("world", new String[]{"lon", "lat"});
        final AsdfNode shiftNode1 = mockShiftNode(1.0);
        final AsdfNode shiftNode2 = mockShiftNode(2.0);

        final AsdfNode concatNode = mock(AsdfNode.class);
        when(concatNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/concatenate-1.3.0");
        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(shiftNode1);
        when(forwardNode.get(1L)).thenReturn(shiftNode2);
        when(concatNode.get("forward")).thenReturn(forwardNode);
        when(concatNode.getOptional("name")).thenReturn(Optional.empty());
        when(concatNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(concatNode.getOptional("outputs")).thenReturn(Optional.empty());
        when(concatNode.getOptional("inverse")).thenReturn(Optional.empty());
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.empty());

        final AsdfNode step1Node = mock(AsdfNode.class);
        when(step1Node.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(step1Node.get("frame")).thenReturn(detectorFrameNode);
        when(step1Node.getOptional("transform")).thenReturn(Optional.of(concatNode));

        final AsdfNode step2Node = mock(AsdfNode.class);
        when(step2Node.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(step2Node.get("frame")).thenReturn(worldFrameNode);
        when(step2Node.getOptional("transform")).thenReturn(Optional.empty());

        final AsdfNode stepsNode = mock(AsdfNode.class);
        when(stepsNode.size()).thenReturn(2);
        when(stepsNode.get(0L)).thenReturn(step1Node);
        when(stepsNode.get(1L)).thenReturn(step2Node);

        final AsdfNode wcsNode = mock(AsdfNode.class);
        when(wcsNode.getTag()).thenReturn("tag:stsci.edu:gwcs/wcs-1.2.0");
        when(wcsNode.getString("name")).thenReturn("test_wcs");
        when(wcsNode.get("steps")).thenReturn(stepsNode);
        when(wcsNode.getOptional("pixel_shape")).thenReturn(Optional.empty());

        final Wcs wcs = support.deserializeWcs(wcsNode);

        assertEquals("test_wcs", wcs.getName());
        assertEquals(2, wcs.getSteps().length);
        assertEquals("detector", wcs.getInputFrame().getName());
        assertEquals("world", wcs.getOutputFrame().getName());
        assertArrayEquals(new double[]{1.0, 2.0}, wcs.evaluate(0.0, 0.0), 1e-15);
    }

    @Test
    void deserializeWcsWithPixelShape() {
        final AsdfNode detectorFrameNode = mockFrame2DNode("detector", new String[]{"x", "y"});
        final AsdfNode worldFrameNode = mockFrame2DNode("world", new String[]{"lon", "lat"});
        final AsdfNode shiftNode1 = mockShiftNode(0.0);
        final AsdfNode shiftNode2 = mockShiftNode(0.0);

        final AsdfNode concatNode = mock(AsdfNode.class);
        when(concatNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/concatenate-1.3.0");
        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(shiftNode1);
        when(forwardNode.get(1L)).thenReturn(shiftNode2);
        when(concatNode.get("forward")).thenReturn(forwardNode);
        when(concatNode.getOptional("name")).thenReturn(Optional.empty());
        when(concatNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(concatNode.getOptional("outputs")).thenReturn(Optional.empty());
        when(concatNode.getOptional("inverse")).thenReturn(Optional.empty());
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.empty());

        final AsdfNode step1Node = mock(AsdfNode.class);
        when(step1Node.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(step1Node.get("frame")).thenReturn(detectorFrameNode);
        when(step1Node.getOptional("transform")).thenReturn(Optional.of(concatNode));

        final AsdfNode step2Node = mock(AsdfNode.class);
        when(step2Node.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(step2Node.get("frame")).thenReturn(worldFrameNode);
        when(step2Node.getOptional("transform")).thenReturn(Optional.empty());

        final AsdfNode stepsNode = mock(AsdfNode.class);
        when(stepsNode.size()).thenReturn(2);
        when(stepsNode.get(0L)).thenReturn(step1Node);
        when(stepsNode.get(1L)).thenReturn(step2Node);

        final AsdfNode wcsNode = mock(AsdfNode.class);
        when(wcsNode.getTag()).thenReturn("tag:stsci.edu:gwcs/wcs-1.4.0");
        when(wcsNode.getString("name")).thenReturn("test_wcs");
        when(wcsNode.get("steps")).thenReturn(stepsNode);
        final AsdfNode pixelShapeNode = mock(AsdfNode.class);
        when(pixelShapeNode.isNdArray()).thenReturn(false);
        when(pixelShapeNode.asList(Integer.class)).thenReturn(List.of(4088, 4088));
        when(wcsNode.getOptional("pixel_shape")).thenReturn(Optional.of(pixelShapeNode));
        when(wcsNode.get("pixel_shape")).thenReturn(pixelShapeNode);

        final Wcs wcs = support.deserializeWcs(wcsNode);

        assertArrayEquals(new int[]{4088, 4088}, wcs.getPixelShape());
    }

    @Test
    void deserializeWcsWithoutPixelShape() {
        final AsdfNode detectorFrameNode = mockFrame2DNode("detector", new String[]{"x", "y"});
        final AsdfNode worldFrameNode = mockFrame2DNode("world", new String[]{"lon", "lat"});
        final AsdfNode shiftNode1 = mockShiftNode(0.0);
        final AsdfNode shiftNode2 = mockShiftNode(0.0);

        final AsdfNode concatNode = mock(AsdfNode.class);
        when(concatNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/concatenate-1.3.0");
        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(shiftNode1);
        when(forwardNode.get(1L)).thenReturn(shiftNode2);
        when(concatNode.get("forward")).thenReturn(forwardNode);
        when(concatNode.getOptional("name")).thenReturn(Optional.empty());
        when(concatNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(concatNode.getOptional("outputs")).thenReturn(Optional.empty());
        when(concatNode.getOptional("inverse")).thenReturn(Optional.empty());
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.empty());

        final AsdfNode step1Node = mock(AsdfNode.class);
        when(step1Node.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(step1Node.get("frame")).thenReturn(detectorFrameNode);
        when(step1Node.getOptional("transform")).thenReturn(Optional.of(concatNode));

        final AsdfNode step2Node = mock(AsdfNode.class);
        when(step2Node.getTag()).thenReturn("tag:stsci.edu:gwcs/step-1.1.0");
        when(step2Node.get("frame")).thenReturn(worldFrameNode);
        when(step2Node.getOptional("transform")).thenReturn(Optional.empty());

        final AsdfNode stepsNode = mock(AsdfNode.class);
        when(stepsNode.size()).thenReturn(2);
        when(stepsNode.get(0L)).thenReturn(step1Node);
        when(stepsNode.get(1L)).thenReturn(step2Node);

        final AsdfNode wcsNode = mock(AsdfNode.class);
        when(wcsNode.getTag()).thenReturn("tag:stsci.edu:gwcs/wcs-1.2.0");
        when(wcsNode.getString("name")).thenReturn("test_wcs");
        when(wcsNode.get("steps")).thenReturn(stepsNode);
        when(wcsNode.getOptional("pixel_shape")).thenReturn(Optional.empty());

        final Wcs wcs = support.deserializeWcs(wcsNode);

        assertNull(wcs.getPixelShape());
    }

    private AsdfNode mockFrame2DNode(final String name, final String[] axisNames) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/frame2d-1.0.0");
        when(node.getString("name")).thenReturn(name);
        when(node.getList("axes_names", String.class)).thenReturn(List.of(axisNames));
        final AsdfNode axisOrderNode = mock(AsdfNode.class);
        when(axisOrderNode.isNdArray()).thenReturn(false);
        when(axisOrderNode.asList(Integer.class)).thenReturn(List.of(0, 1));
        when(node.get("axes_order")).thenReturn(axisOrderNode);
        when(node.getList("axis_physical_types", String.class)).thenReturn(List.of("custom:x", "custom:y"));
        when(node.getList("unit", String.class)).thenReturn(List.of("pixel", "pixel"));
        return node;
    }

    private AsdfNode mockShiftNode(final double offset) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(offset);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());
        return node;
    }
}
