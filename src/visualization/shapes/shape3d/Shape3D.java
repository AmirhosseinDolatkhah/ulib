package visualization.shapes.shape3d;

import jmath.datatypes.functions.Function;
import jmath.datatypes.functions.Mapper3D;
import jmath.datatypes.tuples.Point3D;
import visualization.canvas.CoordinatedScreen;
import visualization.canvas.Render;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

@SuppressWarnings("unused")
public class Shape3D implements Render, Comparable<Shape3D>, Serializable, Function<Object, Object> {
    protected final CoordinatedScreen cs;
    protected final ArrayList<Point3D> points;
    protected final List<Shape3D> components;
    protected boolean isVisible;
    protected boolean doTick;
    protected boolean isRotatable;
    protected Point3D currentAngle;
    protected List<Runnable> ticks;
    protected Labeled label;

    public Shape3D(CoordinatedScreen cs) {
        this.cs = cs;
        points = new ArrayList<>();
        ticks = new ArrayList<>();
        currentAngle = new Point3D();
        components = new ArrayList<>();
        isVisible = true;
        doTick = true;
        isRotatable = true;
        label = () -> null;
    }

    public Shape3D() {
        this(null);
    }

    public CoordinatedScreen getCs() {
        return cs;
    }

    public List<Point3D> getPoints() {
        return points;
    }

    public void addTicks(Runnable... ticks) {
        this.ticks.addAll(Arrays.asList(ticks));
    }

    public void rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        if (!isRotatable)
            return;
        if (!Double.isFinite(center.x) || !Double.isFinite(center.y) || !Double.isFinite(center.z))
            center = new Point3D();
        for (var c : points)
            c.rotate(center, xAngle, yAngle, zAngle);
        currentAngle.addVector(xAngle, yAngle, zAngle);
    }

    public boolean isRotatable() {
        return isRotatable;
    }

    public void setRotatable(boolean rotatable) {
        isRotatable = rotatable;
    }

    public void affectMapper(Mapper3D... mappers) {
        for (var c : points)
            c.affectMapper(mappers);
    }

    public void scalePoints(double xScale, double yScale, double zScale) {
        points.forEach(p -> {
            p.affectOnX(x -> x * xScale);
            p.affectOnY(x -> x * yScale);
            p.affectOnZ(x -> x * zScale);
        });
    }

    public void scalePoints(double scale) {
        scalePoints(scale, scale, scale);
    }

    public void move(double xChange, double yChange, double zChange) {
        affectMapper(p -> p.addVector(xChange, yChange, zChange));
    }

    public void move(Point3D vector) {
        move(vector.x, vector.y, vector.z);
    }

    public Point3D getCurrentAngle() {
        return currentAngle;
    }

    public final void rotate(double xAngle, double yAngle, double zAngle) {
        rotate(getCenter(), xAngle, yAngle, zAngle);
    }

    public Point3D getCenter() {
        var res = new Point3D();
        points.forEach(res::addVector);
        res.affectOnXYZ(x -> x / points.size());
        return res;
    }

    @Override
    public final boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isDoTick() {
        return doTick;
    }

    public List<Shape3D> getComponents() {
        return components;
    }

    public void setDoTick(boolean doTick) {
        this.doTick = doTick;
    }

    public int numberOfComponents() {
        return components.size();
    }

    public void addComponents(Shape3D... shapes) {
        components.addAll(Arrays.asList(shapes));
        for (var s : shapes)
            points.addAll(s.points);
    }

    public List<Runnable> getTicks() {
        return ticks;
    }

    public void removeAllTicks() {
        ticks.clear();
    }

    public void setPos(double x, double y, double z) {
        var c = getCenter();
        var dx = x - c.x;
        var dy = y - c.y;
        var dz = z - c.z;
        move(dx, dy, dz);
    }

    public void setPos(Point3D pos) {
        setPos(pos.x, pos.y, pos.z);
    }

    public String getLabel() {
        return label.getLabel();
    }

    public void setLabel(Labeled label) {
        this.label = label;
    }

    @Deprecated // problem in removing common points of components
    public void removeComponents(int... indexes) {
        for (var i : indexes) {
            points.removeAll(components.get(i).points);
            components.remove(i);
        }
    }

    @Override
    public Object valueAt(Object o) {
        return null;
    }

    @Override // There is a problem in sorting
    public void render(Graphics2D g2d) {
        try {
            Collections.sort(components);
            components.forEach(c -> c.render(g2d));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (label.getLabel() != null) {
            var c = cs.screen(getCenter());
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("serif", Font.PLAIN, 9));
            g2d.drawString(label.getLabel(), c.x, c.y);
        }
    }

    @Override
    public void tick() {
        if (!doTick)
            return;
        try {
            ticks.forEach(Runnable::run);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Shape3D o) {
        return Double.compare(getCenter().z, o.getCenter().z);
    }

    @FunctionalInterface
    public interface Labeled {
        String getLabel();
    }
}
