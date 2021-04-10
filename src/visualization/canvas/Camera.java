package visualization.canvas;

import jmath.datatypes.tuples.Point3D;
import visualization.canvas.CoordinatedScreen;

import javax.swing.*;
import java.awt.*;

public class Camera extends RenderManager {
    private CoordinatedScreen cs;

    private Point3D pos;
    private Point3D angles;

    public Camera(CoordinatedScreen cs, Point3D pos, Point3D angles) {
        this.pos = pos;
        this.angles = angles;
        this.cs = cs;
    }

    public Camera(CoordinatedScreen cs) {
        this(cs, new Point3D(), new Point3D());
    }

    public Camera() {
        this(null);
    }

    public void move(double dx, double dy, double dz) {
        pos.x += dx;
        pos.y += dy;
        pos.z += dz;
    }

    public void setPos(double x, double y, double z) {
        pos.x = x;
        pos.y = y;
        pos.z = z;
    }

    public void setAngle(double roll, double yaw, double pitch) {
        angles.x = roll;
        angles.y = yaw;
        angles.z = pitch;
    }

    public void rotate(double dRoll, double dYaw, double dPitch) {
        angles.x += dRoll;
        angles.y += dYaw;
        angles.z += dPitch;
    }

    public double getRoll() {
        return angles.x;
    }

    public void setRoll(double roll) {
        angles.x = roll;
    }

    public double getPitch() {
        return angles.z;
    }

    public void setPitch(double pitch) {
        angles.z = pitch;
    }

    public double getYaw() {
        return angles.y;
    }

    public void setYaw(double yaw) {
        angles.y = yaw;
    }

    public double getX() {
        return pos.x;
    }

    public double getY() {
        return pos.y;
    }

    public double getZ() {
        return pos.z;
    }

    public void setX(double x) {
        pos.x = x;
    }

    public void setY(double y) {
        pos.y = y;
    }

    public void setZ(double z) {
        pos.z = z;
    }

    public void setPos(Point3D pos) {
        this.pos = pos;
    }

    public Point3D getAngles() {
        return angles;
    }

    public void setAngles(Point3D angles) {
        this.angles = angles;
    }

    public void setCs(CoordinatedScreen cs) {
        this.cs = cs;
    }

    public CoordinatedScreen getCs() {
        return cs;
    }

    public Point3D getPos() {
        return pos;
    }

    public boolean inViewPort(Point3D p) {
//        boolean windowSizeAffect = true;
//        if (cs instanceof JComponent) {
//            var component = (JComponent) cs;
//            windowSizeAffect =
//                    p.x > cs.coordinateX(0) &&
//                    p.x < cs.coordinateX(component.getWidth()) &&
//                    p.y < cs.coordinateY(0) &&
//                    p.y > cs.coordinateY(component.getHeight());
//        }
        return p.z < p.z;
    }
}
