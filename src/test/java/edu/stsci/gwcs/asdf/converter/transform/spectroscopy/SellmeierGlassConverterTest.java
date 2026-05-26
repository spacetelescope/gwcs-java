package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.SellmeierGlass;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellmeierGlassConverterTest {
    @Test
    void deserializeSellmeierGlass() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/sellmeier_glass-1.0.0");
        when(node.getList("B_coef", Double.class)).thenReturn(List.of(0.6961663, 0.4079426, 0.8974794));
        when(node.getList("C_coef", Double.class)).thenReturn(List.of(0.0046914826, 0.013512063, 97.934003));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(SellmeierGlass.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
    }

    @Test
    void sellmeierGlassEvaluatesCorrectly() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/sellmeier_glass-1.0.0");
        when(node.getList("B_coef", Double.class)).thenReturn(List.of(0.6961663, 0.4079426, 0.8974794));
        when(node.getList("C_coef", Double.class)).thenReturn(List.of(0.0046914826, 0.013512063, 97.934003));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        final double[] result = transform.evaluate(0.5893);
        assertTrue(result[0] > 1.0, "Refractive index should be > 1");
        assertEquals(1.4585, result[0], 0.001);
    }
}
