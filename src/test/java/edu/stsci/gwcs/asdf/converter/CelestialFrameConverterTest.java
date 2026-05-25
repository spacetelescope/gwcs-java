package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.coordinate.CelestialFrame;
import edu.stsci.gwcs.coordinate.Frame;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static edu.stsci.gwcs.asdf.converter.Frame2DConverterTest.mockFrame2DNode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CelestialFrameConverterTest {
    @Test
    void parseReferenceFrameFromTag() {
        assertEquals("ICRS", CelestialFrameConverter.parseReferenceFrame(
                "tag:astropy.org:astropy/coordinates/frames/icrs-1.1.0"));
        assertEquals("GALACTIC", CelestialFrameConverter.parseReferenceFrame(
                "tag:astropy.org:astropy/coordinates/frames/galactic-1.0.0"));
        assertEquals("FK5", CelestialFrameConverter.parseReferenceFrame(
                "tag:astropy.org:astropy/coordinates/frames/fk5-1.0.0"));
    }

    @Test
    void deserializeCelestialFrameWithICRS() {
        final AsdfNode node = mockCelestialFrameNode(
                "tag:stsci.edu:gwcs/celestial_frame-1.0.0",
                "world",
                List.of("lon", "lat"),
                List.of(0, 1),
                List.of("pos.eq.ra", "pos.eq.dec"),
                List.of("deg", "deg"),
                "tag:astropy.org:astropy/coordinates/frames/icrs-1.1.0"
        );

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Frame frame = support.deserializeFrame(node);

        assertInstanceOf(CelestialFrame.class, frame);
        final CelestialFrame celestialFrame = (CelestialFrame) frame;
        assertEquals("world", celestialFrame.getName());
        assertEquals("ICRS", celestialFrame.getReferenceFrame());
        assertArrayEquals(new String[]{"lon", "lat"}, celestialFrame.getAxisNames());
        assertArrayEquals(new int[]{0, 1}, celestialFrame.getAxisOrder());
        assertArrayEquals(new String[]{"pos.eq.ra", "pos.eq.dec"}, celestialFrame.getAxisPhysicalTypes());
        assertArrayEquals(new String[]{"deg", "deg"}, celestialFrame.getUnits());
    }

    @Test
    void deserializeCelestialFrameWithGalactic() {
        final AsdfNode node = mockCelestialFrameNode(
                "tag:stsci.edu:gwcs/celestial_frame-1.2.0",
                "galactic_frame",
                List.of("lon", "lat"),
                List.of(0, 1),
                List.of("pos.galactic.lon", "pos.galactic.lat"),
                List.of("deg", "deg"),
                "tag:astropy.org:astropy/coordinates/frames/galactic-1.0.0"
        );

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Frame frame = support.deserializeFrame(node);

        assertInstanceOf(CelestialFrame.class, frame);
        assertEquals("GALACTIC", ((CelestialFrame) frame).getReferenceFrame());
    }

    static AsdfNode mockCelestialFrameNode(final String tag,
                                           final String name,
                                           final List<String> axisNames,
                                           final List<Integer> axisOrder,
                                           final List<String> axisPhysicalTypes,
                                           final List<String> units,
                                           final String referenceFrameTag) {
        final AsdfNode node = mockFrame2DNode(tag, name, axisNames, axisOrder, axisPhysicalTypes, units);

        final AsdfNode refFrameNode = mock(AsdfNode.class);
        when(refFrameNode.getTag()).thenReturn(referenceFrameTag);
        when(node.get("reference_frame")).thenReturn(refFrameNode);

        return node;
    }
}
