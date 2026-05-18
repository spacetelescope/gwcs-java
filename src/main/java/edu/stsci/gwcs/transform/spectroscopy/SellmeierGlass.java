package edu.stsci.gwcs.transform.spectroscopy;

import edu.stsci.gwcs.transform.Transform;

/** Sellmeier equation for glass refractive index. Input wavelength in microns. */
public class SellmeierGlass implements Transform {
    private final double[] bCoefficients;
    private final double[] cCoefficients;

    public SellmeierGlass(final double[] bCoefficients, final double[] cCoefficients) {
        if (bCoefficients == null || bCoefficients.length != 3) {
            throw new IllegalArgumentException("bCoefficients must have length 3");
        }
        if (cCoefficients == null || cCoefficients.length != 3) {
            throw new IllegalArgumentException("cCoefficients must have length 3");
        }
        this.bCoefficients = bCoefficients.clone();
        this.cCoefficients = cCoefficients.clone();
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
        outputs[outputOffset] = computeRefractiveIndex(inputs[inputOffset], bCoefficients, cCoefficients);
    }

    static double computeRefractiveIndex(final double wavelength, final double[] b, final double[] c) {
        final double lam2 = wavelength * wavelength;
        final double nSquared = 1.0
                + b[0] * lam2 / (lam2 - c[0])
                + b[1] * lam2 / (lam2 - c[1])
                + b[2] * lam2 / (lam2 - c[2]);
        if (nSquared < 0.0) {
            return Double.NaN;
        }
        return Math.sqrt(nSquared);
    }
}
