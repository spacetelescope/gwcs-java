package edu.stsci.gwcs.asdf.converter;

import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AsdfNodeUtilsTest {
    @Test
    void readStringArrayReturnsEmptyArrayForEmptyList() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getList("keys", String.class)).thenReturn(Collections.emptyList());

        final String[] result = AsdfNodeUtils.readStringArray(node, "keys");
        assertEquals(0, result.length);
    }

    @Test
    void readStringArrayPreservesOrder() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getList("names", String.class)).thenReturn(List.of("a", "b", "c"));

        final String[] result = AsdfNodeUtils.readStringArray(node, "names");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    @Test
    void readIntArrayReturnsEmptyArrayForEmptyList() {
        final AsdfNode node = mock(AsdfNode.class);
        final AsdfNode child = mock(AsdfNode.class);
        when(node.get("order")).thenReturn(child);
        when(child.isNdArray()).thenReturn(false);
        when(child.asList(Integer.class)).thenReturn(Collections.emptyList());

        final int[] result = AsdfNodeUtils.readIntArray(node, "order");
        assertEquals(0, result.length);
    }

    @Test
    void readIntArrayPreservesOrder() {
        final AsdfNode node = mock(AsdfNode.class);
        final AsdfNode child = mock(AsdfNode.class);
        when(node.get("order")).thenReturn(child);
        when(child.isNdArray()).thenReturn(false);
        when(child.asList(Integer.class)).thenReturn(List.of(2, 0, 1));

        final int[] result = AsdfNodeUtils.readIntArray(node, "order");
        assertArrayEquals(new int[]{2, 0, 1}, result);
    }
}
