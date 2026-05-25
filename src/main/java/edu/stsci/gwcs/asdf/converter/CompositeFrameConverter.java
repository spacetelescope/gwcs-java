package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.coordinate.CompositeFrame;
import edu.stsci.gwcs.coordinate.Frame;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class CompositeFrameConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/composite_frame-1.0.0",
            "tag:stsci.edu:gwcs/composite_frame-1.2.0"
    );

    public CompositeFrameConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public CompositeFrame fromAsdfNode(final AsdfNode node) {
        final String name = node.getString("name");
        final AsdfNode framesNode = node.get("frames");
        final Frame[] frames = new Frame[framesNode.size()];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = support().deserializeFrame(framesNode.get((long) i));
        }
        return new CompositeFrame(name, frames);
    }
}
