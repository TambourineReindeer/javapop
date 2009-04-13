package com.novusradix.JavaPop.Math;

/**
 *
 * @author gef
 */
public class Rotation {

    float angle;
    Vector3 axis;

    public Rotation() {
        axis = new Vector3();
        angle = 0;
    }

    public Rotation(Rotation r) {
        axis = new Vector3(r.axis);
        angle = r.angle;
    }

    public Rotation(Quaternion q) {
        angle = (float) (2 * Math.acos(q.w));
        axis = new Vector3(q.x, q.y, q.z);
        axis.normalize();
    }

    public static Rotation add(Rotation a, Rotation b) {
        Quaternion q1, q2;
        q1 = new Quaternion(a);
        q2 = new Quaternion(b);
        return new Rotation(Quaternion.multiply(q1, q2));
    }

    public Rotation scale(float s)
    {
        Rotation r = new Rotation(this);
        r.angle*=s;
        return r;
    }

    
}
