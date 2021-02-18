package jmath.datatypes;

import jmath.datatypes.functions.Function2D;

import java.io.Serializable;

public interface Operator<Z> extends Serializable {
    Z operate(Function2D f);
}
