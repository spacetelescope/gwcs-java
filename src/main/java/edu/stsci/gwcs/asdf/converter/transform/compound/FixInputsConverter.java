package edu.stsci.gwcs.asdf.converter.transform.compound;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.compound.FixInputs;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FixInputsConverter extends ConverterBase {
    private static final Set<String> TAGS = Set.of(
            "tag:stsci.edu:asdf/transform/fix_inputs-1.2.0",
            "tag:stsci.edu:asdf/transform/fix_inputs-1.3.0",
            "tag:stsci.edu:asdf/transform/fix_inputs-1.4.0"
    );

    public FixInputsConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final AsdfNode forwardNode = node.get("forward");
        final Transform delegate = support().deserializeTransform(forwardNode.get(0L));

        final AsdfNode mappingNode = forwardNode.get(1L);
        final int[] keys = AsdfNodeUtils.readIntArray(mappingNode, "keys");
        final double[] values = AsdfNodeUtils.readDoubleArray(mappingNode, "values");

        if (keys.length != values.length) {
            throw new IllegalArgumentException(
                    "fix_inputs keys and values must have the same length, got "
                            + keys.length + " keys and " + values.length + " values");
        }

        final Map<Integer, Double> fixedInputs = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) {
            fixedInputs.put(keys[i], values[i]);
        }

        return new FixInputs(delegate, fixedInputs);
    }
}
