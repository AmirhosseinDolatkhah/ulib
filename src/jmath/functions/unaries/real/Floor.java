package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

public class Floor extends UnaryFunction {
    private Floor() {
        super(Math::floor);
    }

    public static UnaryFunction f() {
        return new Floor();
    }
}
