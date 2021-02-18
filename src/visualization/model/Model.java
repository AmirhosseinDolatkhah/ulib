package visualization.model;


import visualization.canvas.Render;

import java.util.List;

public interface Model<T> extends Render {
    List<T> getVertexes();


}
