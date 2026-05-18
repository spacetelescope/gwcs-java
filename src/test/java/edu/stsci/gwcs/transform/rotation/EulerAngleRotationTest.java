package edu.stsci.gwcs.transform.rotation;

import edu.stsci.gwcs.transform.rotation.EulerAngleRotation.AxesOrder;
import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Numeric oracles in this class were generated from astropy.modeling.projections.EulerAngleRotation
 * (Pix2Sky_EulerAngleRotation / Sky2Pix_EulerAngleRotation). Regenerate via the project tooling if tolerances drift.
 */
class EulerAngleRotationTest {

    @Test
    void testIdentity() {
        final EulerAngleRotation rot = new EulerAngleRotation(0, 0, 0, AxesOrder.XYZ);

        assertEquals(2, rot.getInputCount());
        assertEquals(2, rot.getOutputCount());

        final double[] out = rot.evaluate(45.0, 30.0);
        assertEquals(45.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testKnownRotation() {
        final EulerAngleRotation rot = new EulerAngleRotation(90, 0, 0, AxesOrder.ZXZ);

        final double[] out = rot.evaluate(45.0, 30.0);
        assertEquals(-45.0, out[0], DOUBLE_TOLERANCE);
        assertEquals(30.0, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testRoundTrip() {
        final EulerAngleRotation rot = new EulerAngleRotation(30, 45, 60, AxesOrder.XYZ);
        assertTrue(rot.hasInverse());

        final Transform inverse = rot.getInverse();
        final double[] original = {123.0, -45.0};
        final double[] rotated = rot.evaluate(original);
        final double[] recovered = inverse.evaluate(rotated);

        assertEquals(original[0], recovered[0], DOUBLE_TOLERANCE);
        assertEquals(original[1], recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void testInputOutputCounts() {
        final EulerAngleRotation rot = new EulerAngleRotation(0, 0, 0, AxesOrder.XYZ);
        assertEquals(2, rot.getInputCount());
        assertEquals(2, rot.getOutputCount());
    }

    @Test
    void testHasInverse() {
        final EulerAngleRotation rot = new EulerAngleRotation(0, 0, 0, AxesOrder.XYZ);
        assertTrue(rot.hasInverse());
    }

    @Test
    void testStringOverloadRejectsInvalidAxisCharacter() {
        assertThrows(IllegalArgumentException.class,
                () -> new EulerAngleRotation(0, 0, 0, "xya"));
    }

    @Test
    void testStringOverloadRejectsWrongLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new EulerAngleRotation(0, 0, 0, "xy"));
    }

    @Test
    void testRejectsNaN() {
        assertThrows(IllegalArgumentException.class,
                () -> new EulerAngleRotation(Double.NaN, 0, 0, AxesOrder.XYZ));
    }

    @Test
    void testStringOverloadCaseInsensitive() {
        final EulerAngleRotation lower = new EulerAngleRotation(30, 45, 60, "xyz");
        final EulerAngleRotation upper = new EulerAngleRotation(30, 45, 60, "XYZ");

        final double[] outLower = lower.evaluate(45.0, 30.0);
        final double[] outUpper = upper.evaluate(45.0, 30.0);

        assertEquals(outLower[0], outUpper[0], DOUBLE_TOLERANCE);
        assertEquals(outLower[1], outUpper[1], DOUBLE_TOLERANCE);
    }

    @Test
    void zxzReferenceValue() {
        // Reference computed independently in Python via the rotation-matrix definition
        // (numpy direct vector rotation, no astropy involved).
        final EulerAngleRotation r = new EulerAngleRotation(30.0, 45.0, 60.0, AxesOrder.ZXZ);
        final double[] out = r.evaluate(10.0, 20.0);
        assertEquals(-59.05372219801787, out[0], DOUBLE_TOLERANCE);
        assertEquals(27.97618347870382, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void xyzReferenceValue() {
        // Independent xyz (Tait-Bryan) rotation reference.
        final EulerAngleRotation r = new EulerAngleRotation(10.0, 20.0, 30.0, AxesOrder.XYZ);
        final double[] out = r.evaluate(45.0, -15.0);
        assertEquals(9.202331425811616, out[0], DOUBLE_TOLERANCE);
        assertEquals(-6.739912326996273, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void zyzPoleInputReference() {
        // Pole input under zyz rotation. WcsMath gives cos(90°)=0 exactly so the
        // output is well-defined. +/-180 represent the same longitude.
        final EulerAngleRotation r = new EulerAngleRotation(90.0, 90.0, 0.0, AxesOrder.ZYZ);
        final double[] out = r.evaluate(0.0, 90.0);
        assertEquals(180.0, Math.abs(out[0]), DOUBLE_TOLERANCE);
        assertEquals(0.0, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void zxzNegativeDeltaReference() {
        final EulerAngleRotation r = new EulerAngleRotation(-45.0, 30.0, 60.0, AxesOrder.ZXZ);
        final double[] out = r.evaluate(120.0, -45.0);
        assertEquals(135.93866812284068, out[0], DOUBLE_TOLERANCE);
        assertEquals(-44.739033955734385, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void xyzPoleInputReference() {
        final EulerAngleRotation r = new EulerAngleRotation(10.0, 20.0, 30.0, AxesOrder.XYZ);
        final double[] out = r.evaluate(0.0, 90.0);
        assertEquals(122.72683044319636, out[0], DOUBLE_TOLERANCE);
        assertEquals(67.73125550470313, out[1], DOUBLE_TOLERANCE);
    }

    @Test
    void zyzSouthPoleRoundTrip() {
        final EulerAngleRotation r = new EulerAngleRotation(90.0, 90.0, 0.0, AxesOrder.ZYZ);
        final Transform inverse = r.getInverse();
        final double[] rotated = r.evaluate(0.0, -90.0);
        final double[] recovered = inverse.evaluate(rotated);
        assertEquals(0.0, recovered[0], DOUBLE_TOLERANCE);
        assertEquals(-90.0, recovered[1], DOUBLE_TOLERANCE);
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final EulerAngleRotation r = new EulerAngleRotation(10.0, 20.0, 30.0, AxesOrder.XYZ);
        final double[] inputs = new double[]{99.0, 99.0, 0.0, 90.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0, 77.0};
        r.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(122.72683044319636, outputs[1], DOUBLE_TOLERANCE);
        assertEquals(67.73125550470313, outputs[2], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[3]);
        assertEquals(77.0, outputs[4]);
    }

    @Test
    void axesOrderCodeRoundTrip() {
        for (final AxesOrder order : AxesOrder.values()) {
            assertEquals(order, AxesOrder.fromCode(order.code()));
        }
    }

    @Test
    void axesOrderFromCodeIsCaseInsensitive() {
        assertEquals(AxesOrder.ZXZ, AxesOrder.fromCode("ZXZ"));
        assertEquals(AxesOrder.ZXZ, AxesOrder.fromCode("zxz"));
        assertEquals(AxesOrder.ZXZ, AxesOrder.fromCode("Zxz"));
    }

    @Test
    void axesOrderFromCodeRejectsUnknown() {
        assertThrows(IllegalArgumentException.class, () -> AxesOrder.fromCode("abc"));
    }

    @Test
    void axesOrderReverseSwapsEndpoints() {
        assertEquals(AxesOrder.ZYX, AxesOrder.XYZ.reverse());
        assertEquals(AxesOrder.XYZ, AxesOrder.ZYX.reverse());
        // Symmetric orders are self-reverse
        assertEquals(AxesOrder.ZXZ, AxesOrder.ZXZ.reverse());
        assertEquals(AxesOrder.ZYZ, AxesOrder.ZYZ.reverse());
        assertEquals(AxesOrder.YZX, AxesOrder.XZY.reverse());
    }

    @Test
    void getInverseUsesParametricForm() {
        final EulerAngleRotation rot = new EulerAngleRotation(30, 45, 60, AxesOrder.XYZ);
        final EulerAngleRotation inverse = rot.getInverse();
        assertEquals(-60.0, inverse.getPhi(), DOUBLE_TOLERANCE);
        assertEquals(-45.0, inverse.getTheta(), DOUBLE_TOLERANCE);
        assertEquals(-30.0, inverse.getPsi(), DOUBLE_TOLERANCE);
        assertEquals(AxesOrder.ZYX, inverse.getAxesOrder());
    }

    @Test
    void parametricInverseRoundTripsAtCoordinateLevel() {
        // Test the parametric form across multiple Euler orders and inputs.
        final AxesOrder[] orders = AxesOrder.values();
        final double[][] inputs = {
                {0.0, 0.0},
                {45.0, 30.0},
                {-120.0, -60.0},
                {179.0, 89.0},
                {12.34, -56.78}
        };
        for (final AxesOrder order : orders) {
            final EulerAngleRotation rot = new EulerAngleRotation(11.0, 22.0, 33.0, order);
            final EulerAngleRotation inverse = rot.getInverse();
            for (final double[] input : inputs) {
                final double[] rotated = rot.evaluate(input);
                final double[] recovered = inverse.evaluate(rotated);
                assertEquals(input[0], recovered[0], 1e-9,
                        "lon mismatch for order " + order + " input " + input[0] + "," + input[1]);
                assertEquals(input[1], recovered[1], 1e-9,
                        "lat mismatch for order " + order + " input " + input[0] + "," + input[1]);
            }
        }
    }

    @Test
    void inverseOfInverseEqualsOriginalParametrically() {
        final EulerAngleRotation rot = new EulerAngleRotation(30, 45, 60, AxesOrder.XYZ);
        final EulerAngleRotation back = rot.getInverse().getInverse();
        assertEquals(rot.getPhi(), back.getPhi(), DOUBLE_TOLERANCE);
        assertEquals(rot.getTheta(), back.getTheta(), DOUBLE_TOLERANCE);
        assertEquals(rot.getPsi(), back.getPsi(), DOUBLE_TOLERANCE);
        assertEquals(rot.getAxesOrder(), back.getAxesOrder());
    }
}
