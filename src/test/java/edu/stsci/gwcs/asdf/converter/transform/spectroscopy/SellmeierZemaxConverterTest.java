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

    private static AsdfNode mockDoubleListNode(final List<Double> values) {
        final AsdfNode child = mock(AsdfNode.class);
        when(child.isNdArray()).thenReturn(false);
        when(child.asList(Double.class)).thenReturn(values);
        return child;
    }

    private AsdfNode mockSellmeierZemaxNode(final String tag, final double temperature) {
        final AsdfNode bCoefNode = mockDoubleListNode(List.of(0.6961663, 0.4079426, 0.8974794));
        final AsdfNode cCoefNode = mockDoubleListNode(List.of(0.0046914826, 0.013512063, 97.934003));
        final AsdfNode dCoefNode = mockDoubleListNode(List.of(1.0e-5, 2.0e-5, 3.0e-5));
        final AsdfNode eCoefNode = mockDoubleListNode(List.of(4.0e-5, 5.0e-5, 0.15));

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn(tag);
        when(node.getDouble("temperature")).thenReturn(temperature);
        when(node.getDouble("ref_temperature")).thenReturn(293.15);
        when(node.getDouble("ref_pressure")).thenReturn(1.0);
        when(node.getDouble("pressure")).thenReturn(1.0);
        when(node.get("B_coef")).thenReturn(bCoefNode);
        when(node.get("C_coef")).thenReturn(cCoefNode);
        when(node.get("D_coef")).thenReturn(dCoefNode);
        when(node.get("E_coef")).thenReturn(eCoefNode);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        return node;
    }

    @Test
    void deserializeSellmeierZemax() {
        final AsdfNode node = mockSellmeierZemaxNode("tag:stsci.edu:gwcs/sellmeier_zemax-1.0.0", 296.15);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(SellmeierZemax.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());

        final double[] result = transform.evaluate(0.5893);
        assertTrue(result[0] > 1.0, "Refractive index should be > 1");

        final AsdfNode refNode = mockSellmeierZemaxNode("tag:stsci.edu:gwcs/sellmeier_zemax-1.0.0", 293.15);
        final Transform refTransform = support.deserializeTransform(refNode);
        final double[] refResult = refTransform.evaluate(0.5893);

        assertNotEquals(result[0], refResult[0], 1e-15,
                "Temperature correction should produce a different refractive index");
    }
}
