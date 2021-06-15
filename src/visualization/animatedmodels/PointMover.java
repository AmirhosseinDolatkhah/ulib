package visualization.animatedmodels;

import jmath.datatypes.functions.Arc3D;
import jmath.datatypes.functions.Function2D;
import jmath.datatypes.tuples.Point3D;
import swingutils.MainFrame;
import visualization.canvas.CoordinatedScreen;
import visualization.canvas.Graph3DCanvas;
import visualization.canvas.Render;
import visualization.shapes.shape3d.Shape3D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class PointMover extends Shape3D {
    private final CoordinatedScreen cs;

    private boolean showHead;
    private boolean showPath;
    private double start;
    private BasicStroke pathStroke;
    private double delta;
    private int pointRadius;

    private Arc3D positionFunction;
    private PointColorSupplier pathColorSupplier;
    private PointColorSupplier headColorSupplier;
    private final List<Point3D> path;

    public PointMover(CoordinatedScreen cs, double start) {
        this.cs = cs;
        this.start = start;
        pathColorSupplier = p -> Color.RED;
        headColorSupplier = p -> Color.GREEN;
        positionFunction = t -> new Point3D(t * sin(t), sin(t), cos(t));
        path = new ArrayList<>(List.of(positionFunction.valueAt(start)));
        points.add(positionFunction.valueAt(start));
        delta = 0.05;
        showPath = true;
        showHead = true;
        pathStroke = new BasicStroke(1f);
        pointRadius = 2;
    }

    public PointMover(CoordinatedScreen cs) {
        this(cs, 0);
    }

    public void move() {
//        path.add(positionFunction.valueAt(start += delta));
        points.add(positionFunction.valueAt(start += delta));
    }

    public boolean isShowHead() {
        return showHead;
    }

    public void setShowHead(boolean showHead) {
        this.showHead = showHead;
    }

    public boolean isShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public BasicStroke getPathStroke() {
        return pathStroke;
    }

    public void setPathStroke(BasicStroke pathStroke) {
        this.pathStroke = pathStroke;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public int getPointRadius() {
        return pointRadius;
    }

    public void setPointRadius(int pointRadius) {
        this.pointRadius = pointRadius;
    }

    public Arc3D getPositionFunction() {
        return positionFunction;
    }

    public void setPositionFunction(Arc3D positionFunction) {
        this.positionFunction = positionFunction;
    }

    public PointColorSupplier getPathColorSupplier() {
        return pathColorSupplier;
    }

    public void setPathColorSupplier(PointColorSupplier pathColorSupplier) {
        this.pathColorSupplier = pathColorSupplier;
    }

    public PointColorSupplier getHeadColorSupplier() {
        return headColorSupplier;
    }

    public void setHeadColorSupplier(PointColorSupplier headColorSupplier) {
        this.headColorSupplier = headColorSupplier;
    }

    public List<Point3D> getPath() {
        return path;
    }

    @Override
    public void render(Graphics2D g2d) {
        if (!showPath && !showHead)
            return;
        var oldColor = g2d.getColor();
        var oldRenderingHints = g2d.getRenderingHints();
//        var head = Point3D.rotateImmutably(path.get(path.size() - 1), cs.camera().getAngles());
        var head = points.get(points.size() - 1);
        if (showPath) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(headColorSupplier.colorOf(head));
            g2d.fillOval(cs.screenX(head.x) - pointRadius, cs.screenY(head.y) - pointRadius, pointRadius * 2,
                    pointRadius * 2);
        }
        if (!showPath) {
            g2d.setColor(oldColor);
            g2d.setRenderingHints(oldRenderingHints);
            return;
        }
        var oldStroke = g2d.getStroke();
        g2d.setStroke(pathStroke);
        g2d.setColor(pathColorSupplier.colorOf(head));
//        Graph3DCanvas.simplePlotter(
//                path.stream().map(e -> Point3D.rotateImmutably(e, cs.camera().getAngles())).toList(), cs, g2d);
        Graph3DCanvas.simplePlotter(points, cs, g2d);
        g2d.setStroke(oldStroke);
        g2d.setColor(oldColor);
    }

    @FunctionalInterface
    public interface PointColorSupplier {
        Color colorOf(Point3D p);
    }

    public static void main(String[] args) {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        var mover = new PointMover(gp);

        f.add(gp);

        gp.addRender(mover);

        new Timer(10, e -> {
            mover.move();
            mover.rotate(0.01, 0.03, 0.02);
            gp.repaint();
        }).start();

        SwingUtilities.invokeLater(f);
    }
}
