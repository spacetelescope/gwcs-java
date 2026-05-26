package edu.stsci.gwcs.asdf;

import edu.stsci.gwcs.Wcs;
import edu.stsci.gwcs.asdf.converter.frame.CelestialFrameConverter;
import edu.stsci.gwcs.asdf.converter.frame.CompositeFrameConverter;
import edu.stsci.gwcs.asdf.converter.frame.Frame2DConverter;
import edu.stsci.gwcs.asdf.converter.transform.ConstantConverter;
import edu.stsci.gwcs.asdf.converter.transform.IdentityConverter;
import edu.stsci.gwcs.asdf.converter.transform.RemapAxesConverter;
import edu.stsci.gwcs.asdf.converter.transform.fits.FitsWcsImagingConverter;
import edu.stsci.gwcs.asdf.converter.transform.functional.AffineConverter;
import edu.stsci.gwcs.asdf.converter.transform.functional.ScaleConverter;
import edu.stsci.gwcs.asdf.converter.transform.functional.ShiftConverter;
import edu.stsci.gwcs.asdf.converter.transform.geometry.DirectionCosinesConverter;
import edu.stsci.gwcs.asdf.converter.transform.geometry.SphericalCartesianConverter;
import edu.stsci.gwcs.asdf.converter.transform.polynomial.PolynomialConverter;
import edu.stsci.gwcs.asdf.converter.transform.projection.ProjectionConverter;
import edu.stsci.gwcs.asdf.converter.transform.rotation.Rotate3DConverter;
import edu.stsci.gwcs.asdf.converter.transform.rotation.RotateSequence3DConverter;
import edu.stsci.gwcs.asdf.converter.transform.rotation.Rotation2DConverter;
import edu.stsci.gwcs.asdf.converter.transform.selector.LabelMapperDictConverter;
import edu.stsci.gwcs.asdf.converter.transform.selector.RegionsSelectorConverter;
import edu.stsci.gwcs.asdf.converter.transform.spectroscopy.GratingEquationConverter;
import edu.stsci.gwcs.asdf.converter.transform.spectroscopy.SellmeierGlassConverter;
import edu.stsci.gwcs.asdf.converter.transform.spectroscopy.SellmeierZemaxConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.AddConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.ComposeConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.ConcatenateConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.DivideConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.FixInputsConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.MultiplyConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.PowerConverter;
import edu.stsci.gwcs.asdf.converter.transform.compound.SubtractConverter;
import edu.stsci.gwcs.asdf.converter.transform.spectroscopy.Snell3DConverter;
import edu.stsci.gwcs.asdf.converter.transform.tabular.Tabular1DConverter;
import edu.stsci.gwcs.frame.Frame;
import edu.stsci.gwcs.transform.BoundingBoxWrapper;
import edu.stsci.gwcs.transform.ExplicitInverseWrapper;
import edu.stsci.gwcs.transform.NamedTransform;
import edu.stsci.gwcs.transform.Transform;
import lombok.NonNull;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Optional;

public class GwcsAsdfSupport {
    private static final GwcsAsdfSupport DEFAULT = new GwcsAsdfSupport();

    private final TagRegistry registry;

    public static GwcsAsdfSupport instance() {
        return DEFAULT;
    }

    public GwcsAsdfSupport() {
        registry = new TagRegistry();
        registerConverters();
    }

    public GwcsAsdfSupport(final TagRegistry registry) {
        this.registry = registry;
    }

    private void registerConverters() {
        registerFrameConverters();
        registerTransformConverters();
    }

    private void registerFrameConverters() {
        registry.register(new Frame2DConverter(this));
        registry.register(new CelestialFrameConverter(this));
        registry.register(new CompositeFrameConverter(this));
    }

