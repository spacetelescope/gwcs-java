package edu.stsci.gwcs.asdf.converter.transform.polynomial;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.polynomial.Polynomial1D;
import edu.stsci.gwcs.transform.polynomial.Polynomial2D;
import org.asdfformat.asdf.ndarray.NdArray;
import org.asdfformat.asdf.ndarray.Shape;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolynomialConverterTest {
    @Test
    void deserializePolynomial1DWithDomainAndWindow() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/polynomial-1.2.0");

        final NdArray<?> coeffNd = mockNdArray1D(3);
        doReturn(new double[]{1.0, 2.0, 3.0}).when(coeffNd).toArray(any(double[].class));
        doReturn(coeffNd).when(node).getNdArray("coefficients");

        final AsdfNode domainNode = mock(AsdfNode.class);
        final AsdfNode domainLow = mock(AsdfNode.class);
        when(domainLow.asDouble()).thenReturn(-1.0);
        final AsdfNode domainHigh = mock(AsdfNode.class);
        when(domainHigh.asDouble()).thenReturn(1.0);
        when(domainNode.get(0)).thenReturn(domainLow);
        when(domainNode.get(1)).thenReturn(domainHigh);
        when(node.getOptional("domain")).thenReturn(Optional.of(domainNode));

        final AsdfNode windowNode = mock(AsdfNode.class);
        final AsdfNode windowLow = mock(AsdfNode.class);
        when(windowLow.asDouble()).thenReturn(-1.0);
        final AsdfNode windowHigh = mock(AsdfNode.class);
        when(windowHigh.asDouble()).thenReturn(1.0);
        when(windowNode.get(0)).thenReturn(windowLow);
        when(windowNode.get(1)).thenReturn(windowHigh);
        when(node.getOptional("window")).thenReturn(Optional.of(windowNode));

        stubNoNamedTransform(node);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Polynomial1D.class, transform);
        assertEquals(1, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
        assertArrayEquals(new double[]{17.0}, transform.evaluate(2.0), 1e-15);
    }

    @Test
    void deserializePolynomial1DWithoutDomainOrWindow() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/polynomial-1.2.0");

        final NdArray<?> coeffNd = mockNdArray1D(2);
        doReturn(new double[]{5.0, 3.0}).when(coeffNd).toArray(any(double[].class));
        doReturn(coeffNd).when(node).getNdArray("coefficients");

        when(node.getOptional("domain")).thenReturn(Optional.empty());
        when(node.getOptional("window")).thenReturn(Optional.empty());
        stubNoNamedTransform(node);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Polynomial1D.class, transform);
        assertArrayEquals(new double[]{17.0}, transform.evaluate(4.0), 1e-15);
    }

    @Test
    void deserializePolynomial1DConstant() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/polynomial-1.2.0");

        final NdArray<?> coeffNd = mockNdArray1D(1);
        doReturn(new double[]{42.0}).when(coeffNd).toArray(any(double[].class));
        doReturn(coeffNd).when(node).getNdArray("coefficients");

        when(node.getOptional("domain")).thenReturn(Optional.empty());
        when(node.getOptional("window")).thenReturn(Optional.empty());
        stubNoNamedTransform(node);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Polynomial1D.class, transform);
        assertArrayEquals(new double[]{42.0}, transform.evaluate(99.0), 1e-15);
    }

    @Test
    void deserializePolynomial2D() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/polynomial-1.2.0");

        final NdArray<?> coeffNd = mockNdArray2D(3);
        doReturn(new double[][]{
                {1.0, 2.0, 0.0},
                {3.0, 0.0, 0.0},
                {0.0, 0.0, 0.0}
        }).when(coeffNd).toArray(any(double[][].class));
        doReturn(coeffNd).when(node).getNdArray("coefficients");

        when(node.getOptional("domain")).thenReturn(Optional.empty());
        when(node.getOptional("window")).thenReturn(Optional.empty());
        stubNoNamedTransform(node);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Polynomial2D.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(1, transform.getOutputCount());
        assertArrayEquals(new double[]{13.0}, transform.evaluate(2.0, 3.0), 1e-15);
    }

    @Test
    void deserializePolynomial2DWithDomainAndWindow() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/polynomial-1.2.0");

        final NdArray<?> coeffNd = mockNdArray2D(2);
        doReturn(new double[][]{
                {0.0, 0.0},
                {1.0, 0.0}
        }).when(coeffNd).toArray(any(double[][].class));
        doReturn(coeffNd).when(node).getNdArray("coefficients");

        final AsdfNode domainNode = mock(AsdfNode.class);
        final AsdfNode xDomainPair = mockDoublePairNode(0.0, 10.0);
        final AsdfNode yDomainPair = mockDoublePairNode(0.0, 10.0);
        when(domainNode.get(0)).thenReturn(xDomainPair);
        when(domainNode.get(1)).thenReturn(yDomainPair);
        when(node.getOptional("domain")).thenReturn(Optional.of(domainNode));

        final AsdfNode windowNode = mock(AsdfNode.class);
        final AsdfNode xWindowPair = mockDoublePairNode(-1.0, 1.0);
        final AsdfNode yWindowPair = mockDoublePairNode(-1.0, 1.0);
        when(windowNode.get(0)).thenReturn(xWindowPair);
        when(windowNode.get(1)).thenReturn(yWindowPair);
        when(node.getOptional("window")).thenReturn(Optional.of(windowNode));

        stubNoNamedTransform(node);

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Polynomial2D.class, transform);
        assertArrayEquals(new double[]{0.0}, transform.evaluate(5.0, 5.0), 1e-15);
        assertArrayEquals(new double[]{1.0}, transform.evaluate(10.0, 5.0), 1e-15);
    }

    @Test
    void deserializePolynomialWithNamedTransform() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/polynomial-1.2.0");

        final NdArray<?> coeffNd = mockNdArray1D(2);
        doReturn(new double[]{1.0, 1.0}).when(coeffNd).toArray(any(double[].class));
        doReturn(coeffNd).when(node).getNdArray("coefficients");

        when(node.getOptional("domain")).thenReturn(Optional.empty());
        when(node.getOptional("window")).thenReturn(Optional.empty());

        final AsdfNode nameNode = mock(AsdfNode.class);
        when(nameNode.asString()).thenReturn("mypoly");
        when(node.getOptional("name")).thenReturn(Optional.of(nameNode));

        final AsdfNode inputsNode = mock(AsdfNode.class);
        when(inputsNode.asList(String.class)).thenReturn(List.of("x"));
        when(node.getOptional("inputs")).thenReturn(Optional.of(inputsNode));

        final AsdfNode outputsNode = mock(AsdfNode.class);
        when(outputsNode.asList(String.class)).thenReturn(List.of("y"));
        when(node.getOptional("outputs")).thenReturn(Optional.of(outputsNode));

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(NamedTransform.class, transform);
        final NamedTransform named = (NamedTransform) transform;
        assertEquals("mypoly", named.getName());
        assertArrayEquals(new String[]{"x"}, named.getInputNames());
        assertArrayEquals(new String[]{"y"}, named.getOutputNames());
        assertInstanceOf(Polynomial1D.class, named.getDelegate());
    }

    private static void stubNoNamedTransform(final AsdfNode node) {
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
    }

    private static AsdfNode mockDoublePairNode(final double low, final double high) {
        final AsdfNode pairNode = mock(AsdfNode.class);
        final AsdfNode lowNode = mock(AsdfNode.class);
        when(lowNode.asDouble()).thenReturn(low);
        final AsdfNode highNode = mock(AsdfNode.class);
        when(highNode.asDouble()).thenReturn(high);
        when(pairNode.get(0)).thenReturn(lowNode);
        when(pairNode.get(1)).thenReturn(highNode);
        return pairNode;
    }

    private static NdArray<?> mockNdArray1D(final int size) {
        final NdArray<?> ndArray = mock(NdArray.class);
        final Shape shape = mock(Shape.class);
        when(shape.get(0)).thenReturn(size);
        when(shape.getRank()).thenReturn(1);
        doReturn(shape).when(ndArray).getShape();
        return ndArray;
    }

    private static NdArray<?> mockNdArray2D(final int size) {
        final NdArray<?> ndArray = mock(NdArray.class);
        final Shape shape = mock(Shape.class);
        when(shape.get(0)).thenReturn(size);
        when(shape.getRank()).thenReturn(2);
        doReturn(shape).when(ndArray).getShape();
        return ndArray;
    }
}
