package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;
import edu.stsci.gwcs.transform.polynomial.Polynomial1D;
import edu.stsci.gwcs.transform.functional.Scale;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.geometry.SphericalToCartesian;

class ExplicitInverseWrapperTest {
    @Test
    void testDelegatesToForward() {
        final Transform forward = new Scale(3.0);
        final Transform inverse = new Scale(1.0 / 3.0);
        final ExplicitInverseWrapper wrapper = new ExplicitInverseWrapper(forward, inverse);

        assertEquals(1, wrapper.getInputCount());
        assertEquals(1, wrapper.getOutputCount());

        final double[] outputs = wrapper.evaluate(5.0);
        assertEquals(15.0, outputs[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testInverseIsAlwaysAvailable() {
        final Transform forward = new Polynomial1D(new double[]{1.0, 2.0}, null, null);
        final Transform inverse = new Shift(-1.0);
        final ExplicitInverseWrapper wrapper = new ExplicitInverseWrapper(forward, inverse);

        assertTrue(wrapper.hasInverse());
    }

    @Test
    void testRoundTripInversion() {
        final Transform forward = new Scale(5.0);
        final Transform inverse = new Scale(0.2);
        final ExplicitInverseWrapper wrapper = new ExplicitInverseWrapper(forward, inverse);

        final Transform recoveredInverse = wrapper.getInverse();
        final Transform recoveredForward = recoveredInverse.getInverse();

        final double[] result = recoveredForward.evaluate(4.0);
        assertEquals(20.0, result[0], DOUBLE_TOLERANCE);
    }

    @Test
    void testRejectsForwardToInverseMismatch() {
        final Transform oneToOne = new Scale(2.0);
        final Transform twoToTwo = new Identity(2);

        assertThrows(IllegalArgumentException.class,
                () -> new ExplicitInverseWrapper(oneToOne, twoToTwo));
    }

    @Test
    void testRejectsInverseToForwardMismatch() {
        final Transform twoToThree = new SphericalToCartesian();
        final Transform twoToTwo = new Identity(2);

        assertThrows(IllegalArgumentException.class,
                () -> new ExplicitInverseWrapper(twoToThree, twoToTwo));
    }

    @Test
    void testRejectsInverseOutputMismatchesForwardInput() {
        final Transform twoToTwo = new Identity(2);
        final Transform twoToThree = new SphericalToCartesian();

        assertThrows(IllegalArgumentException.class,
                () -> new ExplicitInverseWrapper(twoToTwo, twoToThree));
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final ExplicitInverseWrapper wrapper = new ExplicitInverseWrapper(new Scale(3.0), new Scale(1.0 / 3.0));
        final double[] sampleInputs = {5.0};
        final double[] expected = wrapper.evaluate(sampleInputs);

        final double[] inputs = new double[]{99.0, 99.0, 5.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        wrapper.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }
}
