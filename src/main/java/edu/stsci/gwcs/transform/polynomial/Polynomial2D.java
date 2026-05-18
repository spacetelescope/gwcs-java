package edu.stsci.gwcs.transform.polynomial;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;

public class Polynomial2D implements Transform {
    private final double[][] coefficients;
    private final int degree;

    private final double xMapSlope;
    private final double xMapIntercept;

    private final double yMapSlope;
    private final double yMapIntercept;

    public Polynomial2D(@NonNull final double[][] coefficients,
                        double[] xDomain, double[] yDomain,
                        double[] xWindow, double[] yWindow) {
        if (coefficients.length == 0) {
            throw new IllegalArgumentException("Coefficients array cannot be empty");
        }

        this.degree = coefficients.length - 1;

        for (final double[] coefficient : coefficients) {
            if (coefficient == null || coefficient.length != coefficients.length) {
                throw new IllegalArgumentException("Square coefficients matrix required");
            }
        }

        for (int i = 0; i <= degree; i++) {
            for (int j = degree - i + 1; j <= degree; j++) {
                if (coefficients[i][j] != 0.0) {
                    throw new IllegalArgumentException(
                            "Coefficient at [" + i + "][" + j + "] must be zero because i + j = "
                                    + (i + j) + " exceeds degree " + degree
                    );
                }
            }
        }

        this.coefficients = new double[coefficients.length][];
        for (int i = 0; i < coefficients.length; i++) {
            this.coefficients[i] = coefficients[i].clone();
        }

        xDomain = (xDomain != null) ? xDomain : new double[]{-1.0, 1.0};
        yDomain = (yDomain != null) ? yDomain : new double[]{-1.0, 1.0};
        xWindow = (xWindow != null) ? xWindow : new double[]{-1.0, 1.0};
        yWindow = (yWindow != null) ? yWindow : new double[]{-1.0, 1.0};

        if (xDomain.length != 2 || yDomain.length != 2 || xWindow.length != 2 || yWindow.length != 2) {
            throw new IllegalArgumentException("Domain/window arrays must have length 2");
        }

        if (!Double.isFinite(xDomain[0]) || !Double.isFinite(xDomain[1])
                || !Double.isFinite(yDomain[0]) || !Double.isFinite(yDomain[1])
                || !Double.isFinite(xWindow[0]) || !Double.isFinite(xWindow[1])
                || !Double.isFinite(yWindow[0]) || !Double.isFinite(yWindow[1])) {
            throw new IllegalArgumentException("Domain and window values must be finite");
        }

        if (xDomain[0] == xDomain[1] || yDomain[0] == yDomain[1]) {
            throw new IllegalArgumentException("Domain endpoints must not be equal");
        }

        this.xMapSlope = (xWindow[1] - xWindow[0]) / (xDomain[1] - xDomain[0]);
        this.xMapIntercept = xWindow[0] - (this.xMapSlope * xDomain[0]);

        this.yMapSlope = (yWindow[1] - yWindow[0]) / (yDomain[1] - yDomain[0]);
        this.yMapIntercept = yWindow[0] - (this.yMapSlope * yDomain[0]);
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 1;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];
        final double y = inputs[inputOffset + 1];

        final double mappedX = Math.fma(x, xMapSlope, xMapIntercept);
        final double mappedY = Math.fma(y, yMapSlope, yMapIntercept);

        double result = 0.0;

        for (int i = degree; i >= 0; i--) {
            double piY = coefficients[i][degree - i];
            for (int j = degree - i - 1; j >= 0; j--) {
                piY = Math.fma(piY, mappedY, coefficients[i][j]);
            }

            if (i == degree) {
                result = piY;
            } else {
                result = Math.fma(result, mappedX, piY);
            }
        }

        outputs[outputOffset] = result;
    }
}