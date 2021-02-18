package jmath.datatypes.functions;

import jmath.datatypes.tuples.Point2D;

public interface Mapper2D extends Function<Point2D, Point2D> {
    Mapper2D NaN = p -> new Point2D(Double.NaN, Double.NaN);

    default Point2D map(double x, double y) {return valueAt(new Point2D(x, y));}

    @Override
    default Point2D atOrigin() {
        return map(0, 0);
    }
}
