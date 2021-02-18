package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Exponential extends UnaryFunction {
    private Exponential(double base) {
        super(x -> Math.pow(base, x));
    }

    public static UnaryFunction f(double base) {
        return new Exponential(base);
    }
    
    public static UnaryFunction f() {
        return new Exponential(Math.E);
    }
}
