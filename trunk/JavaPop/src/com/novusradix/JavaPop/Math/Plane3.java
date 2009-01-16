package com.novusradix.JavaPop.Math;

/**
 *
 * @author gef
 */
public class Plane3 {

    private Vector3 p,  n;

    public Plane3() {
        p = new Vector3();
        n = new Vector3(0, 0, 1);
    }

    public Plane3(Vector3 a, Vector3 b, Vector3 c) {
        p = new Vector3(a);
        Vector3 ab,ac ;
        ab = new Vector3(b);
        ac = new Vector3(c);
        ab.sub(a);
        ac.sub(a);

        n = new Vector3();
        n.cross(ab, ac);
        n.normalize();
    }

    public void set(Vector3 a, Vector3 b, Vector3 c) {
        p.set(a);
        n.cross(b.x - a.x, b.y - a.y, b.z - a.z, c.x - a.x, c.y - a.y, c.z - a.z);
        n.normalize();
    }

    public float distance(Vector3 point) {
        return (point.x - p.x) * n.x + (point.y - p.y) * n.y + (point.z - p.z) * n.z;
    }
}
