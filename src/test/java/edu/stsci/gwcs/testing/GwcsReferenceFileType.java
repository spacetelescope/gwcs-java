package edu.stsci.gwcs.testing;

import java.io.InputStream;

public enum GwcsReferenceFileType implements ReferenceFile {
    FUNCTIONAL,
    POLYNOMIAL,
    TABULAR,
    COMPOUND,
    ROTATION,
    GEOMETRY,
    PROJECTION,
    SPECTROSCOPY,
    FITS_WCS,
    WCS_IMAGING,
    WCS_CAL,
    WCS_SPECTROSCOPY,
    ROMAN_WCS,
    ;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public InputStream openScript() {
        return GwcsReferenceFileType.class.getResourceAsStream(
                String.format("/reference-file-scripts/%s.py", name().toLowerCase())
        );
    }
}
