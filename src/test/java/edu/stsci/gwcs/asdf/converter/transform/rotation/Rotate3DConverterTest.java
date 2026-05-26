package edu.stsci.gwcs.asdf.converter.transform.rotation;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.rotation.EulerAngleRotation;
import edu.stsci.gwcs.transform.rotation.RotateCelestial2Native;
import edu.stsci.gwcs.transform.rotation.RotateNative2Celestial;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Rotate3DConverterTest {
    @Test
    void deserializeNative2Celestial() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate3d-1.3.0");
        when(node.getString("direction")).thenReturn("native2celestial");
        when(node.getDouble("phi")).thenReturn(0.0);
        when(node.getDouble("theta")).thenReturn(0.0);
        when(node.getDouble("psi")).thenReturn(180.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(RotateNative2Celestial.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
    }

    @Test
    void deserializeCelestial2Native() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate3d-1.4.0");
        when(node.getString("direction")).thenReturn("celestial2native");
        when(node.getDouble("phi")).thenReturn(45.0);
        when(node.getDouble("theta")).thenReturn(30.0);
        when(node.getDouble("psi")).thenReturn(180.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(RotateCelestial2Native.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
    }

    @Test
    void deserializeEulerAngleRotation() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate3d-1.3.0");
        when(node.getString("direction")).thenReturn("zxz");
        when(node.getDouble("phi")).thenReturn(0.0);
        when(node.getDouble("theta")).thenReturn(45.0);
        when(node.getDouble("psi")).thenReturn(90.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(EulerAngleRotation.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(2, transform.getOutputCount());
    }

    @Test
    void native2CelestialRoundTrips() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/rotate3d-1.3.0");
        when(node.getString("direction")).thenReturn("native2celestial");
        when(node.getDouble("phi")).thenReturn(45.0);
        when(node.getDouble("theta")).thenReturn(30.0);
        when(node.getDouble("psi")).thenReturn(180.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertTrue(transform.hasInverse());
        final double[] forward = transform.evaluate(10.0, 20.0);
        final double[] roundTrip = transform.getInverse().evaluate(forward);
        assertEquals(10.0, roundTrip[0], 1e-10);
        assertEquals(20.0, roundTrip[1], 1e-10);
    }
}
