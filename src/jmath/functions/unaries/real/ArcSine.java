package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ArcSine extends UnaryFunction {
    private ArcSine() {
        super(Math::asin);
    }

    public static UnaryFunction f() {
        return new ArcSine();
    }
}
