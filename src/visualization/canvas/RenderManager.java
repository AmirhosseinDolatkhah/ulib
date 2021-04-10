package visualization.canvas;

import utils.Utils;
import visualization.shapes.shape3d.Area;
import visualization.shapes.shape3d.FlatSurface;
import visualization.shapes.shape3d.Shape3D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class RenderManager extends ArrayList<Render> implements Render {
    private static final long MAX_SINGLE_THREADED_TICK_TIME = 80;
    private int renderCounter;
    private int tickCounter;
    private final ThreadPoolExecutor tickExecutor;
    private final Runnable tickRunnable;
    private final AtomicLong lastTickTime;
    private long lastRenderTime;

    public RenderManager(Render... renders) {
        super(Arrays.asList(renders));
        tickExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
        lastTickTime = new AtomicLong();
        renderCounter = 0;
        tickCounter = 0;
        tickRunnable = () -> {
            var t = System.currentTimeMillis();
            forEach(Tick::tick);
            lastTickTime.set(System.currentTimeMillis() - t);
            tickCounter++;
        };
    }

    public int numOfAliveTickThreads() {
        return tickExecutor.getActiveCount();
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
        var t = System.currentTimeMillis();
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        var list = new ArrayList<Shape3D>();
        stream().filter(Area.class::isInstance).map(e -> ((Area) e).getComponents()).forEach(list::addAll);
        list.sort(Comparator.comparingDouble(f -> f.getCenter().z));
        list.forEach(r -> r.renderIfInView(g2d));
        stream().filter(e -> !(e instanceof Area)).forEach(render -> render.renderIfInView(g2d));
        lastRenderTime = System.currentTimeMillis() - t;
        renderCounter++;
    }

    public int getRenderCounter() {
        return renderCounter;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void resetCounters() {
        renderCounter = 0;
        tickCounter = 0;
    }

    public synchronized void asyncTickCounterChange(int change) {
        tickCounter += change;
    }

    public boolean singleThreadedTick() {
        return lastTickTime.get() < MAX_SINGLE_THREADED_TICK_TIME;
    }

    public long tickRoundTime() {
        return lastTickTime.get();
    }

    public long renderRoundTime() {
        return lastRenderTime;
    }

    @Override
    public void tick() {
        if (lastTickTime.get() < MAX_SINGLE_THREADED_TICK_TIME) {
            var t = System.currentTimeMillis();
            forEach(Tick::tick);
            lastTickTime.set(System.currentTimeMillis() - t);
            tickCounter++;
        } else {
            tickExecutor.execute(tickRunnable);
        }
    }
}
