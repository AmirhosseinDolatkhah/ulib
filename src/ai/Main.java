package ai;

import ai.uni.PathFinderVisualPanel;
import swingutils.MainFrame;
import utils.Utils;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        PathFinderVisualPanel p;
        gp.addRender(p = new PathFinderVisualPanel(gp, "test3.txt"));
        f.add(gp);
        var timer = p.getAlgorithm().getReleaseTimer("step", 40);
        gp.addMouseListener(new MouseAdapter() {
            private boolean running = false;

            @Override
            public void mousePressed(MouseEvent e) {
                timer.stop();
                if (e.isControlDown()) {
                    running = !running;
                    if (running)
                        timer.start();
                    else
                        timer.stop();
                } else if (e.isShiftDown()) {
                    p.getAlgorithm().release("step");
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (running)
                    timer.start();
            }

        });
        gp.start();
        SwingUtilities.invokeLater(f);
    }
}
