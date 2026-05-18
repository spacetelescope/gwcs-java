package edu.stsci.gwcs.transform.projection;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import edu.stsci.gwcs.transform.Transform;

/**
 * Contract test for {@link Projection} subclasses. Each concrete subclass test should extend this
 * class and override {@link #factory()} to provide a fresh projection instance and
 * {@link #roundTripSamples()} to supply the (phi, theta) inputs to round-trip.
 *
 * <p>The contracts asserted here:
 * <ul>
 *   <li>{@code getInputCount()} returns 2.</li>
 *   <li>{@code getOutputCount()} returns 2.</li>
 *   <li>{@code hasInverse()} returns true (override {@link #expectedHasInverse()} if the
 *       subclass doesn't support inversion).</li>
 *   <li>For each sample {@code (phi, theta)}, {@code inverse.evaluate(phi, theta)} composed with
 *       {@code forward.evaluate(...)} recovers the original within {@link #roundTripTolerance()}.</li>
 * </ul>
 */
public abstract class AbstractProjectionContractTest {

    protected abstract Projection factory();

    /**
     * (phi, theta) pairs in degrees to use as round-trip inputs to the inverse projection.
     */
    protected Stream<double[]> roundTripSamples() {
        return Stream.of(
                new double[]{0.0, 30.0},
                new double[]{45.0, 60.0},
                new double[]{-30.0, 45.0},
                new double[]{0.0, -30.0},
                new double[]{45.0, -60.0},
                new double[]{-30.0, -45.0}
        );
    }

    protected double roundTripTolerance() {
        return 1e-10;
    }

    protected boolean expectedHasInverse() {
        return true;
    }

    @TestFactory
    Stream<DynamicTest> projectionContract() {
        final List<DynamicTest> tests = new ArrayList<>();

        tests.add(DynamicTest.dynamicTest("getInputCount returns 2", () -> {
            assertEquals(2, factory().getInputCount());
        }));

        tests.add(DynamicTest.dynamicTest("getOutputCount returns 2", () -> {
            assertEquals(2, factory().getOutputCount());
        }));

        tests.add(DynamicTest.dynamicTest("hasInverse matches expectation", () -> {
            assertEquals(expectedHasInverse(), factory().hasInverse());
        }));

        if (expectedHasInverse()) {
            roundTripSamples().forEach(sample -> {
                final double phi = sample[0];
                final double theta = sample[1];
                tests.add(DynamicTest.dynamicTest(
                        String.format("round trip sky->pix->sky (phi=%.3f, theta=%.3f)", phi, theta), () -> {
                            final Projection forward = factory();
                            final Transform inverse = forward.getInverse();
                            assertNotNull(inverse, "Projection.getInverse() returned null");
                            final double[] pix = inverse.evaluate(phi, theta);
                            final double[] sky = forward.evaluate(pix);
                            final double phiDiff2 = Math.abs(((sky[0] - phi) % 360.0 + 540.0) % 360.0 - 180.0);
                            assertTrue(phiDiff2 <= roundTripTolerance(),
                                    () -> "phi round-trip diverged: expected " + phi + ", got " + sky[0]);
                            assertTrue(Math.abs(sky[1] - theta) <= roundTripTolerance(),
                                    () -> "theta round-trip diverged: expected " + theta + ", got " + sky[1]);
                        }));
                tests.add(DynamicTest.dynamicTest(
                        String.format("round trip pix->sky->pix (phi=%.3f, theta=%.3f)", phi, theta), () -> {
                            final Projection forward = factory();
                            final Transform inverse = forward.getInverse();
                            assertNotNull(inverse, "Projection.getInverse() returned null");
                            final double[] sky = forward.evaluate(phi, theta);
                            if (Double.isNaN(sky[0]) || Double.isNaN(sky[1])) {
                                return;
                            }
                            final double[] recovered = inverse.evaluate(sky);
                            final double tol = 2.0 * roundTripTolerance();
                            final double phiDiff = Math.abs(((recovered[0] - phi) % 360.0 + 540.0) % 360.0 - 180.0);
                            assertTrue(phiDiff <= tol,
                                    () -> "phi round-trip diverged: expected " + phi + ", got " + recovered[0]);
                            assertTrue(Math.abs(recovered[1] - theta) <= tol,
                                    () -> "theta round-trip diverged: expected " + theta + ", got " + recovered[1]);
                        }));
            });
        }

        // NaN-propagation contract: each input slot can be NaN; either NaN must drive both
        // outputs to NaN (forward and inverse). Matches astropy's "NaN in → NaN out" convention.
        final double[][] nanInputs = {
                {Double.NaN, 0.0},
                {0.0, Double.NaN},
                {Double.NaN, Double.NaN}
        };
        for (final double[] in : nanInputs) {
            final double a = in[0];
            final double b = in[1];
            tests.add(DynamicTest.dynamicTest(
                    String.format("forward propagates NaN (%s, %s)", a, b), () -> {
                        final Projection forward = factory();
                        final double[] out = forward.evaluate(a, b);
                        assertTrue(Double.isNaN(out[0]) && Double.isNaN(out[1]),
                                () -> "forward did not propagate NaN, got (" + out[0] + ", " + out[1] + ")");
                    }));
            if (expectedHasInverse()) {
                tests.add(DynamicTest.dynamicTest(
                        String.format("inverse propagates NaN (%s, %s)", a, b), () -> {
                            final Transform inverse = factory().getInverse();
                            final double[] out = inverse.evaluate(a, b);
                            assertTrue(Double.isNaN(out[0]) && Double.isNaN(out[1]),
                                    () -> "inverse did not propagate NaN, got (" + out[0] + ", " + out[1] + ")");
                        }));
            }
        }

        return tests.stream();
    }
}
