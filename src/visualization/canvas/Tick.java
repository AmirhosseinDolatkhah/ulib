package visualization.canvas;

@FunctionalInterface
public interface Tick extends Runnable {
    void tick();

    @Override
    default void run() {
        tick();
    }
}
