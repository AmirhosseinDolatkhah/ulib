import jmath.datatypes.functions.Arc3D;
import jmath.datatypes.functions.Surface;
import jmath.datatypes.graph.Graph;
import jmath.datatypes.tuples.Point2D;
import jmath.datatypes.tuples.Point3D;
import swingutils.MainFrame;
import utils.Utils;
import visualization.animatedmodels.*;
import visualization.canvas.Graph3DCanvas;
import visualization.canvas.ImageCanvas;
import visualization.shapes.shape3d.Area;
import visualization.shapes.shape3d.Curve3D;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.security.SecureRandomParameters;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

public class Main {

    public static void main(String[] args) {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        f.add(gp);
                var g = new Graph<Point3D>();
                g.setCs(gp);
//                g.addNode(new Point3D(0, 1, 1), "1");
//                g.addNode(new Point3D(1, 0, 1), "2");
//                g.addNode(new Point3D(1, 1, 0), "3");
//                g.addEdge("1", "2");
//                g.addEdge("2", "3");
//                gp.addRender(g);
//                gp.getRenderManager().addTick(() -> g.addNode(new Point3D(Math.random() + 5, Math.random() + 5, Math.random() + 5), String.valueOf(Math.random())));
//                gp.getRenderManager().addTick(() -> gp.rotateShapes(0.1, 0.2, 0.3));
//                gp.addRender(new Curve3D(gp, Color.GREEN, 1f, -4, 4, 0.05,
//                        x -> new Point3D(sin(x) + 2 * sin(2*x), cos(x) - 2 * cos(2*x), -sin(3*x))));
//                gp.addRender(new Area(gp, Color.GREEN, true, 1f, 0, 2*PI, -1, 1, 0.05, 0.05,
//                        Surface.mobius()));
//                        gp.addRender(new Area(gp, Color.GREEN, true, 1f, 0, PI, 0, 2*PI, 0.05, 0.05,
//                                Surface.kleinBottle()));

        //
//                gp.addRender(new Curve3D(gp, Color.GREEN, 1f, -4, 4, 0.05, Arc3D
//                        .circle(new Point3D(), 3, new Point3D(1, 1, 1))));
//                gp.addRender(new Area(gp, Color.BLUE, true, 1f, -Math.PI, Math.PI, -Math.PI, Math.PI, 0.05, 0.05,
//                        Surface.circulation(x -> new Point3D(sin(x) + 2 * sin(2*x), cos(x) - 2 * cos(2*x), -sin(3*x)), t -> Math.abs(
//                                sin(t) / 3))));
                gp.addRender(new Area(gp, Color.BLUE, true, 1f, -Math.PI, Math.PI, -Math.PI, Math.PI, 0.05, 0.1,
                        Surface.curveWrapping(
                                x -> new Point3D(sin(x) + 2 * sin(2*x), cos(x) - 2 * cos(2*x), -sin(3*x)),
                                t -> new Point2D(t / 10, cos(t) / 5))));
//                gp.addRender(
//                        new Area(gp, Color.BLUE, true, 1f, -Math.PI, Math.PI, -Math.PI, Math.PI, 1, 0.01,
//                                Surface.curveWrapping(
//                                        x -> new Point3D(0, 0, x),
//                                        t -> new Point2D(sin(t), cos(t))
//                                )
//                        )
//                );
//                gp.addRender(new Area(gp, Color.BLUE, true, 1f, -Math.PI, Math.PI, -Math.PI, Math.PI, 0.1, 0.1,
//                        Surface.curveWrapping(x -> new Point3D(x / PI, Math.sin(x), Math.cos(x)), t -> new Point2D(sin(t) / 5, cos(t) / 10))));
        //        gp.addRender(new GameOfLife2D(gp, 50, 50));
        //        gp.addRender(new GameOfLife3D(gp, 20));
        //        gp.addRender(new VectorField2D(gp, 15, 12));
        //        gp.addRender(new ComplexFunctionVisualization(gp, z -> z.power(2)));
        //        gp.addRender(new Mapper2DVisualization(gp, (x, y) -> new Point2D(x*x-y*y, x*x+y*y)));
        //        gp.addRender(new PuzzleGame(gp, 4, 4));
        //        gp.setFps(1_000_000);
//                gp.addRender(new Area(gp, "t.obj"));
        //        gp.start();
        //        gp.start();
//                gp.addRender(new PathFinder(gp, 30, 30, new Point(2, 5), new Point(25, 20)));
        //        gp.addRender(new Snake(gp, 10, 10));
        //        gp.getRenderManager().addTick(() -> {
        //            try {
        //                Thread.sleep(1000);
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        });
        //        var ps = List.of(
        //                new Point2D(0.1, 3.534),
        //                new Point2D(0.122, 3.831),
        //                new Point2D(0.1471, 4.292),
        //                new Point2D(0.1786, 4.785),
        //                new Point2D(0.2128, 5.263),
        //                new Point2D(0.2564, 5.917),
        //                new Point2D(0.3030, 6.369)
        //                );
        //        gp.addRender(g2d -> {
        //            g2d.setColor(Color.RED);
        //            Graph2DCanvas.simplePlotter2D(ps, gp, g2d);
        //                });
        //        f.add(new ImageCanvas(new ImageIcon("this3.png").getImage()));
//        gp.addRender(new Area(gp, Utils.randomColor(),
//                -5, 5,
//                -5, 5,
//                0.1, 0.1,
//                (x, y) -> 1 + x*x / 20 - 0.5 * cos(2*PI*x) + y*y / 20 - 0.5 * cos(2*PI*y)));
        SwingUtilities.invokeLater(f);
    }
}
