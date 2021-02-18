package visualization.shapes.shapes3d;

import jmath.datatypes.functions.*;
import jmath.datatypes.tuples.Point2D;
import jmath.datatypes.tuples.Point3D;
import jmath.functions.utils.Sampling;
import utils.Utils;
import visualization.canvas.CoordinatedScreen;
import visualization.model.OBJHandler;
import visualization.render3D.shading.LightSource;
import visualization.render3D.shading.Shader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

@SuppressWarnings("unused")
public class Area extends Shape3D implements Function<Integer, Point3D> {
    private Color color;
    private final int numOfSides;
    private Point3D xBound;
    private Point3D yBound;

    private Shader shader;

    public Area(CoordinatedScreen canvas, Color color, boolean isFill, float thickness,
                double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface... surfaces) {
        super(canvas);
        this.color = color;
        var domain = Sampling.sampleOf2DRectangularRegion(xL, xU, yL, yU, deltaX, deltaY);
        for (var s : surfaces)
            domain.forEach(p -> points.add(s.valueAt(p)));
        int numberOfCols = (int) ((xU - xL) / deltaX) + 2;
        int numberOfRows = (int) ((yU - yL) / deltaY) + 2;
        numOfSides = numberOfCols * numberOfRows;

        for (int i = 0; i < points.size(); i++)
            if (i + numberOfCols + 1 < points.size() &&
                    i % numberOfCols < numberOfCols - 1 &&
                    i / numberOfCols < numberOfRows - 1)
                components.add(new FlatSurface(canvas, color, isFill, thickness,
                        points.get(i), points.get(i + 1), points.get(i + numberOfCols + 1), points.get(i + numberOfCols)));
        shader = new Shader(
                new LightSource(new Point3D(1, 1, 1), Color.RED, 0.1)
//                new LightSource(new Point3D(-1, -1, 1), Color.GREEN, 0.1)
//                new LightSource(new Point3D(-5, -10, 5), Color.BLUE, 0.2)
        );
        shader.shade(this);
        xBound = new Point3D(xL, xU, deltaX);
        yBound = new Point3D(yL, yU, deltaY);
    }

    public Area(List<Point3D> points, FlatSurface... squares) {
        super(squares[0].getCs());
        components.addAll(Arrays.asList(squares));
        this.points.addAll(points);
        numOfSides = squares.length;
        shader = new Shader(
                new LightSource(new Point3D(1, 1, 1), Color.RED, 0.1)
//                new LightSource(new Point3D(-1, -1, 1), Color.GREEN, 0.1)
//                new LightSource(new Point3D(-5, -10, 5), Color.BLUE, 0.2)
        );
        shader.shade(this);
    }

    public Area(CoordinatedScreen canvas, String pathOfModel) {
        super(canvas);
        List<FlatSurface> surfaces;
        components.addAll(surfaces = OBJHandler.getSurfaces(pathOfModel, canvas, getPoints()));
        numOfSides = surfaces.size();
        shader = new Shader(
                new LightSource(new Point3D(1, 1, 1), Color.RED, 0.1)
//                new LightSource(new Point3D(-1, -1, 1), Color.GREEN, 0.1)
//                new LightSource(new Point3D(-5, -10, 5), Color.BLUE, 0.2)
        );
        shader.shade(this);
    }

    public Area(CoordinatedScreen canvas, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface... surfaces) {
        this(canvas, Utils.randomColor(), true, 2, xL, xU, yL, yU, deltaX, deltaY, surfaces);
    }

    public Area(CoordinatedScreen canvas, Color color, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Function3D... fs) {
        this(canvas, color, true, 2, xL, xU, yL, yU, deltaX, deltaY, functionsToSurfaces(fs));
    }

