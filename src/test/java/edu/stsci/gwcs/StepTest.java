package edu.stsci.gwcs;

import edu.stsci.gwcs.frame.Frame2D;
import edu.stsci.gwcs.transform.Identity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepTest {
    private Frame2D makeFrame(final String name) {
        return new Frame2D(
                name,
                new String[]{"x", "y"},
                new int[]{0, 1},
                new String[]{"custom:x", "custom:y"},
                new String[]{"pixel", "pixel"}
        );
    }

    @Test
    void constructWithFrameAndTransform() {
        final Frame2D frame = makeFrame("detector");
        final Identity transform = new Identity(2);
        final Step step = new Step(frame, transform);

        assertSame(frame, step.getFrame());
        assertSame(transform, step.getTransform());
    }

    @Test
    void constructWithNullTransform() {
        final Frame2D frame = makeFrame("world");
        final Step step = new Step(frame, null);

        assertSame(frame, step.getFrame());
        assertNull(step.getTransform());
    }

    @Test
    void getFrameNameDelegatesToFrame() {
        final Frame2D frame = makeFrame("detector");
        final Step step = new Step(frame, new Identity(2));

        assertEquals("detector", step.getFrameName());
    }
}
