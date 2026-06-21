package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Concatenate;

import java.util.Set;

public class ConcatenateConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/concatenate-1.2.0",
            "tag:stsci.edu:asdf/transform/concatenate-1.3.0",
            "tag:stsci.edu:asdf/transform/concatenate-1.4.0"
    );

    public ConcatenateConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Concatenate(new Transform[]{l, r}));
    }
}
