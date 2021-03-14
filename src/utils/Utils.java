package utils;

import jmath.datatypes.functions.ColorFunction;
import visualization.canvas.Canvas;
import visualization.canvas.CoordinatedCanvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

public final class Utils {
    public static BufferedImage getCanvasImage(Canvas canvas) {
        var res = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        var g2d = res.createGraphics();
        canvas.paintComponents(g2d);
        g2d.dispose();
        return res;
    }

    public static void saveJComponentImage(String path, Canvas canvas) {
        try {
            ImageIO.write(getCanvasImage(canvas), path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : "jpg", new File(path.contains(".") ? path : path + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Color randomColor() {
        return new Color((int) (Math.random() * Integer.MAX_VALUE));
    }

    public static double round(double num, int precision) {
        if (!Double.toString(num).contains("."))
            return num;
        String res = num + "0".repeat(20);
        return Double.parseDouble(res.substring(0, res.indexOf('.') + precision + 1));
    }

    public static int[] getIntRgbArray(BufferedImage bi) {
        return ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
    }

    public static void multiThreadIntArraySetter(int[] src, IntUnaryOperator func, int numOfThreads) {
        var list = new Thread[numOfThreads];
        var partLen = src.length / numOfThreads;
        for (var i = 0; i < numOfThreads; i++) {
            final var counter = i;
            var t = new Thread(() -> {
                var start = counter * partLen;
                var end = counter == numOfThreads - 1 ? src.length : start + partLen;
                for (int j = start; j < end; j++)
                    src[j] = func.applyAsInt(j);
            });
            list[counter] = t;
            t.start();
        }
        for (var t : list)
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static BufferedImage createImage(int width, int height, IntBinaryOperator colorFunc, int numOfThreads) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        multiThreadIntArraySetter(getIntRgbArray(res), i -> colorFunc.applyAsInt(i/width, i%width), numOfThreads);
        return res;
    }

    public static BufferedImage createImage(int width, int height, IntUnaryOperator colorFunc, int numOfThreads) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        multiThreadIntArraySetter(getIntRgbArray(res), colorFunc, numOfThreads);
        return res;
    }

    public static BufferedImage createImage(CoordinatedCanvas cc, ColorFunction colorFunc, int numOfThreads) {
        var w = cc.getWidth();
        var h = cc.getHeight();
        var res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        multiThreadIntArraySetter(getIntRgbArray(res), i -> colorFunc.valueAt(cc.coordinateX(i/w), cc.coordinateY(i%w)).getRGB(), numOfThreads);
        return res;
    }

    public static void main(String[] args) {
        var arr = new int[10];
        multiThreadIntArraySetter(arr, i -> i*i, 3);
        System.out.println(Arrays.toString(arr));
    }

    public static void arrayShuffle(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int rp = (int) (Math.random() * arr.length);
            var temp = arr[i];
            arr[i] = arr[rp];
            arr[rp] = temp;
        }
    }
}
