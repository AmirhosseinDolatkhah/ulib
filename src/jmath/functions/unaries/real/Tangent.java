package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Tangent extends UnaryFunction {
    private Tangent() {
        super(Math::tan);
    }

    public static UnaryFunction f() {
        return new Tangent();
    }
}
