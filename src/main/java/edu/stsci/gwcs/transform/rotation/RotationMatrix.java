package edu.stsci.gwcs.transform.rotation;

import edu.stsci.gwcs.transform.util.WcsMath;

final class RotationMatrix {

    private RotationMatrix() {
    }

    static double[][] build(final double[] anglesDeg, final String axesOrder) {
        double[][] totalMatrix = {
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0}
        };

        for (int i = 0; i < anglesDeg.length; i++) {
            final char axis = axesOrder.charAt(i);
            final double cos = WcsMath.cosd(anglesDeg[i]);
            final double sin = WcsMath.sind(anglesDeg[i]);

            final double[][] rot = new double[3][3];

            if (axis == 'x') {
                rot[0][0] = 1.0; rot[0][1] = 0.0;  rot[0][2] = 0.0;
                rot[1][0] = 0.0; rot[1][1] = cos;   rot[1][2] = sin;
                rot[2][0] = 0.0; rot[2][1] = -sin;  rot[2][2] = cos;
            } else if (axis == 'y') {
                rot[0][0] = cos;  rot[0][1] = 0.0; rot[0][2] = -sin;
                rot[1][0] = 0.0;  rot[1][1] = 1.0; rot[1][2] = 0.0;
                rot[2][0] = sin;  rot[2][1] = 0.0; rot[2][2] = cos;
            } else if (axis == 'z') {
                rot[0][0] = cos;  rot[0][1] = sin;  rot[0][2] = 0.0;
                rot[1][0] = -sin; rot[1][1] = cos;  rot[1][2] = 0.0;
                rot[2][0] = 0.0;  rot[2][1] = 0.0;  rot[2][2] = 1.0;
            } else {
                throw new IllegalArgumentException("Invalid rotation axis: '" + axis + "' (expected 'x', 'y', or 'z')");
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

        return totalMatrix;
    }
}
