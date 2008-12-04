package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Vector3;
import java.awt.Point;


public class Helpers {

    public static Point[][] rings;
    
    static {
        rings = new Point[10][];
        rings[0] = new Point[1];
        rings[0][0] = new Point(0,0);
        for(int i=1;i<10;i++)
        {
            rings[i] = new Point[8*i];
            for(int n=0;n<i*2;n++)
            {
                rings[i][n]     = new Point(n-i,i);
                rings[i][n+2*i] = new Point(i,i-n);
                rings[i][n+4*i] = new Point(i-n,-i);
                rings[i][n+6*i] = new Point(-i,n-i);
                
            }
        }
    }
    
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
