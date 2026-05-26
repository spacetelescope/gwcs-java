package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.SellmeierZemax;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellmeierZemaxConverterTest {
    @Test
    void deserializeSellmeierZemax() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/sellmeier_zemax-1.0.0");
        when(node.getDouble("temperature")).thenReturn(296.15);
        when(node.getDouble("ref_temperature")).thenReturn(293.15);
        when(node.getDouble("ref_pressure")).thenReturn(1.0);
        when(node.getDouble("pressure")).thenReturn(1.0);
        when(node.getList("B_coef", Double.class)).thenReturn(List.of(0.6961663, 0.4079426, 0.8974794));
        when(node.getList("C_coef", Double.class)).thenReturn(List.of(0.0046914826, 0.013512063, 97.934003));
        when(node.getList("D_coef", Double.class)).thenReturn(List.of(1.0e-5, 2.0e-5, 3.0e-5));
        when(node.getList("E_coef", Double.class)).thenReturn(List.of(4.0e-5, 5.0e-5, 0.15));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(SellmeierZemax.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());

        final double[] result = transform.evaluate(0.5893);
        assertTrue(result[0] > 1.0, "Refractive index should be > 1");

        final AsdfNode refNode = mock(AsdfNode.class);
        when(refNode.getTag()).thenReturn("tag:stsci.edu:gwcs/sellmeier_zemax-1.0.0");
        when(refNode.getDouble("temperature")).thenReturn(293.15);
        when(refNode.getDouble("ref_temperature")).thenReturn(293.15);
        when(refNode.getDouble("ref_pressure")).thenReturn(1.0);
        when(refNode.getDouble("pressure")).thenReturn(1.0);
        when(refNode.getList("B_coef", Double.class)).thenReturn(List.of(0.6961663, 0.4079426, 0.8974794));
        when(refNode.getList("C_coef", Double.class)).thenReturn(List.of(0.0046914826, 0.013512063, 97.934003));
        when(refNode.getList("D_coef", Double.class)).thenReturn(List.of(1.0e-5, 2.0e-5, 3.0e-5));
        when(refNode.getList("E_coef", Double.class)).thenReturn(List.of(4.0e-5, 5.0e-5, 0.15));
        when(refNode.getOptional("name")).thenReturn(Optional.empty());
        when(refNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(refNode.getOptional("outputs")).thenReturn(Optional.empty());

        final Transform refTransform = support.deserializeTransform(refNode);
        final double[] refResult = refTransform.evaluate(0.5893);

        assertNotEquals(result[0], refResult[0], 1e-15,
                "Temperature correction should produce a different refractive index");
    }
}
