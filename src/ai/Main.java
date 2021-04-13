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
        var b = new JButton("Step");
        b.addActionListener(e -> {
            p.getAlgorithm().release("begin-dls");
            f.repaint();
            gp.getRenderManager().asyncTickCounterChange(1);
        });
        var timer = p.getAlgorithm().getReleaseTimer("begin-dls", 40);
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
                    p.getAlgorithm().release("begin-dls");
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (running)
                    timer.start();
            }

        });
        gp.start();
//        p.getAlgorithm().ignoreSemaphore("begin-dls");
        SwingUtilities.invokeLater(f);
    }
}
