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
import edu.stsci.gwcs.asdf.converter.transform.tabular.Tabular1DConverter;
import edu.stsci.gwcs.frame.Frame;
import edu.stsci.gwcs.transform.Transform;
import lombok.NonNull;
import org.asdfformat.asdf.node.AsdfNode;

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
    }

    public Wcs deserializeWcs(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Wcs.class);
    }

    public Transform deserializeTransform(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Transform.class);
    }

    public Frame deserializeFrame(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Frame.class);
    }

}
