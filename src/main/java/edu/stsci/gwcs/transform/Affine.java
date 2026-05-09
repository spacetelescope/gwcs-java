package edu.stsci.gwcs.transform;

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

        this.matrix = new double[this.dimensionCount][];
        for (int i = 0; i < this.dimensionCount; i++) {
            this.matrix[i] = matrix[i].clone();
        }
        this.translation = translation.clone();
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
        final double[] localInputs = new double[dimensionCount];
        System.arraycopy(inputs, inputOffset, localInputs, 0, dimensionCount);

        for (int i = 0; i < dimensionCount; i++) {
            double sum = translation[i];
            for (int j = 0; j < dimensionCount; j++) {
                sum = Math.fma(matrix[i][j], localInputs[j], sum);
            }
            outputs[outputOffset + i] = sum;
        }
    }

    @Override
    public boolean hasInverse() {
        try {
            new SimpleMatrix(this.matrix).invert();
            return true;
        } catch (final SingularMatrixException e) {
            return false;
        }
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