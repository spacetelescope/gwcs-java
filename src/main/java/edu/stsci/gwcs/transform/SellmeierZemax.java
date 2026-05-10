package edu.stsci.gwcs.transform;

public class SellmeierZemax implements Transform {
    private static final double KELVIN_TO_CELSIUS = 273.15;

    private final double temperatureC;
    private final double refTemperatureC;
    private final double refPressure;
    private final double pressure;
    private final double[] bCoefficients;
    private final double[] cCoefficients;
    private final double d0;
    private final double d1;
    private final double d2;
    private final double e0;
    private final double e1;
    private final double lamTk;

    public SellmeierZemax(
            final double temperature,
            final double refTemperature,
            final double refPressure,
            final double pressure,
            final double[] bCoefficients,
            final double[] cCoefficients,
            final double[] dCoefficients,
            final double[] eCoefficients
    ) {
        if (bCoefficients == null || bCoefficients.length != 3) {
            throw new IllegalArgumentException("bCoefficients must have length 3");
        }
        if (cCoefficients == null || cCoefficients.length != 3) {
            throw new IllegalArgumentException("cCoefficients must have length 3");
        }
        if (dCoefficients == null || dCoefficients.length != 3) {
            throw new IllegalArgumentException("dCoefficients must have length 3");
        }
        if (eCoefficients == null || eCoefficients.length != 3) {
            throw new IllegalArgumentException("eCoefficients must have length 3");
        }

        this.temperatureC = temperature - KELVIN_TO_CELSIUS;
        this.refTemperatureC = refTemperature - KELVIN_TO_CELSIUS;
        this.refPressure = refPressure;
        this.pressure = pressure;
        this.bCoefficients = bCoefficients.clone();
        this.cCoefficients = cCoefficients.clone();
        this.d0 = dCoefficients[0];
        this.d1 = dCoefficients[1];
        this.d2 = dCoefficients[2];
        this.e0 = eCoefficients[0];
        this.e1 = eCoefficients[1];
        this.lamTk = eCoefficients[2];
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
        final double wavelength = inputs[inputOffset];
        final double lam2 = wavelength * wavelength;

        final double delta = temperatureC - refTemperatureC;

        // Edlen equation for refractive index of air
        final double nref = 1.0
                + (6432.8
                + 2949810.0 * lam2 / (146.0 * lam2 - 1.0)
                + 5540.0 * lam2 / (41.0 * lam2 - 1.0))
                * 1e-8;

        final double nairObs = 1.0 + ((nref - 1.0) * pressure) / (1.0 + (temperatureC - 15.0) * 3.4785e-3);
        final double nairRef = 1.0 + ((nref - 1.0) * refPressure) / (1.0 + (refTemperatureC - 15.0) * 3.4785e-3);

        final double lamrel = wavelength * nairObs / nairRef;
        final double nrel = SellmeierGlass.computeRefractiveIndex(lamrel, bCoefficients, cCoefficients);
        final double nabsRef = nrel * nairRef;

        final double lamrel2 = lamrel * lamrel;
        final double delnabs = (0.5 * (nrel * nrel - 1.0) / nrel)
                * (d0 * delta
                + d1 * delta * delta
                + d2 * delta * delta * delta
                + (e0 * delta + e1 * delta * delta) / (lamrel2 - lamTk * lamTk));

        final double nabsObs = nabsRef + delnabs;
        outputs[outputOffset] = nabsObs / nairObs;
    }
}
