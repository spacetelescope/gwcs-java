package edu.stsci.gwcs.asdf.converter.transform.spectroscopy;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.spectroscopy.Snell3D;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class Snell3DConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:gwcs/snell3d-1.0.0",
            "tag:stsci.edu:gwcs/snell3d-1.1.0",
            "tag:stsci.edu:gwcs/snell3d-1.2.0",
            "tag:stsci.edu:gwcs/snell3d-1.3.0"
    );

    public Snell3DConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        return new Snell3D();
    }
}
