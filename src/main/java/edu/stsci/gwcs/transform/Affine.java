package edu.stsci.gwcs.transform;

import edu.stsci.gwcs.util.DoubleUtils;
import lombok.NonNull;
import org.ejml.data.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;

public class Affine implements Transform {
    private final double[][] matrix;
    private final double[] translation;
    private final int dimensionCount;

    public Affine(@NonNull final double[][] matrix, @NonNull final double[] translation) {
        this.dimensionCount = matrix.length;
        if (this.dimensionCount == 0 || translation.length != this.dimensionCount) {
            throw new IllegalArgumentException("Matrix must be N x N and translation must be length N");
        }

        for (final double[] doubles : matrix) {
            if (doubles == null || doubles.length != this.dimensionCount) {
                throw new IllegalArgumentException("Matrix must be square");
            }
        }

        this.matrix = matrix;
        this.translation = translation;
    }

    @Override
    public int getInputCount() {
        return dimensionCount;
    }

    @Override
    public int getOutputCount() {
        return dimensionCount;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        for (int i = 0; i < dimensionCount; i++) {
            double sum = translation[i];
            for (int j = 0; j < dimensionCount; j++) {
                sum = Math.fma(matrix[i][j], inputs[inputOffset + j], sum);
            }
            outputs[outputOffset + i] = sum;
        }
    }

    @Override
    public boolean hasInverse() {
        final SimpleMatrix m = new SimpleMatrix(this.matrix);
        return Math.abs(m.determinant()) > DoubleUtils.EPSILON;
    }

    @Override
    public Transform getInverse() {
        final SimpleMatrix m = new SimpleMatrix(this.matrix);

        final SimpleMatrix invM;
        try {
            invM = m.invert();
        } catch (final SingularMatrixException e) {
            throw new UnsupportedOperationException(
                    "Matrix cannot be inverted"
            );
        }

        final SimpleMatrix t = new SimpleMatrix(this.dimensionCount, 1, true, this.translation);
        final SimpleMatrix invT = invM.mult(t).scale(-1.0);

        return new Affine(invM.toArray2(), invT.transpose().toArray2()[0]);
    }
}