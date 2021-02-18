package visualization.shapes.shapes3d;

import jmath.datatypes.tuples.Point3D;
import utils.Utils;
import visualization.canvas.CoordinatedScreen;

import java.awt.*;

@SuppressWarnings("unused")
public final class Line3D extends Shape3D {
    private final Point3D start;
    private final Point3D end;
    private Color color;
    private float thickness;

    public Line3D(CoordinatedScreen cs, Point3D start, Point3D end, Color color, float thickness) {
        super(cs);
        this.start = start;
        this.end = end;
        this.color = color;
        this.thickness = thickness;
        points.add(start);
        points.add(end);
    }

    public Line3D(CoordinatedScreen cs, Point3D start, Point3D end, Color color) {
        this(cs, start, end, color, 2f);
    }

    public Line3D(CoordinatedScreen cs, double x1, double y1, double z1, double x2, double y2, double z2, Color color, float thickness) {
        this(cs, new Point3D(x1, y1, z1), new Point3D(x2, y2, z2), color, thickness);
    }

    public Line3D(CoordinatedScreen cs, Point3D start, Point3D end) {
        this(cs, start, end, Utils.randomColor(), 2f);
    }

    public Line3D(CoordinatedScreen cs) {
        this(cs, Point3D.random(), Point3D.random());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public double getLen() {
        return start.distanceFrom(end);
    }

    public Point3D getStart() {
        return start;
    }

    public Point3D getEnd() {
        return end;
    }

    @Override
    public void render(Graphics2D g2d) {
        if (!isVisible)
            return;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.drawLine(cs.screenX(start.x), cs.screenY(start.y), cs.screenX(end.x), cs.screenY(end.y));
        super.render(g2d);
    }
}
