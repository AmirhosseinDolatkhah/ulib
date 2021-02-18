package visualization.canvas;

import java.awt.*;

@FunctionalInterface
public interface Render {
    void render(Graphics2D g2d);
    default void tick() {}
}
