package ai.uni2;

import swingutils.MainFrame;
import utils.Utils;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Runner {
    private static void run0(FitnessModel fitnessModel, String level) {
        var f = new MainFrame();

        var gp = new Graph3DCanvas();
        gp.setBackground(new Color(106, 133, 250));

        var v = new Visualization(gp, level);
        var algo = v.getAlgorithm();
        gp.addRender(v);

        var population = new JTextArea();
        int counter = 0;
        for (var p : algo.getPopulation())
            population.append(++counter + ") " + p + '\n');
        population.setEditable(false);
        population.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        var sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(population), gp);
        f.add(sp);

        new Thread(() -> {
            int generationCounter = 0;
            while (generationCounter < 2000) {
                algo.nextGeneration0();
                algo.nextGeneration();
                population.setText("");
                int counter_ = 0;
                for (var p : algo.getPopulation())
                    population.append(++counter_ + ") " + p + '\n');
                generationCounter++;
            }
            System.out.println(algo.getSolutions());
        }).start();

        SwingUtilities.invokeLater(f);
    }

    public static void run(FitnessModel fitnessModel, String filePath) throws IOException {
        run0(fitnessModel, Utils.getFileAsString(filePath));
    }
}
