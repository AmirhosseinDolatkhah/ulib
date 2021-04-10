package visualization.canvas;

import jmath.datatypes.tuples.AbstractPoint;
import jmath.datatypes.tuples.Point2D;
import jmath.datatypes.tuples.Point3D;

import java.awt.*;

public interface CoordinatedScreen {
    int screenX(double coordinateX);
    int screenY(double coordinateY);

    double coordinateX(int screenX);
    double coordinateY(int screenY);

    Camera camera();

    default int screenXLen(double coordinateXLen) {
        return screenX(coordinateXLen) - screenX(0);
    }

    default int screenYLen(double coordinateYLen) {
        return -(screenY(coordinateYLen) - screenY(0));
    }

    default double coordinateXLen(int screenXLen) {
        return coordinateX(screenXLen) - coordinateX(0);
    }

    default double coordinateYLen(int screenYLen) {
        return -(coordinateY(screenYLen) - coordinateY(0));
    }

    default Point screen(Point2D p) {return new Point();}

    default Point screen(Point3D p) {return new Point();}

    default Point screen(AbstractPoint point) {
        if (point instanceof Point3D)
            return screen((Point3D) point);
        if (point instanceof Point2D)
            return screen((Point2D) point);
        throw new RuntimeException("AHD:: Only Point2D and Point3D is Considered");
    }

    static CoordinatedScreen of2D(Point2D center, double scaleX, double scaleY) {
        var res = new Graph2DCanvas();
        // TODO://
        return res;
    }
}

