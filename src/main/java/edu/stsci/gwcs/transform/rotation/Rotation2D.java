package edu.stsci.gwcs.transform.rotation;

import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

public class Rotation2D implements Transform {
    private final double angle;
    private final double cosAngle;
    private final double sinAngle;

    public double getAngle() {
        return angle;
    }

    public Rotation2D(final double angle) {
        if (!Double.isFinite(angle)) {
            throw new IllegalArgumentException("Rotation angle must be finite");
        }
        this.angle = angle;
        this.cosAngle = WcsMath.cosd(angle);
        this.sinAngle = WcsMath.sind(angle);
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];
        final double y = inputs[inputOffset + 1];
        outputs[outputOffset] = x * cosAngle - y * sinAngle;
        outputs[outputOffset + 1] = x * sinAngle + y * cosAngle;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        return new Rotation2D(-angle);
    }
}
