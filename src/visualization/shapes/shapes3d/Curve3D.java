package visualization.shapes.shapes3d;

import jmath.datatypes.functions.Arc3D;
import jmath.datatypes.tuples.Point2D;
import jmath.functions.utils.Sampling;
import utils.Utils;
import visualization.canvas.CoordinatedScreen;
import visualization.canvas.Graph2DCanvas;

import java.awt.*;
import java.util.ArrayList;

public final class Curve3D extends Shape3D {
    private Color color;
    private float thickness;

    public Curve3D(CoordinatedScreen canvas, Color color, float thickness, double l, double u, double delta, Arc3D... arcs) {
        super(canvas);
        this.thickness = thickness;
        this.color = color;
        var domain = Sampling.sample(l, u, delta);
        for (var a : arcs)
            domain.forEach(t -> getPoints().add(a.valueAt(t)));
    }

    public Curve3D(CoordinatedScreen canvas, double l, double u, double delta, Arc3D... arcs) {
        this(canvas, Utils.randomColor(), 2, l, u, delta, arcs);
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

    @Override
    public void render(Graphics2D g2d) {
        if (!isVisible)
            return;
        var ps = new ArrayList<Point2D>();
        points.forEach(p -> ps.add(new Point2D(p.x, p.y)));
        g2d.setStroke(new BasicStroke(thickness));
        Graph2DCanvas.typicalPlotter(ps, color, cs::screenX, cs::screenY, g2d);
        super.render(g2d);
    }
}
