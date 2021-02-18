package jmath.datatypes.tuples;

import jmath.datatypes.functions.Function2D;
import jmath.datatypes.functions.Function4D;
import jmath.datatypes.functions.Mapper3D;

import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Point3D extends SortableTuple<Double> implements Comparable<Point3D>, AbstractPoint {
    private static int comparatorMode = 0;
    private static Function4D comparatorFunction = (xx, yy, zz) -> xx;
    public static final int ABS_COMPARE = 0;
    public static final int X_COMPARE = 1;
    public static final int Y_COMPARE = 2;
    public static final int Z_COMPARE = 3;
    public static final int FUNCTION_COMPARATOR = 4;

    public static final Point3D NaN = new Point3D(Double.NaN, Double.NaN, Double.NaN);

    public double x;
    public double y;
    public double z;

    public Point3D(double x, double y, double z) {
        super(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D() {
        this(0, 0, 0);
    }

    public Point3D(Point3D p) {
        this(p.x, p.y, p.z);
    }

    public Point3D(Point2D p, double z) {
        this(p.x, p.y, z);
    }

    @Override
    public double getCoordinate(int numOfCoordinate) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: return x;
            case CoordinateY: return y;
            case CoordinateZ: return z;
        }
        return Double.NaN;
    }

    @Override
    public double distanceFromOrigin() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public int numOfCoordinates() {
        return 3;
    }

    @Override
    public void setCoordinate(int numOfCoordinate, double newValue) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: x = newValue; break;
            case CoordinateY: y = newValue; break;
            case CoordinateZ: z = newValue; break;
        }
    }

    public Point3D set(Point3D p) {
        x = p.x;
        y = p.y;
        z = p.z;
        return this;
    }

    public Point3D set(double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
        return this;
    }

    public Point3D set(Point2D p, double newZ) {
        x = p.x;
        y = p.y;
        z = newZ;
        return this;
    }

    public Point2D getXY() {
        return new Point2D(x, y);
    }

    public Point2D getXZ() {
        return new Point2D(x, z);
    }

    public Point2D getYZ() {
        return new Point2D(y, z);
    }

    public Point3D addVector(Point3D vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        return this;
    }

    public Point3D addVector(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Point3D affectOnX(Function2D f) {
        x = f.valueAt(x);
        return this;
    }

    public Point3D affectOnY(Function2D f) {
        y = f.valueAt(y);
        return this;
    }

    public Point3D affectOnZ(Function2D f) {
        z = f.valueAt(z);
        return this;
    }

    public Point3D affectOnXYZ(Function2D f) {
        x = f.valueAt(x);
        y = f.valueAt(y);
        z = f.valueAt(z);
        return this;
    }

    public Point3D rotate(double xAngle, double yAngle, double zAngle) {
        var p = getYZ().rotate(xAngle);
        set(x, p.x, p.y);
        p = getXZ().rotate(yAngle);
        set(p.x, y, p.y);
        p = getXY().rotate(zAngle);
        set(p.x, p.y, z);
        return this;
    }

    public Point3D rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        var p = getYZ().rotate(center.getYZ(), xAngle);
        set(x, p.x, p.y);
        p = getXZ().rotate(center.getXZ(), yAngle);
        set(p.x, y, p.y);
        p = getXY().rotate(center.getXY(), zAngle);
        set(p.x, p.y, z);
        return this;
    }

    public Point3D affectMapper(Mapper3D... mappers) {
        for (var m : mappers)
            set(m.valueAt(this));
        return this;
    }

    public Point3D getCopy() {
        return new Point3D(this);
    }

    public Point3D crossProduct(Point3D p) {
        x = x * p.y - y * p.x;
        y = z * p.x - x * p.z;
        z = y * p.z - z * p.y;
        return this;
    }

    public Point3D normalize() {
        var magnitude = distanceFromOrigin();
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;
        return this;
    }

    public double dotProduct(Point3D p) {
        return x * p.x + y * p.y + z * p.z;
    }

    public double distanceFrom(Point3D p) {
        return new Point3D(this).addVector(-p.x, -p.y, -p.z).distanceFromOrigin();
    }

    public double distanceFrom(double x, double y, double z) {
        return new Point3D(this).addVector(-x, -y, -z).distanceFromOrigin();
    }

    public double pointToValue(Function4D f) {
        return f.valueAt(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point3D)) return false;
        Point3D point3D = (Point3D) o;
        return Double.compare(point3D.x, x) == 0 &&
                Double.compare(point3D.y, y) == 0 &&
                Double.compare(point3D.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    @Override
    public int compareTo(Point3D o) {
        switch (comparatorMode) {
            case ABS_COMPARE: return Double.compare(distanceFromOrigin(), o.distanceFromOrigin());
            case X_COMPARE: return Double.compare(x, o.x);
            case Y_COMPARE: return Double.compare(y, o.y);
            case Z_COMPARE: return Double.compare(z, o.z);
            case FUNCTION_COMPARATOR:
                return Double.compare(comparatorFunction.valueAt(x, y, z), comparatorFunction.valueAt(o.x, o.y, o.z));
            default: return 0;
        }
    }

    public static Function4D getComparatorFunction() {
        return comparatorFunction;
    }

    public static void setComparatorFunction(Function4D comparatorFunction) {
        Point3D.comparatorFunction = comparatorFunction;
    }

    public static void setComparatorMode(int comparatorMode) {
        Point3D.comparatorMode = comparatorMode;
    }

    public static Point3D random() {
        return new Point3D(Math.random(), Math.random(), Math.random());
    }

    public static Point3D random(double xL, double xU, double yL, double yU, double zL, double zU) {
        return new Point3D(xL + (xU - xL) * Math.random(), yL + (yU - yL) * Math.random(), zL + (zU - zL) * Math.random());
    }

    public static Point3D crossProduct(Point3D v1, Point3D v2) {
        return new Point3D(
                v1.x * v2.y - v1.y * v2.x,
                v1.z * v2.x - v1.x * v2.z,
                v1.y * v2.z - v1.z * v2.y
                );
    }

    public static double dotProduct(Point3D v1, Point3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Point3D sum(Point3D... points) {
        var res = new Point3D();
        for (var p : points)
            res.addVector(p);
        return res;
    }

    public static Point3D average(Point3D... points) {
        return sum(points).affectOnXYZ(x -> x / points.length);
    }

    public static Point3D sub(Point3D start, Point3D end) {
        return new Point3D(end.x - start.x, end.y - start.y, end.z - start.z);
    }

    public static double distance(Point3D p1, Point3D p2) {
        return p1.distanceFrom(p2);
    }

    public static double complexProduct(Point3D dotVector, Point3D crossVector1, Point3D crossVector2) {
        return dotProduct(dotVector, crossProduct(crossVector1, crossVector2));
    }
}
