package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class RotateSequence3D implements Transform {

    // Flattened 3x3 combined rotation matrix
    private final double m00, m01, m02;
    private final double m10, m11, m12;
    private final double m20, m21, m22;

    public RotateSequence3D(@NonNull final double[] anglesDeg, @NonNull final String axesOrder) {
        if (anglesDeg.length != axesOrder.length()) {
            throw new IllegalArgumentException("Number of angles must match the number of axes in axesOrder");
        }

        double[][] totalMatrix = {
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0}
        };

        final String order = axesOrder.toLowerCase();

        for (int i = 0; i < anglesDeg.length; i++) {
            final char axis = order.charAt(i);
            final double theta = Math.toRadians(anglesDeg[i]);
            final double cos = Math.cos(theta);
            final double sin = Math.sin(theta);

            final double[][] rot = new double[3][3];

            if (axis == 'x') {
                rot[0][0] = 1.0; rot[0][1] = 0.0;  rot[0][2] = 0.0;
                rot[1][0] = 0.0; rot[1][1] = cos;  rot[1][2] = -sin;
                rot[2][0] = 0.0; rot[2][1] = sin;  rot[2][2] = cos;
            } else if (axis == 'y') {
                rot[0][0] = cos;  rot[0][1] = 0.0; rot[0][2] = sin;
                rot[1][0] = 0.0;  rot[1][1] = 1.0; rot[1][2] = 0.0;
                rot[2][0] = -sin; rot[2][1] = 0.0; rot[2][2] = cos;
            } else if (axis == 'z') {
                rot[0][0] = cos;  rot[0][1] = -sin; rot[0][2] = 0.0;
                rot[1][0] = sin;  rot[1][1] = cos;  rot[1][2] = 0.0;
                rot[2][0] = 0.0;  rot[2][1] = 0.0;  rot[2][2] = 1.0;
            } else {
                throw new IllegalArgumentException("Invalid rotation axis: " + axis);
            }

            final double[][] nextMatrix = new double[3][3];
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    nextMatrix[r][c] = rot[r][0] * totalMatrix[0][c] +
                            rot[r][1] * totalMatrix[1][c] +
                            rot[r][2] * totalMatrix[2][c];
                }
            }
            totalMatrix = nextMatrix;
        }

        this.m00 = totalMatrix[0][0]; this.m01 = totalMatrix[0][1]; this.m02 = totalMatrix[0][2];
        this.m10 = totalMatrix[1][0]; this.m11 = totalMatrix[1][1]; this.m12 = totalMatrix[1][2];
        this.m20 = totalMatrix[2][0]; this.m21 = totalMatrix[2][1]; this.m22 = totalMatrix[2][2];
    }

    private RotateSequence3D(final double m00, final double m01, final double m02,
                             final double m10, final double m11, final double m12,
                             final double m20, final double m21, final double m22) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02;
        this.m10 = m10; this.m11 = m11; this.m12 = m12;
        this.m20 = m20; this.m21 = m21; this.m22 = m22;
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
        return new RotateSequence3D(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }
}
