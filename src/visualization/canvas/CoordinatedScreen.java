package visualization.canvas;

public interface CoordinatedScreen {
    int screenX(double coordinateX);
    int screenY(double coordinateY);

    double coordinateX(int screenX);
    double coordinateY(int screenY);

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
        return coordinateY(screenYLen) - coordinateY(0);
    }
}
