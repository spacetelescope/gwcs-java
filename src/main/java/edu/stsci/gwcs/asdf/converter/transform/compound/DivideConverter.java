package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Divide;

import java.util.Set;

public class DivideConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/divide-1.2.0",
            "tag:stsci.edu:asdf/transform/divide-1.3.0"
    );

    public DivideConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Divide(new Transform[]{l, r}));
    }
}
