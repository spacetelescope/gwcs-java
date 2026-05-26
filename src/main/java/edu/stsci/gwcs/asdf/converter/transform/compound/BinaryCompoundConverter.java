package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;
import java.util.function.BiFunction;

class BinaryCompoundConverter extends ConverterBase {
    private final BiFunction<Transform, Transform, Transform> constructor;

    BinaryCompoundConverter(final GwcsAsdfSupport support, final Set<String> tags,
                            final BiFunction<Transform, Transform, Transform> constructor) {
        super(support, tags);
        this.constructor = constructor;
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final AsdfNode forwardNode = node.get("forward");
        final Transform left = support().deserializeTransform(forwardNode.get(0L));
        final Transform right = support().deserializeTransform(forwardNode.get(1L));
        return constructor.apply(left, right);
    }
}
