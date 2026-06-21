package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.BoundingBoxWrapper;
import edu.stsci.gwcs.transform.ExplicitInverseWrapper;
import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Add;
import edu.stsci.gwcs.transform.compound.Compose;
import edu.stsci.gwcs.transform.compound.Concatenate;
import edu.stsci.gwcs.transform.compound.Divide;
import edu.stsci.gwcs.transform.compound.FixInputs;
import edu.stsci.gwcs.transform.compound.Multiply;
import edu.stsci.gwcs.transform.compound.Power;
import edu.stsci.gwcs.transform.compound.Subtract;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompoundConverterTest {
    private final GwcsAsdfSupport support = new GwcsAsdfSupport();

    private AsdfNode mockShiftNode(final double offset) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(offset);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());
        return node;
    }

    private AsdfNode mockScaleNode(final double factor) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/scale-1.3.0");
        when(node.getDouble("factor")).thenReturn(factor);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());
        return node;
    }

    private AsdfNode mockCompoundNode(final String tag, final AsdfNode left, final AsdfNode right) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn(tag);
        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(left);
        when(forwardNode.get(1L)).thenReturn(right);
        when(node.get("forward")).thenReturn(forwardNode);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());
        return node;
    }

    private AsdfNode mockInterval(final double lower, final double upper) {
        final AsdfNode lowerNode = mock(AsdfNode.class);
        when(lowerNode.asDouble()).thenReturn(lower);
        final AsdfNode upperNode = mock(AsdfNode.class);
        when(upperNode.asDouble()).thenReturn(upper);
        final AsdfNode interval = mock(AsdfNode.class);
        when(interval.get(0L)).thenReturn(lowerNode);
        when(interval.get(1L)).thenReturn(upperNode);
        return interval;
    }

    @Test
    void deserializeCompose() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Compose.class, transform);
        assertArrayEquals(new double[]{3.0}, transform.evaluate(0.0), 1e-15);
    }

    @Test
    void deserializeComposeVersion1_2_0() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.2.0",
                mockShiftNode(5.0),
                mockScaleNode(2.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Compose.class, transform);
        assertArrayEquals(new double[]{14.0}, transform.evaluate(2.0), 1e-15);
    }

    @Test
    void deserializeConcatenate() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
                mockShiftNode(1.0),
                mockScaleNode(2.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Concatenate.class, transform);
        assertArrayEquals(new double[]{1.0, 6.0}, transform.evaluate(0.0, 3.0), 1e-15);
    }

    @Test
    void deserializeAdd() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/add-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Add.class, transform);
        assertArrayEquals(new double[]{13.0}, transform.evaluate(5.0), 1e-15);
    }

    @Test
    void deserializeSubtract() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/subtract-1.3.0",
                mockShiftNode(10.0),
                mockShiftNode(3.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Subtract.class, transform);
        assertArrayEquals(new double[]{7.0}, transform.evaluate(0.0), 1e-15);
    }

    @Test
    void deserializeMultiply() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/multiply-1.3.0",
                mockScaleNode(3.0),
                mockScaleNode(4.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Multiply.class, transform);
        assertArrayEquals(new double[]{12.0}, transform.evaluate(1.0), 1e-15);
    }

    @Test
    void deserializeDivide() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/divide-1.3.0",
                mockScaleNode(6.0),
                mockScaleNode(3.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Divide.class, transform);
        assertArrayEquals(new double[]{2.0}, transform.evaluate(1.0), 1e-15);
    }

    @Test
    void deserializePower() {
        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/power-1.3.0",
                mockScaleNode(3.0),
                mockScaleNode(2.0));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Power.class, transform);
        assertArrayEquals(new double[]{9.0}, transform.evaluate(1.0), 1e-15);
    }

    @Test
    void deserializeFixInputs() {
        final AsdfNode identityNode = mock(AsdfNode.class);
        when(identityNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/identity-1.3.0");
        final AsdfNode nDimsNode = mock(AsdfNode.class);
        when(nDimsNode.asInt()).thenReturn(3);
        when(identityNode.getOptional("n_dims")).thenReturn(Optional.of(nDimsNode));
        when(identityNode.getOptional("name")).thenReturn(Optional.empty());
        when(identityNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(identityNode.getOptional("outputs")).thenReturn(Optional.empty());
        when(identityNode.getOptional("inverse")).thenReturn(Optional.empty());
        when(identityNode.getOptional("bounding_box")).thenReturn(Optional.empty());

        final AsdfNode mappingNode = mock(AsdfNode.class);
        final AsdfNode keysNode = mock(AsdfNode.class);
        when(keysNode.isNdArray()).thenReturn(false);
        when(keysNode.asList(Integer.class)).thenReturn(List.of(1));
        when(mappingNode.get("keys")).thenReturn(keysNode);
        final AsdfNode valuesNode = mock(AsdfNode.class);
        when(valuesNode.isNdArray()).thenReturn(false);
        when(valuesNode.asList(Double.class)).thenReturn(List.of(99.0));
        when(mappingNode.get("values")).thenReturn(valuesNode);

        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(identityNode);
        when(forwardNode.get(1L)).thenReturn(mappingNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/fix_inputs-1.3.0");
        when(node.get("forward")).thenReturn(forwardNode);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(FixInputs.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(3, transform.getOutputCount());
        assertArrayEquals(new double[]{10.0, 99.0, 20.0}, transform.evaluate(10.0, 20.0), 1e-15);
    }

    @Test
    void deserializeFixInputsMultipleFixedInputs() {
        final AsdfNode identityNode = mock(AsdfNode.class);
        when(identityNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/identity-1.3.0");
        final AsdfNode nDimsNode = mock(AsdfNode.class);
        when(nDimsNode.asInt()).thenReturn(4);
        when(identityNode.getOptional("n_dims")).thenReturn(Optional.of(nDimsNode));
        when(identityNode.getOptional("name")).thenReturn(Optional.empty());
        when(identityNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(identityNode.getOptional("outputs")).thenReturn(Optional.empty());
        when(identityNode.getOptional("inverse")).thenReturn(Optional.empty());
        when(identityNode.getOptional("bounding_box")).thenReturn(Optional.empty());

        final AsdfNode mappingNode = mock(AsdfNode.class);
        final AsdfNode keysNode = mock(AsdfNode.class);
        when(keysNode.isNdArray()).thenReturn(false);
        when(keysNode.asList(Integer.class)).thenReturn(List.of(0, 2));
        when(mappingNode.get("keys")).thenReturn(keysNode);
        final AsdfNode valuesNode = mock(AsdfNode.class);
        when(valuesNode.isNdArray()).thenReturn(false);
        when(valuesNode.asList(Double.class)).thenReturn(List.of(5.0, 15.0));
        when(mappingNode.get("values")).thenReturn(valuesNode);

        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(identityNode);
        when(forwardNode.get(1L)).thenReturn(mappingNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/fix_inputs-1.3.0");
        when(node.get("forward")).thenReturn(forwardNode);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(FixInputs.class, transform);
        assertEquals(2, transform.getInputCount());
        assertEquals(4, transform.getOutputCount());
        // inputs[0]=5.0 (fixed), inputs[1]=10.0 (from arg 0), inputs[2]=15.0 (fixed), inputs[3]=20.0 (from arg 1)
        assertArrayEquals(new double[]{5.0, 10.0, 15.0, 20.0}, transform.evaluate(10.0, 20.0), 1e-15);
    }

    @Test
    void deserializeDeeplyNestedCompose() {
        final AsdfNode inner = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));
        final AsdfNode outer = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.3.0",
                inner,
                mockShiftNode(3.0));

        final Transform transform = support.deserializeTransform(outer);

        assertInstanceOf(Compose.class, transform);
        assertArrayEquals(new double[]{6.0}, transform.evaluate(0.0), 1e-15);
    }

    @Test
    void deserializeTransformWithExplicitInverse() {
        final AsdfNode inverseShiftNode = mockShiftNode(-1.0);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(1.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.of(inverseShiftNode));
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(ExplicitInverseWrapper.class, transform);
        assertArrayEquals(new double[]{6.0}, transform.evaluate(5.0), 1e-15);
        assertTrue(transform.hasInverse());
        assertArrayEquals(new double[]{5.0}, transform.getInverse().evaluate(6.0), 1e-15);
    }

    @Test
    void deserializeTransformWithBoundingBox() {
        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        when(intervalsNode.get("x")).thenReturn(intervalX);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode inputsLabelNode = mock(AsdfNode.class);
        when(inputsLabelNode.asList(String.class)).thenReturn(List.of("x"));

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(1.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.of(inputsLabelNode));
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(NamedTransform.class, transform);
        final NamedTransform named = (NamedTransform) transform;
        assertInstanceOf(BoundingBoxWrapper.class, named.getDelegate());

        assertArrayEquals(new double[]{1.0}, transform.evaluate(0.0), 1e-15);
        assertArrayEquals(new double[]{4088.5}, transform.evaluate(4087.5), 1e-15);
        assertTrue(Double.isNaN(transform.evaluate(-1.0)[0]));
        assertTrue(Double.isNaN(transform.evaluate(4088.0)[0]));
    }

    @Test
    void deserializeTransformWithBoundingBoxWithoutInputLabels() {
        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        @SuppressWarnings("unchecked")
        final Iterator<AsdfNode> iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(intervalX);
        when(intervalsNode.iterator()).thenReturn(iterator);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(1.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(BoundingBoxWrapper.class, transform);
        assertArrayEquals(new double[]{1.0}, transform.evaluate(0.0), 1e-15);
        assertTrue(Double.isNaN(transform.evaluate(-1.0)[0]));
        assertTrue(Double.isNaN(transform.evaluate(4088.0)[0]));
    }

    @Test
    void deserializeTransformWithBoundingBox2D() {
        final AsdfNode intervalX0 = mockInterval(-0.5, 4087.5);
        final AsdfNode intervalX1 = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        when(intervalsNode.get("x0")).thenReturn(intervalX0);
        when(intervalsNode.get("x1")).thenReturn(intervalX1);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode inputsLabelNode = mock(AsdfNode.class);
        when(inputsLabelNode.asList(String.class)).thenReturn(List.of("x0", "x1"));

        final AsdfNode concatNode = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));
        when(concatNode.getOptional("inputs")).thenReturn(Optional.of(inputsLabelNode));
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final Transform transform = support.deserializeTransform(concatNode);

        assertInstanceOf(NamedTransform.class, transform);
        assertArrayEquals(new double[]{1.0, 2.0}, transform.evaluate(0.0, 0.0), 1e-15);
        assertTrue(Double.isNaN(transform.evaluate(-1.0, 0.0)[0]));
        assertTrue(Double.isNaN(transform.evaluate(0.0, 5000.0)[0]));
    }

    @Test
    void deserializeTransformWithBoundingBox2DWithoutInputLabels() {
        final AsdfNode intervalX0 = mockInterval(-0.5, 4087.5);
        final AsdfNode intervalX1 = mockInterval(-0.5, 2043.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        @SuppressWarnings("unchecked")
        final Iterator<AsdfNode> iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(intervalX0, intervalX1);
        when(intervalsNode.iterator()).thenReturn(iterator);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode concatNode = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final Transform transform = support.deserializeTransform(concatNode);

        assertInstanceOf(BoundingBoxWrapper.class, transform);
        assertArrayEquals(new double[]{1.0, 2.0}, transform.evaluate(0.0, 0.0), 1e-15);
        assertTrue(Double.isNaN(transform.evaluate(-1.0, 0.0)[0]));
        assertTrue(Double.isNaN(transform.evaluate(0.0, 2044.0)[0]));
    }

    @Test
    void deserializeTransformWithInverseAndBoundingBoxStacksCorrectly() {
        final AsdfNode inverseShiftNode = mockShiftNode(-1.0);

        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        when(intervalsNode.get("x")).thenReturn(intervalX);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode inputsLabelNode = mock(AsdfNode.class);
        when(inputsLabelNode.asList(String.class)).thenReturn(List.of("x"));

        final AsdfNode nameNode = mock(AsdfNode.class);
        when(nameNode.asString()).thenReturn("myshift");

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(1.0);
        when(node.getOptional("name")).thenReturn(Optional.of(nameNode));
        when(node.getOptional("inputs")).thenReturn(Optional.of(inputsLabelNode));
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.of(inverseShiftNode));
        when(node.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(NamedTransform.class, transform);
        final NamedTransform named = (NamedTransform) transform;
        assertEquals("myshift", named.getName());

        assertInstanceOf(BoundingBoxWrapper.class, named.getDelegate());
        final BoundingBoxWrapper bboxWrapper = (BoundingBoxWrapper) named.getDelegate();

        assertInstanceOf(ExplicitInverseWrapper.class, bboxWrapper.getDelegate());

        assertArrayEquals(new double[]{6.0}, transform.evaluate(5.0), 1e-15);
        assertTrue(Double.isNaN(transform.evaluate(-1.0)[0]));
    }

    @Test
    void deserializeComposeWithBoundingBox() {
        final AsdfNode intervalX = mockInterval(-10.0, 10.0);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        when(intervalsNode.get("x")).thenReturn(intervalX);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode inputsLabelNode = mock(AsdfNode.class);
        when(inputsLabelNode.asList(String.class)).thenReturn(List.of("x"));

        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.3.0",
                mockShiftNode(1.0),
                mockScaleNode(2.0));
        when(node.getOptional("inputs")).thenReturn(Optional.of(inputsLabelNode));
        when(node.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(NamedTransform.class, transform);
        final NamedTransform named = (NamedTransform) transform;
        assertInstanceOf(BoundingBoxWrapper.class, named.getDelegate());

        // Compose(Shift(1), Scale(2)).evaluate(5) = Scale(2).evaluate(Shift(1).evaluate(5)) = 12
        assertArrayEquals(new double[]{12.0}, transform.evaluate(5.0), 1e-15);
        // Out of bounding box
        assertTrue(Double.isNaN(transform.evaluate(11.0)[0]));
    }

    @Test
    void deserializeComposeWithExplicitInverse() {
        final AsdfNode inverseNode = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.3.0",
                mockScaleNode(0.5),
                mockShiftNode(-1.0));

        final AsdfNode node = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/compose-1.3.0",
                mockShiftNode(1.0),
                mockScaleNode(2.0));
        when(node.getOptional("inverse")).thenReturn(Optional.of(inverseNode));

        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(ExplicitInverseWrapper.class, transform);
        assertArrayEquals(new double[]{12.0}, transform.evaluate(5.0), 1e-15);
        assertTrue(transform.hasInverse());
        assertArrayEquals(new double[]{5.0}, transform.getInverse().evaluate(12.0), 1e-15);
    }

    @Test
    void deserializeFixInputsKeyValueSizeMismatchThrows() {
        final AsdfNode identityNode = mock(AsdfNode.class);
        when(identityNode.getTag()).thenReturn("tag:stsci.edu:asdf/transform/identity-1.3.0");
        final AsdfNode nDimsNode = mock(AsdfNode.class);
        when(nDimsNode.asInt()).thenReturn(3);
        when(identityNode.getOptional("n_dims")).thenReturn(Optional.of(nDimsNode));
        when(identityNode.getOptional("name")).thenReturn(Optional.empty());
        when(identityNode.getOptional("inputs")).thenReturn(Optional.empty());
        when(identityNode.getOptional("outputs")).thenReturn(Optional.empty());
        when(identityNode.getOptional("inverse")).thenReturn(Optional.empty());
        when(identityNode.getOptional("bounding_box")).thenReturn(Optional.empty());

        final AsdfNode mappingNode = mock(AsdfNode.class);
        final AsdfNode keysNode = mock(AsdfNode.class);
        when(keysNode.isNdArray()).thenReturn(false);
        when(keysNode.asList(Integer.class)).thenReturn(List.of(0, 1));
        when(mappingNode.get("keys")).thenReturn(keysNode);
        final AsdfNode valuesNode = mock(AsdfNode.class);
        when(valuesNode.isNdArray()).thenReturn(false);
        when(valuesNode.asList(Double.class)).thenReturn(List.of(99.0));
        when(mappingNode.get("values")).thenReturn(valuesNode);

        final AsdfNode forwardNode = mock(AsdfNode.class);
        when(forwardNode.get(0L)).thenReturn(identityNode);
        when(forwardNode.get(1L)).thenReturn(mappingNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/fix_inputs-1.3.0");
        when(node.get("forward")).thenReturn(forwardNode);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> support.deserializeTransform(node));
    }

    @Test
    void deserializeBoundingBoxMoreIntervalsThanInputsThrows() {
        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        @SuppressWarnings("unchecked")
        final Iterator<AsdfNode> iterator = mock(Iterator.class);
        // Provide 2 intervals for a 1-input transform
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(intervalX, intervalX);
        when(intervalsNode.iterator()).thenReturn(iterator);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/shift-1.3.0");
        when(node.getDouble("offset")).thenReturn(1.0);
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());
        when(node.getOptional("inverse")).thenReturn(Optional.empty());
        when(node.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        assertThrows(IllegalArgumentException.class, () -> support.deserializeTransform(node));
    }

    @Test
    void deserializeBoundingBoxFewerIntervalsThanInputsThrows() {
        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        @SuppressWarnings("unchecked")
        final Iterator<AsdfNode> iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(intervalX);
        when(intervalsNode.iterator()).thenReturn(iterator);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode concatNode = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        assertThrows(IllegalArgumentException.class, () -> support.deserializeTransform(concatNode));
    }

    @Test
    void deserializeLabeledBoundingBoxLabelCountMismatchThrows() {
        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        when(intervalsNode.get("x")).thenReturn(intervalX);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode inputsLabelNode = mock(AsdfNode.class);
        when(inputsLabelNode.asList(String.class)).thenReturn(List.of("x", "y", "z"));

        final AsdfNode concatNode = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));
        when(concatNode.getOptional("inputs")).thenReturn(Optional.of(inputsLabelNode));
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> support.deserializeTransform(concatNode));
        assertTrue(ex.getMessage().contains("does not match transform input count"));
    }

    @Test
    void deserializeLabeledBoundingBoxMissingIntervalKeyThrows() {
        final AsdfNode intervalX = mockInterval(-0.5, 4087.5);

        final AsdfNode intervalsNode = mock(AsdfNode.class);
        when(intervalsNode.get("x")).thenReturn(intervalX);
        when(intervalsNode.get("y")).thenReturn(null);

        final AsdfNode bboxNode = mock(AsdfNode.class);
        when(bboxNode.get("intervals")).thenReturn(intervalsNode);

        final AsdfNode inputsLabelNode = mock(AsdfNode.class);
        when(inputsLabelNode.asList(String.class)).thenReturn(List.of("x", "y"));

        final AsdfNode concatNode = mockCompoundNode(
                "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
                mockShiftNode(1.0),
                mockShiftNode(2.0));
        when(concatNode.getOptional("inputs")).thenReturn(Optional.of(inputsLabelNode));
        when(concatNode.getOptional("bounding_box")).thenReturn(Optional.of(bboxNode));

        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> support.deserializeTransform(concatNode));
        assertTrue(ex.getMessage().contains("missing key 'y'"));
    }
}
