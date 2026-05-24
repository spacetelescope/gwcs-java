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
        return checkedCast(registry.deserialize(node), Wcs.class, node);
    }

    public Transform deserializeTransform(@NonNull final AsdfNode node) {
        return checkedCast(registry.deserialize(node), Transform.class, node);
    }

    public Frame deserializeFrame(@NonNull final AsdfNode node) {
        return checkedCast(registry.deserialize(node), Frame.class, node);
    }

    private <T> T checkedCast(final Object result, final Class<T> expectedType, final AsdfNode node) {
        if (!expectedType.isInstance(result)) {
            throw new ClassCastException(
                    "Tag " + node.getTag() + " produced " + result.getClass().getName()
                            + ", expected " + expectedType.getName());
        }
        return expectedType.cast(result);
    }

    public TagRegistry getRegistry() {
        return registry;
    }
}
