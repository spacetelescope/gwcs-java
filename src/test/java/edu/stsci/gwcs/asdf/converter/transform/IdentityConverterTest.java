package edu.stsci.gwcs.asdf.converter.transform;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Identity;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdentityConverterTest {
    @Test
    void deserializeIdentityWithNDims() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/identity-1.3.0");

        final AsdfNode nDimsNode = mock(AsdfNode.class);
        when(nDimsNode.asInt()).thenReturn(3);
        when(node.getOptional("n_dims")).thenReturn(Optional.of(nDimsNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Identity.class, transform);
        assertEquals(3, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void deserializeIdentityWithoutNDims() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/identity-1.2.0");
        when(node.getOptional("n_dims")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Identity.class, transform);
        assertEquals(1, transform.getInputCount());
    }
}
