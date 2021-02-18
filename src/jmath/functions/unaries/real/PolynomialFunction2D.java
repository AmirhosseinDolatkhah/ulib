package jmath.functions.unaries.real;

import jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class PolynomialFunction2D extends UnaryFunction {
    private PolynomialFunction2D(double... coefficients) {
        super(x -> {
            double res = 0;
            int counter = 0;
            for (double c : coefficients)
                res += c * Math.pow(x, counter++);
            return res;
        });
    }

    public static UnaryFunction f(double... coefficients) {
        return new PolynomialFunction2D(coefficients);
    }
}
