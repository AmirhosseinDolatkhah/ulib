package utils;

import visualization.canvas.Canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
}
