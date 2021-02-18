package jmath.datatypes.functions;

//import animation.canvaspanel.CoordinatedCanvasPanel;
import jmath.datatypes.tuples.Point2D;
import jmath.datatypes.tuples.Point3D;
import jmath.functions.utils.Sampling;

import java.awt.*;
import java.util.List;

public interface Arc3D extends Function<Point3D, Double> {
    Arc3D NaN = t -> new Point3D(Double.NaN, Double.NaN, Double.NaN);

    Point3D valueAt(double t);

    default List<Point3D> sample(double l, double u, double delta, int numOfThreads) {
        return Sampling.multiThreadSampling(this, l, u, delta, numOfThreads);
    }

    default UnaryFunction fx() {
        return new UnaryFunction(x -> valueAt(x).x);
    }

    default UnaryFunction fy() {
        return new UnaryFunction(x -> valueAt(x).y);
    }

    default UnaryFunction fz() {
        return new UnaryFunction(x -> valueAt(x).z);
    }

    default UnaryFunction zAsFunctionOfY(double l, double u, double delta) {
        return new UnaryFunction(x -> valueAt(fy().inverse(l, u, delta).valueAt(x)).z);
    }

    default Arc3D derivative(double delta, int order) {
        return t -> new Point3D(
                fx().derivative(delta, order).valueAt(t),
                fy().derivative(delta, order).valueAt(t),
                fz().derivative(delta, order).valueAt(t)
        );
    }

    default Arc3D rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        return t -> valueAt(t).rotate(center, xAngle, yAngle, zAngle);
    }

    default Arc3D rotate(double xAngle, double yAngle, double zAngle) {
        return rotate(new Point3D(), xAngle, yAngle, zAngle);
    }

    default Arc2D asArc2D() {
        return t -> new Point2D(fx().valueAt(t), fy().valueAt(t));
    }

    @Override
    default Point3D valueAt(Double t) {
        return valueAt(t.doubleValue());
    }

    @Override
    default Point3D atOrigin() {
        return valueAt(0);
    }

//    @Override
//    default void renderWithParams(Graphics2D g2d, Object... params) {
//        CoordinatedCanvasPanel canvas = (CoordinatedCanvasPanel) params[0];
//        Color color = (Color) params[1];
//        double l = (double) params[2];
//        double u = (double) params[3];
//        double delta = (double) params[4];
//        int numOfThreads = (int) params[5];
//        asArc2D().renderWithParams(g2d, canvas, color, l, u, delta, numOfThreads);
//    }

    static Arc3D circle(Point3D center, double radius) {
        var xc = center.x;
        var yc = center.y;
        var zc = center.z;
        return t -> new Point3D(radius * Math.sin(t) + xc, radius * Math.cos(t) + yc, zc);
    }
}
