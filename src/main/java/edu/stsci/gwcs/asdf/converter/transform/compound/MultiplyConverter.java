package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Multiply;

import java.util.Set;

public class MultiplyConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/multiply-1.2.0",
            "tag:stsci.edu:asdf/transform/multiply-1.3.0"
    );

    public MultiplyConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Multiply(new Transform[]{l, r}));
    }
}
