package edu.stsci.gwcs.transform.rotation;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;

public class RotateSequence3D implements Transform {

    private final double[] anglesDeg;
    private final String axesOrder;
    private final double m00, m01, m02;
    private final double m10, m11, m12;
    private final double m20, m21, m22;

    public RotateSequence3D(@NonNull final double[] anglesDeg, @NonNull final String axesOrder) {
        if (anglesDeg.length == 0) {
            throw new IllegalArgumentException("RotateSequence3D requires at least one rotation");
        }

        if (anglesDeg.length != axesOrder.length()) {
            throw new IllegalArgumentException("Number of angles must match the number of axes in axesOrder");
        }

        for (int i = 0; i < anglesDeg.length; i++) {
            if (!Double.isFinite(anglesDeg[i])) {
                throw new IllegalArgumentException("All angles must be finite (angle " + i + " was " + anglesDeg[i] + ")");
            }
        }

        final String order = axesOrder.toLowerCase();
        for (int i = 0; i < order.length(); i++) {
            final char c = order.charAt(i);
            if (c != 'x' && c != 'y' && c != 'z') {
                throw new IllegalArgumentException("Invalid rotation axis: " + axesOrder.charAt(i));
            }
        }

        this.anglesDeg = anglesDeg.clone();
        this.axesOrder = order;

        final double[][] m = RotationMatrix.build(anglesDeg, order);
        this.m00 = m[0][0]; this.m01 = m[0][1]; this.m02 = m[0][2];
        this.m10 = m[1][0]; this.m11 = m[1][1]; this.m12 = m[1][2];
        this.m20 = m[2][0]; this.m21 = m[2][1]; this.m22 = m[2][2];
    }

    public double[] getAnglesDeg() {
        return anglesDeg.clone();
    }

    public String getAxesOrder() {
        return axesOrder;
    }

    @Override
    public int getInputCount() {
        return 3;
    }

    @Override
    public int getOutputCount() {
        return 3;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];
        final double y = inputs[inputOffset + 1];
        final double z = inputs[inputOffset + 2];

        outputs[outputOffset]     = Math.fma(m00, x, Math.fma(m01, y, m02 * z));
        outputs[outputOffset + 1] = Math.fma(m10, x, Math.fma(m11, y, m12 * z));
        outputs[outputOffset + 2] = Math.fma(m20, x, Math.fma(m21, y, m22 * z));
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        final int n = anglesDeg.length;
        final double[] reversedAngles = new double[n];
        final StringBuilder reversedAxes = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            reversedAngles[i] = -anglesDeg[n - 1 - i];
            reversedAxes.append(axesOrder.charAt(n - 1 - i));
        }
        return new RotateSequence3D(reversedAngles, reversedAxes.toString());
    }
}
