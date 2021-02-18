package jmath.datatypes.functions;

import jmath.datatypes.tuples.Point2D;
import jmath.datatypes.tuples.Point3D;

public interface Surface extends Function<Point3D, Point2D> {
    Surface NaN = (x, y) -> new Point3D(Double.NaN, Double.NaN, Double.NaN);

    Point3D valueAt(double x, double y);

    default BinaryFunction fx() {
        return new BinaryFunction((x, y) -> valueAt(x, y).x);
    }

    default BinaryFunction fy() {
        return new BinaryFunction((x, y) -> valueAt(x, y).y);
    }

    default BinaryFunction fz() {
        return new BinaryFunction((x, y) -> valueAt(x, y).z);
    }

    @Override
    default Point3D valueAt(Point2D p) {
        return valueAt(p.x, p.y);
    }

    @Override
    default Point3D atOrigin() {
        return valueAt(new Point2D());
    }

    static Surface surfaceOfRevolution(Arc2D arc) {
        return (x, y) -> new Point3D(arc.fx().valueAt(x) * Math.cos(y), arc.fx().valueAt(x) * Math.sin(y), arc.fy().valueAt(x));
    }
}
