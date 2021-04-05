package ai;

import ai.uni.PathFinderVisualPanel;
import swingutils.MainFrame;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        gp.addRender(new PathFinderVisualPanel(gp, "test1.txt"));
        f.add(gp);
        gp.start();
        SwingUtilities.invokeLater(f);
    }
}
