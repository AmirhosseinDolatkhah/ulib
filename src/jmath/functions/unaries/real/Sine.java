package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Sine extends UnaryFunction {

    private Sine() {
        super(Math::sin);
    }

    public static UnaryFunction f() {
        return new Sine();
    }
}
