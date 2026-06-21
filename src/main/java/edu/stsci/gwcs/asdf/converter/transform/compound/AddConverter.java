package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Add;

import java.util.Set;

public class AddConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/add-1.2.0",
            "tag:stsci.edu:asdf/transform/add-1.3.0",
            "tag:stsci.edu:asdf/transform/add-1.4.0"
    );

    public AddConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Add(new Transform[]{l, r}));
    }
}
