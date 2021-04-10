package ai;

import ai.uni.PathFinderVisualPanel;
import swingutils.MainFrame;
import utils.Utils;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        PathFinderVisualPanel p;
        gp.addRender(p = new PathFinderVisualPanel(gp, "test1.txt"));
        f.add(gp);
        var b = new JButton("Step");
        var wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(b);
        f.add(wrapper, BorderLayout.SOUTH);
        b.addActionListener(e -> {
            p.getAlgorithm().release("begin-dls");
            f.repaint();
            gp.getRenderManager().asyncTickCounterChange(1);
        });
        p.getAlgorithm().getReleaseTimer("begin-dls", 1000, 5).start();
//        p.getAlgorithm().ignoreSemaphore("begin-dls");
        gp.camera().addTick(() -> Utils.sleep(2000));
        SwingUtilities.invokeLater(f);
    }
}
