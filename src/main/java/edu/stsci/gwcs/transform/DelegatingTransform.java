package edu.stsci.gwcs.transform;

public interface DelegatingTransform extends Transform {
    Transform getDelegate();
}
