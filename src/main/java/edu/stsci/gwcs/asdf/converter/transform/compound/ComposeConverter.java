package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Compose;

import java.util.Set;

public class ComposeConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/compose-1.2.0",
            "tag:stsci.edu:asdf/transform/compose-1.3.0"
    );

    public ComposeConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Compose(new Transform[]{l, r}));
    }
}
