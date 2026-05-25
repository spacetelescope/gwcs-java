package edu.stsci.gwcs.asdf.converter.transform;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Constant;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConstantConverterTest {
    @Test
    void deserializeConstant() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/constant-1.4.0");
        when(node.getDouble("value")).thenReturn(42.0);
        when(node.getInt("dimensions")).thenReturn(2);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Constant.class, transform);
        assertEquals(2, transform.getInputCount());
        assertArrayEquals(new double[]{42.0}, transform.evaluate(1.0, 2.0), 1e-15);
    }

    @Test
    void deserializeConstantVersion1_5_0() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/constant-1.5.0");
        when(node.getDouble("value")).thenReturn(0.0);
        when(node.getInt("dimensions")).thenReturn(1);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Constant.class, transform);
        assertEquals(1, transform.getInputCount());
        assertArrayEquals(new double[]{0.0}, transform.evaluate(99.0), 1e-15);
    }
}
