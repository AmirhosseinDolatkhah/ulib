package visualization.canvas;

import visualization.shapes.shape3d.Area;
import visualization.shapes.shape3d.FlatSurface;
import visualization.shapes.shape3d.Shape3D;

import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RenderManager extends ArrayList<Render> implements Render {
    private final ReentrantLock lock;
    private int renderCounter;
    private int tickCounter;
    private final Runnable tickRunnable;
    private final Timer checkTickThreadsTimer;
    private final ArrayList<Thread> tickThreads;

    public RenderManager(Render... renders) {
        super(Arrays.asList(renders));
        lock = new ReentrantLock();
        renderCounter = 0;
        tickCounter = 0;
        tickRunnable = () -> {
            lock.lock();
            var threads = new ArrayList<Thread>();
            for (int i = 0; i < size(); i++) {
                int finalI = i;
                var t = new Thread(() -> get(finalI).tick());
                threads.add(t);
                t.start();
            }
            for (var t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tickCounter++;
            lock.unlock();
        };
        tickThreads = new ArrayList<>();
        checkTickThreadsTimer = new Timer(15, e -> {
            for (int i = 0; i < tickThreads.size(); i++)
                if (!tickThreads.get(i).isAlive())
                    tickThreads.remove(i--);
        });
        checkTickThreadsTimer.start();
    }

    public int numOfAliveTickThreads() {
        return tickThreads.size();
    }

    public Timer getCheckTickThreadsTimer() {
        return checkTickThreadsTimer;
    }

    public void addRender(Render... renders) {
        addAll(Arrays.asList(renders));
    }

    public void addTick(Runnable... ticks) {
        addRender(Arrays.stream(ticks).map(t -> new Render() {
            @Override
            public void render(Graphics2D g2d) {}

            @Override
            public void tick() {
                t.run();
            }
        }).collect(Collectors.toList()).toArray(new Render[] {}));
    }

    public List<Shape3D> getShape3d() {
        return stream().filter(e -> e instanceof Shape3D).map(e -> (Shape3D) e).collect(Collectors.toList());
    }

    public List<FlatSurface> getFlatSurfaces() {
        return stream().filter(e -> e instanceof FlatSurface).map(e -> (FlatSurface) e).collect(Collectors.toList());
    }

    public <T> List<T> get(Class<T> clazz) {
        //noinspection unchecked
        return stream().filter(clazz::isInstance).map(e -> (T) e).collect(Collectors.toList());
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.addRenderingHints(Map.of(
                RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY,
                RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE,
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON,
                RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE,
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC
//                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        ));
//        lock.lock();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        var list = new ArrayList<Shape3D>();
        stream().filter(Area.class::isInstance).map(e -> ((Area) e).getComponents()).forEach(list::addAll);
        list.sort(Comparator.comparingDouble(f -> f.getCenter().z));
        list.forEach(r -> r.renderIfInView(g2d));
        stream().filter(e -> !(e instanceof Area)).forEach(render -> render.renderIfInView(g2d));
        renderCounter++;
//        lock.unlock();
    }

    public int getRenderCounter() {
        return renderCounter;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void resetCounters() {
        lock.lock();
        renderCounter = 0;
        tickCounter = 0;
        lock.unlock();
    }

    @Override
    public void tick() {
//        if (tickThreads.size() > 80)
//            return;
//        var t = new Thread(tickRunnable);
//        tickThreads.add(t);
//        t.start();
//        tickRunnable.run();
        forEach(Render::tick);
        tickCounter++;
    }
}
