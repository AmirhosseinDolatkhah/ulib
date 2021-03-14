package visualization.render3D.raytracer;

import swingutils.MainFrame;
import utils.Utils;
import utils.supplier.ColorSupplier;
import visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

public final class ColorUtil {
    public static Color getColor(int x, int y, int z, int nx, int ny, int nz) {
        return new Color((float) x / nx, (float) y / ny, (float) z / nz);
    }

    public static Color getColor(int x, int y, int nx, int ny) {
        return getColor(x, y, 4, nx, ny, 5);
    }

    public static ColorCalculator getColorGenerator(IntSupplier width, IntSupplier height) {
        return new ColorCalculator(width, height);
    }

    public static void main(String[] args) {
        var frame = new MainFrame();
        var gp = new Graph3DCanvas();
        frame.add(gp);
        var cg =  getColorGenerator(gp::getWidth, gp::getHeight);
        gp.addRender(g -> g.drawImage(Utils.createImage(gp.getWidth(), gp.getHeight(), (IntUnaryOperator) cg, 1), 0, 0, null));

        SwingUtilities.invokeLater(frame);
    }

    protected static class ColorCalculator implements IntUnaryOperator, IntBinaryOperator {
        private final IntSupplier w;
        private final IntSupplier h;

        public ColorCalculator(IntSupplier w, IntSupplier h) {
            this.w = w;
            this.h = h;
        }

        @Override
        public int applyAsInt(int i) {
            var hh = h.getAsInt();
            var ww = w.getAsInt();

            return (((i * 128 / ww / hh + 128) & 0xFF) << 24) | (((i / ww) & 0xFF) << 16) |
                    (((i % ww) & 0xFF) << 8) | (((i * 256 / ww / hh) & 0xFF));
        }

        @Override
        public int applyAsInt(int i, int j) {
            return applyAsInt(i * w.getAsInt() + j);
        }
    }
}
