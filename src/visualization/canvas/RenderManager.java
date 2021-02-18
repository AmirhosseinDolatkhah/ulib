package visualization.canvas;

import visualization.shapes.shapes3d.FlatSurface;
import visualization.shapes.shapes3d.Shape3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderManager extends ArrayList<Render> implements Render {
    public RenderManager(Render... renders) {
        super(Arrays.asList(renders));
    }

    public void addRender(Render... renders) {
        addAll(Arrays.asList(renders));
    }

    @Override
    public void render(Graphics2D g2d) {
        forEach(render -> render.render(g2d));
    }
}
