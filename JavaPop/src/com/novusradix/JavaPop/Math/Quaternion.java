package com.novusradix.JavaPop.Math;

/**
 *
 * @author gef
 */
public class Quaternion {

    public float w,  x,  y,  z;

    public Quaternion() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    public Quaternion(float w, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Rotation r) {
        w = (float) Math.cos(r.angle / 2.0);
        float sinTerm = (float) Math.sin(r.angle / 2.0);
        x = r.axis.x * sinTerm;
        y = r.axis.y * sinTerm;
        z = r.axis.z * sinTerm;
    }

    public Quaternion(Vector4 v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = v.w;
    }

    public Quaternion(Quaternion v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = v.w;
    }

    public static Quaternion multiply(Quaternion a, Quaternion b) {
        Quaternion q = new Quaternion();
        q.w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;
        q.x = a.x * b.w + a.w * b.x + a.y * b.z - a.z * b.y;
        q.y = a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x;
        q.z = a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w;
        return q;
    }
}
