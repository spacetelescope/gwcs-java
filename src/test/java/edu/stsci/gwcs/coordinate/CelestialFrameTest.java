package edu.stsci.gwcs.coordinate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CelestialFrameTest {
    @Test
    void validParameters() {
        final CelestialFrame frame = new CelestialFrame(
                "world",
                new String[]{"lon", "lat"},
                new int[]{0, 1},
                new String[]{"pos.eq.ra", "pos.eq.dec"},
                new String[]{"deg", "deg"},
                "ICRS"
        );

        assertEquals("world", frame.getName());
        assertEquals(2, frame.getAxisCount());
        assertArrayEquals(new int[]{0, 1}, frame.getAxisOrder());
        assertArrayEquals(new String[]{"lon", "lat"}, frame.getAxisNames());
        assertArrayEquals(new String[]{"pos.eq.ra", "pos.eq.dec"}, frame.getAxisPhysicalTypes());
        assertArrayEquals(new String[]{"deg", "deg"}, frame.getUnits());
        assertEquals("ICRS", frame.getReferenceFrame());
    }

    @Test
    void axisCountIsTwo() {
        final CelestialFrame frame = new CelestialFrame(
                "world",
                new String[]{"lon", "lat"},
                new int[]{0, 1},
                new String[]{"pos.eq.ra", "pos.eq.dec"},
                new String[]{"deg", "deg"},
                "ICRS"
        );

        assertEquals(2, frame.getAxisCount());
    }
}
