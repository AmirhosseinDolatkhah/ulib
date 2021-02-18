package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class AbsoluteValue extends UnaryFunction {
    private AbsoluteValue() {
        super(Math::abs);
    }

    public static UnaryFunction f() {
        return new AbsoluteValue();
    }
}
