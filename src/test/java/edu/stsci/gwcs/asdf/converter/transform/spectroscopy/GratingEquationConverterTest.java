package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.AnglesFromGratingEquation3D;
import edu.stsci.gwcs.transform.spectroscopy.WavelengthFromGratingEquation;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GratingEquationConverterTest {
    @Test
    void deserializeAnglesFromGratingEquation3D() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/grating_equation-1.0.0");
        when(node.getDouble("groove_density")).thenReturn(100.0);
        when(node.getInt("order")).thenReturn(1);
        when(node.getString("output")).thenReturn("angle");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(AnglesFromGratingEquation3D.class, transform);
        assertEquals(3, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void deserializeWavelengthFromGratingEquation() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/grating_equation-1.0.0");
        when(node.getDouble("groove_density")).thenReturn(100.0);
        when(node.getInt("order")).thenReturn(1);
        when(node.getString("output")).thenReturn("wavelength");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(WavelengthFromGratingEquation.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
    }

    @Test
    void anglesFromGratingEquationEvaluatesCorrectly() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/grating_equation-1.0.0");
        when(node.getDouble("groove_density")).thenReturn(100.0);
        when(node.getInt("order")).thenReturn(1);
        when(node.getString("output")).thenReturn("angle");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        final double[] result = transform.evaluate(0.01, 0.5, 0.3);
        assertEquals(-0.5, result[0], 1e-12);
        assertEquals(-0.3, result[1], 1e-12);
        final double expectedGamma = Math.sqrt(1.0 - 0.5 * 0.5 - 0.3 * 0.3);
        assertEquals(expectedGamma, result[2], 1e-12);
    }

    @Test
    void unknownOutputThrows() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/grating_equation-1.0.0");
        when(node.getDouble("groove_density")).thenReturn(100.0);
        when(node.getInt("order")).thenReturn(1);
        when(node.getString("output")).thenReturn("invalid");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        assertThrows(IllegalArgumentException.class, () -> support.deserializeTransform(node));
    }
}
