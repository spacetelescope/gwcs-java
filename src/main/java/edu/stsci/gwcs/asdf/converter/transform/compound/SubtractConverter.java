package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Subtract;

import java.util.Set;

public class SubtractConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/subtract-1.2.0",
            "tag:stsci.edu:asdf/transform/subtract-1.3.0",
            "tag:stsci.edu:asdf/transform/subtract-1.4.0"
    );

    public SubtractConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Subtract(new Transform[]{l, r}));
    }
}
