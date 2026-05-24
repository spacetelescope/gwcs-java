package edu.stsci.gwcs.asdf;

import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagRegistryTest {
    private static final String SHIFT_TAG = "tag:stsci.edu:asdf/transform/shift-1.3.0";
    private static final String SCALE_TAG = "tag:stsci.edu:asdf/transform/scale-1.3.0";

    private AsdfNode nodeWithTag(final String tag) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn(tag);
        return node;
    }

    @Test
    void deserializeDispatchesToRegisteredHandler() {
        final TagRegistry registry = new TagRegistry();
        final Object sentinel = new Object();
        registry.register(SHIFT_TAG, node -> sentinel);

        final Object result = registry.deserialize(nodeWithTag(SHIFT_TAG), Object.class);
        assertSame(sentinel, result);
    }

    @Test
    void deserializeReturnsTypedResult() {
        final TagRegistry registry = new TagRegistry();
        registry.register(SHIFT_TAG, node -> "hello");

        final String result = registry.deserialize(nodeWithTag(SHIFT_TAG), String.class);
        assertEquals("hello", result);
    }

    @Test
    void deserializeThrowsOnUnrecognizedTag() {
        final TagRegistry registry = new TagRegistry();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registry.deserialize(nodeWithTag(SHIFT_TAG), Object.class)
        );
        assertTrue(exception.getMessage().contains(SHIFT_TAG));
    }

    @Test
    void deserializeThrowsOnTypeMismatch() {
        final TagRegistry registry = new TagRegistry();
        registry.register(SHIFT_TAG, node -> "not a number");

        final ClassCastException exception = assertThrows(
                ClassCastException.class,
                () -> registry.deserialize(nodeWithTag(SHIFT_TAG), Integer.class)
        );
        assertTrue(exception.getMessage().contains(SHIFT_TAG));
        assertTrue(exception.getMessage().contains("java.lang.String"));
        assertTrue(exception.getMessage().contains("java.lang.Integer"));
    }

    @Test
    void laterRegistrationOverwritesPrevious() {
        final TagRegistry registry = new TagRegistry();
        final Object first = new Object();
        final Object second = new Object();
        registry.register(SHIFT_TAG, node -> first);
        registry.register(SHIFT_TAG, node -> second);

        final Object result = registry.deserialize(nodeWithTag(SHIFT_TAG), Object.class);
        assertSame(second, result);
    }

    @Test
    void hasHandlerReturnsTrueForRegisteredTag() {
        final TagRegistry registry = new TagRegistry();
        registry.register(SHIFT_TAG, node -> null);

        assertTrue(registry.hasHandler(SHIFT_TAG));
    }

    @Test
    void hasHandlerReturnsFalseForUnregisteredTag() {
        final TagRegistry registry = new TagRegistry();
        registry.register(SHIFT_TAG, node -> null);

        assertFalse(registry.hasHandler(SCALE_TAG));
    }

    @Test
    void multipleHandlersDispatchIndependently() {
        final TagRegistry registry = new TagRegistry();
        final Object shiftResult = new Object();
        final Object scaleResult = new Object();
        registry.register(SHIFT_TAG, node -> shiftResult);
        registry.register(SCALE_TAG, node -> scaleResult);

        assertSame(shiftResult, registry.deserialize(nodeWithTag(SHIFT_TAG), Object.class));
        assertSame(scaleResult, registry.deserialize(nodeWithTag(SCALE_TAG), Object.class));
    }

    @Test
    void handlerReceivesTheNode() {
        final TagRegistry registry = new TagRegistry();
        final AsdfNode inputNode = nodeWithTag(SHIFT_TAG);
        registry.register(SHIFT_TAG, node -> {
            assertSame(inputNode, node);
            return "ok";
        });

        registry.deserialize(inputNode, String.class);
    }
}
