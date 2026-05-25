package edu.stsci.gwcs.asdf;

import edu.stsci.gwcs.Wcs;
import edu.stsci.gwcs.asdf.converter.CelestialFrameConverter;
import edu.stsci.gwcs.asdf.converter.CompositeFrameConverter;
import edu.stsci.gwcs.asdf.converter.Frame2DConverter;
import edu.stsci.gwcs.coordinate.Frame;
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
    }

    private void registerFrameConverters() {
        registry.register(new Frame2DConverter(this));
        registry.register(new CelestialFrameConverter(this));
        registry.register(new CompositeFrameConverter(this));
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
