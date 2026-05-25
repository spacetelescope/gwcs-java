package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.coordinate.CelestialFrame;
import edu.stsci.gwcs.coordinate.CompositeFrame;
import edu.stsci.gwcs.coordinate.Frame;
import edu.stsci.gwcs.coordinate.Frame2D;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static edu.stsci.gwcs.asdf.converter.CelestialFrameConverterTest.mockCelestialFrameNode;
import static edu.stsci.gwcs.asdf.converter.Frame2DConverterTest.mockFrame2DNode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompositeFrameConverterTest {
    @Test
    void deserializeCompositeFrame() {
        final AsdfNode frame2dNode = mockFrame2DNode(
                "tag:stsci.edu:gwcs/frame2d-1.0.0",
                "detector",
                List.of("x", "y"),
                List.of(0, 1),
                List.of("custom:x", "custom:y"),
                List.of("pixel", "pixel")
        );

        final AsdfNode celestialNode = mockCelestialFrameNode(
                "tag:stsci.edu:gwcs/celestial_frame-1.0.0",
                "sky",
                List.of("lon", "lat"),
                List.of(2, 3),
                List.of("pos.eq.ra", "pos.eq.dec"),
                List.of("deg", "deg"),
                "tag:astropy.org:astropy/coordinates/frames/icrs-1.1.0"
        );

        final AsdfNode framesSequence = mock(AsdfNode.class);
        when(framesSequence.size()).thenReturn(2);
        when(framesSequence.get(0L)).thenReturn(frame2dNode);
        when(framesSequence.get(1L)).thenReturn(celestialNode);

        final AsdfNode compositeNode = mock(AsdfNode.class);
        when(compositeNode.getTag()).thenReturn("tag:stsci.edu:gwcs/composite_frame-1.0.0");
        when(compositeNode.getString("name")).thenReturn("composite");
        when(compositeNode.get("frames")).thenReturn(framesSequence);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Frame frame = support.deserializeFrame(compositeNode);

        assertInstanceOf(CompositeFrame.class, frame);
        final CompositeFrame compositeFrame = (CompositeFrame) frame;
        assertEquals("composite", compositeFrame.getName());
        assertEquals(4, compositeFrame.getAxisCount());

        final Frame[] subFrames = compositeFrame.getFrames();
        assertEquals(2, subFrames.length);
        assertInstanceOf(Frame2D.class, subFrames[0]);
        assertInstanceOf(CelestialFrame.class, subFrames[1]);
    }

    @Test
    void deserializeCompositeFrameVersion1_2_0() {
        final AsdfNode frame2dNode = mockFrame2DNode(
                "tag:stsci.edu:gwcs/frame2d-1.0.0",
                "detector",
                List.of("x", "y"),
                List.of(0, 1),
                List.of("custom:x", "custom:y"),
                List.of("pixel", "pixel")
        );

        final AsdfNode framesSequence = mock(AsdfNode.class);
        when(framesSequence.size()).thenReturn(1);
        when(framesSequence.get(0L)).thenReturn(frame2dNode);

        final AsdfNode compositeNode = mock(AsdfNode.class);
        when(compositeNode.getTag()).thenReturn("tag:stsci.edu:gwcs/composite_frame-1.2.0");
        when(compositeNode.getString("name")).thenReturn("composite");
        when(compositeNode.get("frames")).thenReturn(framesSequence);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Frame frame = support.deserializeFrame(compositeNode);

        assertInstanceOf(CompositeFrame.class, frame);
        assertEquals("composite", frame.getName());
    }
}
