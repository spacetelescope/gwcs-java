package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.Power;

import java.util.Set;

public class PowerConverter extends BinaryCompoundConverter {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/power-1.2.0",
            "tag:stsci.edu:asdf/transform/power-1.3.0",
            "tag:stsci.edu:asdf/transform/power-1.4.0"
    );

    public PowerConverter(final GwcsAsdfSupport support) {
        super(support, TAGS, (l, r) -> new Power(new Transform[]{l, r}));
    }
}
