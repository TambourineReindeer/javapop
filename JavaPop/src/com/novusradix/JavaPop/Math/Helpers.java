package com.novusradix.JavaPop.Math;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Helpers {
public static final int radius = 32;

    public final static Point[][] rings;
    public final static ArrayList<ArrayList<Point>> shuffledRings;
    public final static Point[][] smallRings;
    public final static Point[] neighbours;

    static {
        rings = new Point[radius][];
        smallRings = new Point[radius][];
        shuffledRings = new ArrayList<ArrayList<Point>>();

        rings[0] = new Point[1];
        rings[0][0] = new Point(0, 0);
        for (int i = 1; i < radius; i++) {
            rings[i] = new Point[8 * i];
            for (int n = 0; n < i * 2; n++) {
                rings[i][n] = new Point(n - i, i);
                rings[i][n + 2 * i] = new Point(i, i - n);
                rings[i][n + 4 * i] = new Point(i - n, -i);
                rings[i][n + 6 * i] = new Point(-i, n - i);
            }
        }
        ArrayList<Point> a;
        for (Point[] r : rings) {
            a = new ArrayList<Point>();
            a.addAll(Arrays.asList(r));
            Collections.shuffle(a);
            shuffledRings.add(a);
        }

        for (int i = 0; i < radius; i++) {
            smallRings[i] = new Point[8 * i + 4];
            for (int n = 0; n < 2 * i + 1; n++) {
                smallRings[i][n] = new Point(i, i - n);
                smallRings[i][n + i * 2 + 1] = new Point(i - n, -i - 1);
                smallRings[i][n + i * 4 + 2] = new Point(-i - 1, n - i - 1);
                smallRings[i][n + i * 6 + 3] = new Point(n - i - 1, i);
            }
        }

        neighbours = new Point[9];
        System.arraycopy(rings[1], 0, neighbours, 0, 8);
        neighbours[8] = new Point(0,0);
    }

    //todo: rewrite to avoid object creation
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
        Vector3 ab, ac, n;
        ab = new Vector3(b);
        ab.sub(a);
        ac = new Vector3(c);
        ac.sub(a);

        n = new Vector3();
        n.cross(ab, ac);
        n.normalize();

        return n;
    }

    public static int clip(int i,int min, int max)
    {
        return Math.min(max,Math.max(min,i));
    }

    public static float clip(float f,float min, float max)
    {
        return Math.min(max,Math.max(min,f));
    }
}
