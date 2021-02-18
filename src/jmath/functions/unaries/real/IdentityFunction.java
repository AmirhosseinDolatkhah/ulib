package jmath.functions.unaries.real;

import jmath.datatypes.functions.Function2D;
import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class IdentityFunction extends UnaryFunction {
    private IdentityFunction() {
        super(x -> x);
    }

    public static boolean isIdentityFunction(Function2D function) {
        return f().equals(function);
    }

    public static UnaryFunction f() {
        return new IdentityFunction();
    }
}
