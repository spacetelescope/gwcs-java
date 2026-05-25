package edu.stsci.gwcs.asdf;

import edu.stsci.gwcs.coordinate.Frame;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GwcsAsdfSupportTest {
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
}
