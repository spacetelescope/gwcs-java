package edu.stsci.gwcs.transform.functional;

import lombok.NonNull;
import org.ejml.data.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;
import edu.stsci.gwcs.transform.Transform;

public class Affine implements Transform {
    private final double[][] matrix;
    private final double[] translation;
    private final int dimensionCount;
    private final Affine inverse;

    public Affine(@NonNull final double[][] matrix, @NonNull final double[] translation) {
        this.dimensionCount = matrix.length;
        if (this.dimensionCount == 0 || translation.length != this.dimensionCount) {
            throw new IllegalArgumentException("Matrix must be N x N and translation must be length N");
        }

        for (final double[] doubles : matrix) {
            if (doubles == null || doubles.length != this.dimensionCount) {
                throw new IllegalArgumentException("Matrix must be square");
            }
            for (final double v : doubles) {
                if (!Double.isFinite(v)) {
                    throw new IllegalArgumentException("Matrix entries must be finite, got " + v);
                }
            }
        }

        for (final double v : translation) {
            if (!Double.isFinite(v)) {
                throw new IllegalArgumentException("Translation entries must be finite, got " + v);
            }
        }

        this.matrix = new double[this.dimensionCount][];
        for (int i = 0; i < this.dimensionCount; i++) {
            this.matrix[i] = matrix[i].clone();
        }
        this.translation = translation.clone();
        this.inverse = computeInverse();
    }

    private Affine(final double[][] matrix, final double[] translation, final Affine forward) {
        this.dimensionCount = matrix.length;
        this.matrix = matrix;
        this.translation = translation;
        this.inverse = forward;
    }

    private Affine computeInverse() {
        final SimpleMatrix m = new SimpleMatrix(this.matrix);
        final SimpleMatrix invM;
        try {
            invM = m.invert();
        } catch (final SingularMatrixException e) {
            return null;
        }

        final SimpleMatrix t = new SimpleMatrix(this.dimensionCount, 1, true, this.translation);
        final SimpleMatrix invT = invM.mult(t).scale(-1.0);

        return new Affine(invM.toArray2(), invT.transpose().toArray2()[0], this);
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
        return inverse != null;
    }

    @Override
    public Transform getInverse() {
        if (inverse == null) {
            throw new UnsupportedOperationException("Matrix cannot be inverted");
        }
        return inverse;
    }
}
