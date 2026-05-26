package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.Snell3D;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Snell3DConverterTest {
    @Test
    void deserializeSnell3D() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/snell3d-1.0.0");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Snell3D.class, transform);
        assertEquals(4, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void snell3DEvaluatesCorrectly() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/snell3d-1.0.0");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        final double[] result = transform.evaluate(2.0, 0.4, 0.6, 0.0);
        assertEquals(0.2, result[0], 1e-12);
        assertEquals(0.3, result[1], 1e-12);
    }
}
