package edu.stsci.gwcs.asdf.converter.transform.geometry;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.geometry.FromDirectionCosines;
import edu.stsci.gwcs.transform.geometry.ToDirectionCosines;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DirectionCosinesConverterTest {
    @Test
    void deserializeToDirectionCosines() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/direction_cosines-1.0.0");
        when(node.getString("transform_type")).thenReturn("to_direction_cosines");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(ToDirectionCosines.class, transform);
        assertEquals(3, transform.getInputCount());
        assertEquals(4, transform.getOutputCount());
    }

    @Test
    void deserializeFromDirectionCosines() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/direction_cosines-1.3.0");
        when(node.getString("transform_type")).thenReturn("from_direction_cosines");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(FromDirectionCosines.class, transform);
        assertEquals(4, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
    }

    @Test
    void toDirectionCosinesRoundTrips() {
        final AsdfNode toNode = mock(AsdfNode.class);
        when(toNode.getTag()).thenReturn("tag:stsci.edu:gwcs/direction_cosines-1.0.0");
        when(toNode.getString("transform_type")).thenReturn("to_direction_cosines");
        when(toNode.getOptional("name")).thenReturn(Optional.empty());
        when(toNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(toNode.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform toTransform = support.deserializeTransform(toNode);

        assertTrue(toTransform.hasInverse());
        final double[] forward = toTransform.evaluate(0.5, 0.3, 0.0);
        final double[] roundTrip = toTransform.getInverse().evaluate(forward);
        assertEquals(0.5, roundTrip[0], 1e-12);
        assertEquals(0.3, roundTrip[1], 1e-12);
    }

    @Test
    void unknownTransformTypeThrows() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:gwcs/direction_cosines-1.0.0");
        when(node.getString("transform_type")).thenReturn("invalid_type");
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        assertThrows(IllegalArgumentException.class, () -> support.deserializeTransform(node));
    }
}
