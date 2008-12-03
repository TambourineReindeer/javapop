package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Vector3;


public class Helpers {

    public static float PointLineDistance(Vector3 line1, Vector3 line2, Vector3 point) {
        Vector3 a, b;

        a = new Vector3();
        b = new Vector3();
        a.sub(line2, line1);
        b.sub(line1, point);

        b.cross(a, b);

        return b.length() / a.length();
    }

    

    public static Vector3 calcNormal(final Vector3 a, final Vector3 b, final Vector3 c) {
        Vector3 ab,ac ,n ;
        ab = new Vector3(b);
        ab.sub(a);
        ac = new Vector3(c);
        ac.sub(a);

        n = new Vector3();
        n.cross(ab, ac);
        n.normalize();

        return n;
    }
}
