package visualization.render3D.camera;

import jmath.datatypes.tuples.Point3D;
import visualization.canvas.CoordinatedScreen;

import javax.swing.*;

public class Camera {
    private double x, y, z;
    private double roll, pitch, yaw;
    private final CoordinatedScreen cs;

    public Camera(CoordinatedScreen cs, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.cs = cs;
    }

    public void move(double dx, double dy, double dz) {
        x += dx;
        y += dy;
        z += dz;
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setAngle(double roll, double yaw, double pitch) {
        this.roll = roll;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void rotate(double dRoll, double dYaw, double dPitch) {
        roll += dRoll;
        yaw += dYaw;
        pitch += dPitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public CoordinatedScreen getCs() {
        return cs;
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
        return p.z < z;
    }
}
