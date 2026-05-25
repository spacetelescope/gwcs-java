package edu.stsci.gwcs.asdf.converter;

import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public interface Converter {
    Set<String> tags();

    Object fromAsdfNode(AsdfNode node);
}
