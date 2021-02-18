package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Logarithm extends UnaryFunction {
    private Logarithm(double base) {
        super(x -> Math.log(x) / Math.log(base));
    }

    public static UnaryFunction f(double base) {
        return new Logarithm(base);
    }

    public static UnaryFunction f() {
        return new Logarithm(Math.E);
    }
}