    private void registerTransformConverters() {
        registry.register(new ShiftConverter(this));
        registry.register(new ScaleConverter(this));
        registry.register(new IdentityConverter(this));
        registry.register(new ConstantConverter(this));
        registry.register(new RemapAxesConverter(this));
        registry.register(new AffineConverter(this));
        registry.register(new PolynomialConverter(this));
        registry.register(new Tabular1DConverter(this));
        registry.register(new RotateSequence3DConverter(this));
        registry.register(new Rotate3DConverter(this));
        registry.register(new Rotation2DConverter(this));
        registry.register(new SphericalCartesianConverter(this));
        registry.register(new DirectionCosinesConverter(this));
        registry.register(new ProjectionConverter(this));
        registry.register(new FitsWcsImagingConverter(this));
        registry.register(new GratingEquationConverter(this));
        registry.register(new SellmeierGlassConverter(this));
        registry.register(new SellmeierZemaxConverter(this));
        registry.register(new Snell3DConverter(this));
        registry.register(new LabelMapperDictConverter(this));
        registry.register(new RegionsSelectorConverter(this));
        registry.register(new ComposeConverter(this));
        registry.register(new ConcatenateConverter(this));
        registry.register(new AddConverter(this));
        registry.register(new SubtractConverter(this));
        registry.register(new MultiplyConverter(this));
        registry.register(new DivideConverter(this));
        registry.register(new PowerConverter(this));
        registry.register(new FixInputsConverter(this));
    }

    public Wcs deserializeWcs(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Wcs.class);
    }

    public Transform deserializeTransform(@NonNull final AsdfNode node) {
        Transform transform = registry.deserialize(node, Transform.class);
        transform = applyExplicitInverse(transform, node);
        transform = applyBoundingBox(transform, node);
        transform = applyNamedTransform(transform, node);
        return transform;
    }

    public Frame deserializeFrame(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Frame.class);
    }

    private Transform applyExplicitInverse(final Transform transform, final AsdfNode node) {
        final Optional<AsdfNode> inverseNode = node.getOptional("inverse");
        if (inverseNode.isEmpty()) {
            return transform;
        }
        final Transform inverse = deserializeTransform(inverseNode.get());
        return new ExplicitInverseWrapper(transform, inverse);
    }

    private Transform applyBoundingBox(final Transform transform, final AsdfNode node) {
        final Optional<AsdfNode> bboxNode = node.getOptional("bounding_box");
        if (bboxNode.isEmpty()) {
            return transform;
        }

        final AsdfNode bbox = bboxNode.get();
        final AsdfNode intervalsNode = bbox.get("intervals");

        final Optional<AsdfNode> inputsNode = node.getOptional("inputs");
        final String[] inputLabels = inputsNode
                .map(n -> n.asList(String.class).toArray(new String[0]))
                .orElse(null);

        final int inputCount = transform.getInputCount();
        final double[][] intervals = new double[inputCount][2];

        if (inputLabels != null) {
            if (inputLabels.length != inputCount) {
                throw new IllegalArgumentException(
                        "Input labels length (" + inputLabels.length
                                + ") does not match transform input count (" + inputCount + ")");
            }
            for (int i = 0; i < inputLabels.length; i++) {
                final AsdfNode interval = intervalsNode.get(inputLabels[i]);
                if (interval == null) {
                    throw new IllegalArgumentException(
                            "Bounding box intervals missing key '" + inputLabels[i] + "'");
                }
                intervals[i][0] = interval.get(0L).asDouble();
                intervals[i][1] = interval.get(1L).asDouble();
            }
        } else {
            int i = 0;
            for (final AsdfNode interval : intervalsNode) {
                if (i >= inputCount) {
                    throw new IllegalArgumentException(
                            "Bounding box has more intervals than transform inputs ("
                                    + inputCount + ")");
                }
                intervals[i][0] = interval.get(0L).asDouble();
                intervals[i][1] = interval.get(1L).asDouble();
                i++;
            }
            if (i != inputCount) {
                throw new IllegalArgumentException(
                        "Bounding box interval count (" + i
                                + ") does not match transform input count (" + inputCount + ")");
            }
        }

        return new BoundingBoxWrapper(transform, intervals, Double.NaN);
    }

    private static Transform applyNamedTransform(final Transform transform, final AsdfNode node) {
        final Optional<AsdfNode> nameNode = node.getOptional("name");
        final Optional<AsdfNode> inputsNode = node.getOptional("inputs");
        final Optional<AsdfNode> outputsNode = node.getOptional("outputs");

        if (nameNode.isEmpty() && inputsNode.isEmpty() && outputsNode.isEmpty()) {
            return transform;
        }

        final String name = nameNode.map(AsdfNode::asString).orElse(null);
        final String[] inputs = inputsNode
                .map(n -> n.asList(String.class).toArray(new String[0]))
                .orElse(null);
        final String[] outputs = outputsNode
                .map(n -> n.asList(String.class).toArray(new String[0]))
                .orElse(null);

        return new NamedTransform(transform, name, inputs, outputs);
    }
}
