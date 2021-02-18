//package visualization.fractals;
//
//import animation.canvaspanel.CoordinatedCanvasPanel;
//import animation.canvaspanel.Graph2DPanel;
//import animation.canvaspanel.Graph3DPanel;
//import guitools.MainFrame;
//import jmath.datatypes.ComplexNumber;
//import jmath.datatypes.functions.Function3D;
//import utils.Utils;
//
//import static java.lang.Math.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class FractalPanel extends Graph3DPanel {
//
//    private final ArrayList<Thread> threads;
//    private int numOfThreads;
//    private boolean inProgress;
//    private int maxIteration;
//    private ColorScheme cs;
//    private int[] colorPalette;
//    private Function3D conditionFunction;
//    private Function3D realIterationFunction;
//    private Function3D imaginaryIterationFunction;
//    private double escapeRadius;
//
//    public FractalPanel() {
//        removeAllRenders();
//        inProgress = false;
//        numOfThreads = 15;
//        threads = new ArrayList<>(numOfThreads);
//        maxIteration = 1000;
//        cs = new ColorScheme(maxIteration);
//        colorPalette = new int[maxIteration];
//        for (int i = 0; i < maxIteration; i++)
//            colorPalette[i] = cs.getColor(i);
//        escapeRadius = 100;
//        conditionFunction = (x, y) -> x * x - y * y;
//        realIterationFunction = (x, y) -> x * x - y * y;
//        imaginaryIterationFunction = (x, y) -> 2 * x * y;
//    }
//
//    public Function3D getConditionFunction() {
//        return conditionFunction;
//    }
//
//    public void setConditionFunction(Function3D conditionFunction) {
//        this.conditionFunction = conditionFunction;
//        repaint();
//    }
//
//    public Function3D getRealIterationFunction() {
//        return realIterationFunction;
//    }
//
//    public void setRealIterationFunction(Function3D realIterationFunction) {
//        this.realIterationFunction = realIterationFunction;
//        repaint();
//    }
//
//    public Function3D getImaginaryIterationFunction() {
//        return imaginaryIterationFunction;
//    }
//
//    public void setImaginaryIterationFunction(Function3D imaginaryIterationFunction) {
//        this.imaginaryIterationFunction = imaginaryIterationFunction;
//        repaint();
//    }
//
//    public double getEscapeRadius() {
//        return escapeRadius;
//    }
//
//    public void setEscapeRadius(double escapeRadius) {
//        this.escapeRadius = escapeRadius;
//        repaint();
//    }
//
//    public int getMaxIteration() {
//        return maxIteration;
//    }
//
//    public void setMaxIteration(int maxIteration) {
//        this.maxIteration = maxIteration;
//        cs.setMaxIteration(maxIteration);
//        colorPalette = new int[maxIteration];
//        for (int i = 0; i < maxIteration; i++)
//            colorPalette[i] = cs.getColor(i);
//        repaint();
//    }
//
//    public int getNumOfThreads() {
//        return numOfThreads;
//    }
//
//    public void setNumOfThreads(int numOfThreads) {
//        this.numOfThreads = numOfThreads;
//    }
//
//    @Override
//    protected void beforePaint() {
//        if (inProgress)
//            return;
//        canvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//        g2d = canvas.createGraphics();
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setColor(backGround);
//        g2d.fillRect(0, 0, getWidth(), getHeight());
//
//        ColorScheme cs = new ColorScheme(maxIteration);
//        int[] colorPallet = new int[maxIteration];
//        for (int i = 0; i < maxIteration; i++)
//            colorPallet[i] = cs.getColor(i);
//
//        if (!inProgress) {
//            inProgress = true;
//
//            var width = canvas.getWidth();
//            var height = canvas.getHeight();
//
//            AtomicInteger counter = new AtomicInteger(0);
//            var tt = System.currentTimeMillis();
//            for (int k = 0; k < numOfThreads; k++) {
//                var t = new Thread(() -> {
//                    int iteration, point;
//                    double zx, zy, a, b, aOld, xtmp;
////                    ComplexNumber c, tmp;
//                    int n = counter.incrementAndGet();
//                    for (int i = (n - 1) * width / numOfThreads; i < width * n / numOfThreads; i++)
//                        for (int j = 0; j < height; j++) {
//                            iteration = 0;
//                            zx = coordinateX(i);
//                            zy = coordinateY(j);
//                            a = zx;
//                            b = zy;
////                            c = new ComplexNumber(zx, zy, false);
////                            tmp = new ComplexNumber(c);
//                            while (/*conditionFunction.valueAt(zx, zy)*/ zx * zx + zy * zy < escapeRadius && iteration < maxIteration) {
////                                xtmp = zy;
////                                zy = imaginaryIterationFunction.valueAt(zx, zy) + b;
////                                zx = realIterationFunction.valueAt(zx, xtmp) + a;
//
//                                xtmp = zx * zx - zy * zy;
//                                zy = 2* zx * zy + b;
//                                zx = xtmp + a;
//
////                                c = c.power(2).sum(tmp);
//
//                                iteration++;
//                            }
//
//                            if (iteration == maxIteration) {
//                                point = 0;
//                            } else {
//                                point = colorPallet[iteration];
//                            }
//                            canvas.setRGB(i, j, point);
//                        }
//                });
//                threads.add(t);
//                t.start();
//            }
//            for (var t : threads)
//                try {
//                    t.join();
//                } catch (Exception ignore) {}
//
//
//            System.out.println((System.currentTimeMillis() - tt) / 1000.0);
//            inProgress = false;
//            }
//
//    }
//
//    public static void main(String[] args) {
//        var fractalPanel = new FractalPanel();
//        var frame = new MainFrame("Fractal Rendering");
//        frame.add(fractalPanel);
//        SwingUtilities.invokeLater(frame);
//    }
//}
