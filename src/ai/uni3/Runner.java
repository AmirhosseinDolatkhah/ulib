package ai.uni3;

import swingutils.MainFrame;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;

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

        SwingUtilities.invokeLater(f);
    }
}
