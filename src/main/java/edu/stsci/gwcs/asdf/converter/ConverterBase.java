package edu.stsci.gwcs.asdf.converter;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;

import java.util.Set;

public abstract class ConverterBase implements Converter {
    private final GwcsAsdfSupport support;
    private final Set<String> tags;

    protected ConverterBase(final GwcsAsdfSupport support, final Set<String> tags) {
        this.support = support;
        this.tags = tags;
    }

    @Override
    public Set<String> tags() {
        return tags;
    }

    protected GwcsAsdfSupport support() {
        return support;
    }
}
