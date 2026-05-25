package edu.stsci.gwcs.asdf;

import edu.stsci.gwcs.asdf.converter.Converter;
import lombok.NonNull;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TagRegistry {
    private final Map<String, Function<AsdfNode, Object>> handlers = new HashMap<>();

    public void register(@NonNull final Converter converter) {
        converter.tags().forEach(tag -> handlers.put(tag, converter::fromAsdfNode));
    }

    public void register(@NonNull final String tag, @NonNull final Function<AsdfNode, Object> handler) {
        handlers.put(tag, handler);
    }

    public <T> T deserialize(@NonNull final AsdfNode node, @NonNull final Class<T> expectedType) {
        final String tag = node.getTag();
        final Function<AsdfNode, Object> handler = handlers.get(tag);
        if (handler == null) {
            throw new IllegalArgumentException("Unrecognized ASDF tag: " + tag);
        }
        final Object result = handler.apply(node);
        if (!expectedType.isInstance(result)) {
            throw new ClassCastException(
                    "Tag " + tag + " produced " + (result == null ? "null" : result.getClass().getName())
                            + ", expected " + expectedType.getName());
        }
        return expectedType.cast(result);
    }

    public boolean hasHandler(final String tag) {
        return handlers.containsKey(tag);
    }
}
