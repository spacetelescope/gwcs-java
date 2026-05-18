package edu.stsci.gwcs.transform.polynomial;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;

public class Polynomial1D implements Transform {
    private final double[] coefficients;

    private final int degree;
    private final double mapSlope;
    private final double mapIntercept;

    public Polynomial1D(@NonNull final double[] coefficients, double[] domain, double[] window) {
        if (coefficients.length == 0) {
            throw new IllegalArgumentException("Coefficients array cannot be empty");
        }

        this.coefficients = coefficients.clone();

        domain = (domain != null) ? domain : new double[]{-1.0, 1.0};
        window = (window != null) ? window : new double[]{-1.0, 1.0};

        if (domain.length != 2 || window.length != 2) {
            throw new IllegalArgumentException("Domain/window arrays must have length 2");
        }

        if (!Double.isFinite(domain[0]) || !Double.isFinite(domain[1])
                || !Double.isFinite(window[0]) || !Double.isFinite(window[1])) {
            throw new IllegalArgumentException("Domain and window values must be finite");
        }

        if (domain[0] == domain[1]) {
            throw new IllegalArgumentException("Domain endpoints must not be equal");
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