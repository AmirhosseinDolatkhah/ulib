package utils;

import jmath.datatypes.functions.ColorFunction;
import visualization.canvas.Canvas;
import visualization.canvas.CoordinatedCanvas;
import visualization.canvas.Render;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
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
        if (numOfThreads < 1) {
            var len = src.length;
            for (int i = 0; i < len; i++)
                src[i] = func.applyAsInt(i);
            return;
        }
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
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntRgbArray(res), i -> colorFunc.applyAsInt(i/width, i%width), numOfThreads);
        return res;
    }

    public static BufferedImage createImage(int width, int height, IntUnaryOperator colorFunc, int numOfThreads) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntRgbArray(res), colorFunc, numOfThreads);
        return res;
    }

    public static BufferedImage createImage(CoordinatedCanvas cc, ColorFunction colorFunc, int numOfThreads) {
        var w = cc.getWidth();
        var h = cc.getHeight();
        var res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntRgbArray(res), i -> colorFunc.valueAt(cc.coordinateX(i/w), cc.coordinateY(i%w)).getRGB(), numOfThreads);
        return res;
    }

    public static BufferedImage createImage(int width, int height, Render render) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        render.render(g2d);
        g2d.dispose();
        return res;
    }

    public static BufferedImage[] createImageSequence(int width, int height, Render render, int numOfFrames) {
        var res = new BufferedImage[numOfFrames];
        for (int i = 0; i < numOfFrames; i++) {
            var bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            var g2d = bi.createGraphics();
            render.render(g2d);
            g2d.dispose();
            res[i] = bi;
            render.tick();
        }
        return res;
    }

    public static BufferedImage createMergeImageFromImageSequence(BufferedImage[] imageSequence) {
        if (imageSequence == null || imageSequence.length == 0)
            throw new RuntimeException("AHD:: image sequence is null or empty");
        var res = new BufferedImage(imageSequence[0].getWidth(), imageSequence[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        Arrays.stream(imageSequence).forEach(bi -> g2d.drawImage(bi, 0, 0, null));
        g2d.dispose();
        return res;
    }

    public static BufferedImage createImageSingleThread(int width, int height, IntUnaryOperator colorFunc) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var arr = getIntRgbArray(res);
        for (int i = 0; i < arr.length; i++)
            arr[i] = colorFunc.applyAsInt(i);
        return res;
    }

    public static void affectOnImageSingleThread(BufferedImage image, IntUnaryOperator func) {
        var pixels = getIntRgbArray(image);
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = func.applyAsInt(pixels[i]);
    }

    public static void affectOnImage(BufferedImage image, IntUnaryOperator func, int numOfThreads) {
        var pixels = getIntRgbArray(image);
        multiThreadIntArraySetter(pixels, i -> func.applyAsInt(pixels[i]), numOfThreads);
    }

    public static <T> AtomicReference<T> checkTimePerform(Task<T> task, boolean inCurrentThread, String name, Object... args) {
        long t = System.currentTimeMillis();
        var res = new AtomicReference<T>();
        if (inCurrentThread) {
            res.set(task.task(args));
            System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
        } else {
            new Thread(() -> {
                res.set(task.task(args));
                System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
            }).start();
        }
        return res;
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        var f = (IntUnaryOperator) i -> (int) (Math.random() * Math.sin(i*i*i) - Math.cos(i * Math.sin(Math.random()) - i * i + i));
        var arr = new int[1_000_000_0];
        var time = System.currentTimeMillis();
        var l = arr.length;
        for (int i = 0; i < l; i++)
            arr[i] = f.applyAsInt(i);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        multiThreadIntArraySetter(arr, f, 20);
        System.out.println(System.currentTimeMillis() - time);
    }

    public static void arrayShuffle(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int rp = (int) (Math.random() * arr.length);
            var temp = arr[i];
            arr[i] = arr[rp];
            arr[rp] = temp;
        }
    }

    private static int mergeAndCount(int[] arr, int l, int m, int r) {
        var left = Arrays.copyOfRange(arr, l, m + 1);
        var right = Arrays.copyOfRange(arr, m + 1, r + 1);
        int i = 0, j = 0, k = l, swaps = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j])
                arr[k++] = left[i++];
            else {
                arr[k++] = right[j++];
                swaps += (m + 1) - (l + i);
            }
        }
        while (i < left.length)
            arr[k++] = left[i++];
        while (j < right.length)
            arr[k++] = right[j++];
        return swaps;
    }

    private static int mergeSortAndCount(int[] arr, int l, int r) {
        int count = 0;
        if (l < r) {
            int m = (l + r) / 2;
            count += mergeSortAndCount(arr, l, m);
            count += mergeSortAndCount(arr, m + 1, r);
            count += mergeAndCount(arr, l, m, r);
        }
        return count;
    }
    
//    public static void main(String[] args) {
//        int[] arr = new int[1000_000_0];
//        Arrays.setAll(arr, ii -> (int) (Math.random() * Integer.MAX_VALUE));
//        System.out.println(mergeSortAndCount(arr, 0, arr.length - 1));
//    }

    @FunctionalInterface
    public interface Task<T> {
        T task(Object... args);
    }
}
