package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class Polynomial1D implements Transform {
    private final double[] coefficients;

    private final int degree;
    private final double mapSlope;
    private final double mapIntercept;

    public Polynomial1D(@NonNull final double[] coefficients, double[] domain, double[] window) {
        this.coefficients = coefficients;

        domain = (domain != null) ? domain : new double[]{-1.0, 1.0};
        window = (window != null) ? window : new double[]{-1.0, 1.0};

        if (domain.length != 2 || window.length != 2) {
            throw new IllegalArgumentException("Domain/window arrays must have length 2");
        }

        this.degree = this.coefficients.length - 1;
        this.mapSlope = (window[1] - window[0]) / (domain[1] - domain[0]);
        this.mapIntercept = window[0] - (this.mapSlope * domain[0]);
    }

    @Override
    public int getInputCount() {
        return 1;
    }

    @Override
    public int getOutputCount() {
        return 1;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double x = inputs[inputOffset];

        final double mappedX = Math.fma(x, mapSlope, mapIntercept);

        double result = coefficients[degree];
        for (int i = degree - 1; i >= 0; i--) {
            result = Math.fma(result, mappedX, coefficients[i]);
        }

        outputs[outputOffset] = result;
    }
}