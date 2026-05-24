package edu.stsci.gwcs.asdf;

import edu.stsci.gwcs.Wcs;
import edu.stsci.gwcs.coordinate.Frame;
import edu.stsci.gwcs.transform.Transform;
import lombok.NonNull;
import org.asdfformat.asdf.node.AsdfNode;

public final class WcsDeserializer {
    private final TagRegistry registry;

    public WcsDeserializer() {
        registry = new TagRegistry();
        registerHandlers();
    }

    public WcsDeserializer(final TagRegistry registry) {
        this.registry = registry;
    }

    private void registerHandlers() {
    }

    public Wcs deserialize(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Wcs.class);
    }

    public Transform deserializeTransform(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Transform.class);
    }

    public Frame deserializeFrame(@NonNull final AsdfNode node) {
        return registry.deserialize(node, Frame.class);
    }

    public TagRegistry getRegistry() {
        return registry;
    }
}
