package visualization.render3D.camera;

import visualization.canvas.CoordinatedScreen;
import visualization.canvas.Graph3DCanvas;
import visualization.canvas.Render;

import java.awt.*;
import java.util.ArrayList;

public class Camera implements Render {
    private ArrayList<Render> objects;
    private CoordinatedScreen canvas;

    public Camera(CoordinatedScreen canvas) {
        this.canvas = canvas;
//        objects = new ArrayList<>(canvas.getRenderManager().getRenderList());
    }

    @Override
    public void render(Graphics2D g2d) {}

    @Override
    public void tick() {

    }
}
