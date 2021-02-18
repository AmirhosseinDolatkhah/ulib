package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Cotangent extends UnaryFunction {
    private Cotangent() {
        super(x -> 1 / Math.tan(x));
    }

    public static UnaryFunction f() {
        return new Cotangent();
    }
}
