package edu.stsci.gwcs;

import edu.stsci.gwcs.coordinate.Frame;
import edu.stsci.gwcs.transform.Transform;
import lombok.NonNull;

public class Step {
    private final Frame frame;
    private final Transform transform;

    public Step(@NonNull final Frame frame, final Transform transform) {
        this.frame = frame;
        this.transform = transform;
    }

    public Frame getFrame() {
        return frame;
    }

    public Transform getTransform() {
        return transform;
    }

    public String getFrameName() {
        return frame.getName();
    }
}