    public Area(CoordinatedScreen canvas, Color color, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Arc2D arc) {
        this(canvas, color, true, 2, xL, xU, yL, yU, deltaX, deltaY, Surface.surfaceOfRevolution(arc));
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    private static Surface[] functionsToSurfaces(Function3D... fs) {
        Surface[] surfaces = new Surface[fs.length];
        int counter = 0;
        for (var f : fs)
            surfaces[counter++] = (x, y) -> new Point3D(x, y, f.valueAt(x, y));
        return surfaces;
    }

    public Color getColor() {
        return color;
    }

    public void setFill(boolean isFill) {
        for (var c : components)
            if (c instanceof FlatSurface)
                ((FlatSurface) c).setFilled(isFill);
    }

    private FlatSurface getFirstFlatSurface() {
        for (var c : components)
            if (c instanceof FlatSurface)
                return (FlatSurface) c;
        return null;
    }

    public void setColor(Color color) {
        this.color = color;
        components.forEach(e -> {
            if (e instanceof FlatSurface)
                ((FlatSurface) e).setColor(color);
        });
    }

    public void setThickness(float thickness) {
        components.forEach(e -> {
            if (e instanceof FlatSurface)
                ((FlatSurface) e).setThickness(thickness);
        });
    }

    public float getThickness() {
        try {
            return getFirstFlatSurface().getThickness();
        } catch (NullPointerException e) {
            return 1.5f;
        }
    }

    public boolean isFilled() {
        try {
            return getFirstFlatSurface().isFilled();
        } catch (NullPointerException e) {
            return true;
        }
    }

    public double getLowBoundX() {
        return xBound.x;
    }

    public double getLowBoundY() {
        return yBound.x;
    }

    public double getUpBoundX() {
        return xBound.y;
    }

    public double getUpBoundY() {
        return yBound.y;
    }

    public double getDeltaX() {
        return xBound.z;
    }

    public double getDeltaY() {
        return yBound.z;
    }

    public void setLowBoundX(double xL) {

    }

    public void setUpBoundX(double xU) {

    }

    public void setDeltaX(double deltaX) {

    }

    public void setLowBoundY(double yL) {

    }

    public void setUpBoundY(double yU) {

    }

    public void setDeltaY(double deltaY) {

    }

    public static Area cube(CoordinatedScreen canvas, Point3D center, double sideLen) {
        var hsl = new double[] {-sideLen * sqrt(3) / 2, sideLen * sqrt(3) / 2}; // half of digLen
        var xc = center.x;
        var yc = center.y;
        var zc = center.z;

        var ps = new Point3D[8];
        int counter = 0;
        for (var hs1 : hsl)
            for (var hs2 : hsl)
                for (var hs3 : hsl)
                    ps[counter++] = new Point3D(xc + hs1, yc + hs2, zc + hs3);

        return new Area(new ArrayList<>(Arrays.asList(ps)),
                new FlatSurface(canvas, Utils.randomColor(), ps[4], ps[5], ps[7], ps[6]),
                new FlatSurface(canvas, Utils.randomColor(), ps[0], ps[2], ps[6], ps[4]),
                new FlatSurface(canvas, Utils.randomColor(), ps[1], ps[3], ps[7], ps[5]),
                new FlatSurface(canvas, Utils.randomColor(), ps[1], ps[5], ps[4], ps[0]),
                new FlatSurface(canvas, Utils.randomColor(), ps[3], ps[7], ps[6], ps[2]),
                new FlatSurface(canvas, Utils.randomColor(), ps[1], ps[3], ps[2], ps[0])
        );
    }

    public static Area sphere(CoordinatedScreen canvas, Color color, Point3D center, double radius) {
        return new Area(canvas, color, true, 2f, -PI / 2, PI / 2, -PI, PI, 0.1, 0.1,
                        (x, y) -> new Point3D(radius * cos(x) * cos(y) + center.x, radius * cos(x) * sin(y) +
                                center.y, radius * sin(x) + center.z));
    }

    public static Area cylinder(CoordinatedScreen canvas, Color color, double radius, double height) {
        return new Area(canvas, color, true, 2f, -PI, PI, -PI, PI, 0.5, 0.5,
                Surface.surfaceOfRevolution(t -> new Point2D(radius, t)));
    }

    public static Area cylinder(CoordinatedScreen canvas, Color color) {
        return new Area(canvas, color, true, 2f, -PI/2, PI/2, -PI, PI, 0.1, 0.1,
                Surface.surfaceOfRevolution(t -> new Point2D(sin(t) * t, cos(t*t) * t)));
    }

    @Override
    public void rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        super.rotate(center, xAngle, yAngle, zAngle);
        shader.shade(this);
    }

    @Override
    public void affectMapper(Mapper3D... mappers) {
        super.affectMapper(mappers);
        shader.shade(this);
    }

    @Override
    public void move(double xChange, double yChange, double zChange) {
        super.move(xChange, yChange, zChange);
        shader.shade(this);
    }

    @Override
    public Integer valueAt(Point3D point3D) {
        return null;
    }
}

