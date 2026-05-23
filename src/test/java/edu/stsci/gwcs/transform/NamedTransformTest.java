package edu.stsci.gwcs.transform;

import org.junit.jupiter.api.Test;

import static edu.stsci.gwcs.testing.TestingUtils.DOUBLE_TOLERANCE;
import static org.junit.jupiter.api.Assertions.*;

import edu.stsci.gwcs.transform.functional.Scale;
import edu.stsci.gwcs.transform.functional.Shift;
import edu.stsci.gwcs.transform.polynomial.Polynomial1D;

class NamedTransformTest {
    @Test
    void evaluationDelegatesToInnerTransform() {
        final NamedTransform named = new NamedTransform(
                new Shift(3.5), "myshift", new String[]{"x"}, new String[]{"y"});

        final double[] result = named.evaluate(2.0);
        assertEquals(5.5, result[0], DOUBLE_TOLERANCE);
    }

    @Test
    void gettersReturnMetadata() {
        final NamedTransform named = new NamedTransform(
                new Shift(1.0), "myshift", new String[]{"x"}, new String[]{"y"});

        assertEquals("myshift", named.getName());
        assertArrayEquals(new String[]{"x"}, named.getInputNames());
        assertArrayEquals(new String[]{"y"}, named.getOutputNames());
    }

    @Test
    void inverseIsNamedTransformWrappingInnerInverse() {
        final NamedTransform named = new NamedTransform(
                new Shift(5.0), "myshift", new String[]{"x"}, new String[]{"y"});

        assertTrue(named.hasInverse());
        final Transform inverse = named.getInverse();
        assertInstanceOf(NamedTransform.class, inverse);

        final NamedTransform namedInverse = (NamedTransform) inverse;
        assertEquals("myshift", namedInverse.getName());
        assertArrayEquals(new String[]{"y"}, namedInverse.getInputNames());
        assertArrayEquals(new String[]{"x"}, namedInverse.getOutputNames());

        final double[] result = inverse.evaluate(8.0);
        assertEquals(3.0, result[0], DOUBLE_TOLERANCE);
    }

    @Test
    void nullMetadataBehavesLikeUnwrappedTransform() {
        final Shift inner = new Shift(2.0);
        final NamedTransform named = new NamedTransform(inner, null, null, null);

        assertNull(named.getName());
        assertNull(named.getInputNames());
        assertNull(named.getOutputNames());

        assertEquals(inner.getInputCount(), named.getInputCount());
        assertEquals(inner.getOutputCount(), named.getOutputCount());

        final double[] expected = inner.evaluate(7.0);
        final double[] actual = named.evaluate(7.0);
        assertEquals(expected[0], actual[0], DOUBLE_TOLERANCE);
    }

    @Test
    void inverseOfNullMetadataPreservesNulls() {
        final NamedTransform named = new NamedTransform(new Shift(3.0), null, null, null);
        final Transform inverse = named.getInverse();

        assertInstanceOf(NamedTransform.class, inverse);
        final NamedTransform namedInverse = (NamedTransform) inverse;
        assertNull(namedInverse.getName());
        assertNull(namedInverse.getInputNames());
        assertNull(namedInverse.getOutputNames());

        final double[] result = inverse.evaluate(10.0);
        assertEquals(7.0, result[0], DOUBLE_TOLERANCE);
    }

    @Test
    void doubleInversionRestoresOriginalLabels() {
        final NamedTransform named = new NamedTransform(
                new Shift(5.0), "myshift", new String[]{"x"}, new String[]{"y"});

        final Transform roundTripped = named.getInverse().getInverse();
        assertInstanceOf(NamedTransform.class, roundTripped);

        final NamedTransform restored = (NamedTransform) roundTripped;
        assertEquals("myshift", restored.getName());
        assertArrayEquals(new String[]{"x"}, restored.getInputNames());
        assertArrayEquals(new String[]{"y"}, restored.getOutputNames());

        final double[] result = restored.evaluate(2.0);
        assertEquals(7.0, result[0], DOUBLE_TOLERANCE);
    }

    @Test
    void inputAndOutputCountsMatchDelegate() {
        final Identity identity = new Identity(3);
        final NamedTransform named = new NamedTransform(identity, "id3", null, null);

        assertEquals(3, named.getInputCount());
        assertEquals(3, named.getOutputCount());
    }

    @Test
    void hasInverseMatchesDelegate() {
        final Polynomial1D noInverse = new Polynomial1D(new double[]{1.0, 2.0}, null, null);
        final NamedTransform named = new NamedTransform(noInverse, "poly", null, null);

        assertFalse(named.hasInverse());
        assertThrows(UnsupportedOperationException.class, named::getInverse);
    }

    @Test
    void getDelegateReturnsInnerTransform() {
        final Shift inner = new Shift(1.0);
        final NamedTransform named = new NamedTransform(inner, "s", null, null);

        assertSame(inner, named.getDelegate());
    }

    @Test
    void offsetVariantEvaluateRespectsBuffers() {
        final NamedTransform named = new NamedTransform(
                new Scale(2.0), "myscale", new String[]{"x"}, new String[]{"y"});
        final double[] expected = named.evaluate(5.0);

        final double[] inputs = new double[]{99.0, 99.0, 5.0, 99.0};
        final double[] outputs = new double[]{77.0, 77.0, 77.0, 77.0};
        named.evaluate(inputs, 2, outputs, 1);

        assertEquals(77.0, outputs[0]);
        assertEquals(expected[0], outputs[1], DOUBLE_TOLERANCE);
        assertEquals(77.0, outputs[2]);
        assertEquals(77.0, outputs[3]);
    }

    @Test
    void gettersReturnDefensiveCopies() {
        final String[] inputNames = {"x"};
        final String[] outputNames = {"y"};
        final NamedTransform named = new NamedTransform(new Shift(1.0), "s", inputNames, outputNames);

        inputNames[0] = "modified";
        outputNames[0] = "modified";
        assertArrayEquals(new String[]{"x"}, named.getInputNames());
        assertArrayEquals(new String[]{"y"}, named.getOutputNames());

        named.getInputNames()[0] = "modified";
        named.getOutputNames()[0] = "modified";
        assertArrayEquals(new String[]{"x"}, named.getInputNames());
        assertArrayEquals(new String[]{"y"}, named.getOutputNames());
    }
}
