package ai.uni3;

import swingutils.MainFrame;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import static utils.Utils.unsafeExecutor;

public class Runner {
    public static void run(String filePath) {
        var f = new MainFrame("Table Solver (CSP)");
        var gp = new Graph3DCanvas();
        var vt = new VisualTable(gp, VisualTable.readTableFromFile(filePath));
        var mp = new JPanel(new BorderLayout());
        f.addState("main", mp);
        mp.add(gp);
        f.setState("main");
        gp.addRender(vt);
        unsafeExecutor.execute(() -> {
            System.out.println(vt.getAlgorithm().solve(false) ? "Solved" : "Error");
            vt.getAlgorithm().getDomainMap().forEach((k, v) -> System.out.println(k.x + " " + k.y + " " + v));
        });
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                vt.getAlgorithm().release("solve");
                System.out.println(vt.getAlgorithm().getDomainMap().get(new Point(0, 1)));
            }
        });
        vt.getAlgorithm().getReleaseTimer("solve", 1).start();
        new Timer(10, e -> gp.repaint()).start();
        SwingUtilities.invokeLater(f);
    }
}
