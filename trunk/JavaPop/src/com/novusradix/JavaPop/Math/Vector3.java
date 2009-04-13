package com.novusradix.JavaPop.Math;

import java.io.Serializable;

/**
 *
 * @author gef
 */
public class Vector3 implements Serializable {

    public float x,  y,  z;

    public Vector3() {
        x = y = z = 0;
    }

    public Vector3(Vector3 v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void cross(Vector3 a, Vector3 b) {
        // A x B = <Ay*Bz - Az*By, Az*Bx - Ax*Bz, Ax*By - Ay*Bx>
        float tx, ty, tz;
        tx = a.y * b.z - a.z * b.y;
        ty = a.z * b.x - a.x * b.z;
        tz = a.x * b.y - a.y * b.x;

        x = tx;
        y = ty;
        z = tz;
    }

    public void cross(float ax, float ay, float az, float bx, float by, float bz) {
        // A x B = <Ay*Bz - Az*By, Az*Bx - Ax*Bz, Ax*By - Ay*Bx>
        x = ay * bz - az * by;
        y = az * bx - ax * bz;
        z = ax * by - ay * bx;
    }

    public void normalize() {
        float l = length();
        if (l > 0) {
            x = x / l;
            y = y / l;
            z = z / l;
        }
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void sub(Vector3 a, Vector3 b) {
        x = a.x - b.x;
        y = a.y - b.y;
        z = a.z - b.z;
    }

    public void sub(Vector3 a) {
        x -= a.x;
        y -= a.y;
        z -= a.z;
    }

    public void add(Vector3 a, Vector3 b) {
        x = a.x + b.x;
        y = a.y + b.y;
        z = a.z + b.z;
    }

    public void add(Vector3 a) {
        x += a.x;
        y += a.y;
        z += a.z;
    }

    public Vector3 inverse() {
        return new Vector3(-x,-y,-z);
    }
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void scaleAdd(float s, Vector3 a, Vector3 b) {
        x = a.x * s + b.x;
        y = a.y * s + b.y;
        z = a.z * s + b.z;
    }

    public float dot(Vector3 b) {
        return x * b.x + y * b.y + z * b.z;
    }
}
