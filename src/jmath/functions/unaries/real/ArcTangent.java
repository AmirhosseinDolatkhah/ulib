package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ArcTangent extends UnaryFunction {
    private ArcTangent() {
        super(Math::atan);
    }

    public static UnaryFunction f() {
        return new ArcTangent();
    }
}
