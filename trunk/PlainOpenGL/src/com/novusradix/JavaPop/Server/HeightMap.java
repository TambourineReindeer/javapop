/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.vecmath.Point2f;

import com.sun.opengl.util.BufferUtil;
import java.nio.IntBuffer;

/**
 *
 * @author mom
 */
public class HeightMap {

    private int width,  breadth;
    private IntBuffer b;
    private static int rowstride;
    private static int VZ = 0;

    public HeightMap(int width, int breadth) {

        b = BufferUtil.newIntBuffer(width * breadth);

        this.breadth = breadth;
        this.width = width;
        rowstride = width;
        int x, y;
        for (y = 0; y < breadth; y++) {
            for (x = 0; x < width; x++) {


                b.put(0);

            }
        }
        b.flip();
        randomize();
    }

    public int getWidth() {
        return width;
    }

    public int getBreadth() {
        return breadth;
    }

    private static int bufPos(int x, int y, int vertex, int index) {
        return y * rowstride + x;
    }
    private static int bufPos(int x, int y) {
        return y * rowstride + x;
    }

    public int getHeight(int x, int y) {
        try {
            return (int) b.get(bufPos(x, y, 0, VZ));
        } catch (Exception e) {
            System.out.println("Array out of bounds in getHeight:" + x + ", " + y);           
        }
        return 0;
    }

    public float getHeight2(int x, int y) {
        try {
            int ha,hb,hc,hd;
            ha = getHeight(x,y);
            hb = getHeight(x,y);
            hc = getHeight(x,y);
            hd = getHeight(x,y);
            
            return (Math.max(Math.max(ha,hb),Math.max(hb,hc)) + Math.min(Math.min(ha,hb),Math.min(hc,hd))) / 2.0f;
        } catch (Exception e) {
             System.out.println("Array out of bounds in getHeight2:" + x + ", " + y);           

        }
        return 0;
    }

    public float getHeight(float x, float y) {
        int x1, x2, y1, y2;
        float ha, hb, hc, hd, hm; 
        x1 = (int) Math.floor(x);
        x2 = (int) Math.ceil(x);
        y1 = (int) Math.floor(y);
        y2 = (int) Math.ceil(y);

        x = x - x1;
        y = y - y1;

        ha = getHeight(x1, y1);
        hb = getHeight(x1, y2);
        hc = getHeight(x2, y1);
        hd = getHeight(x2, y2);
        hm = ha;
        if (hb > ha || hc > ha || hd > ha) {
            hm = ha + 0.5f;
        }
        if (hb < ha || hc < ha || hd < ha) {
            hm = ha - 0.5f;
        }
        if (y > x) {
            if (y > 1 - x) {
                // BMD
                return hb + (hd - hb) * x + (hm - (hd + hb) / 2.0f) * (1 - y);
            } else {
                // AMB
                return ha + (hb - ha) * y + (hm - (hb + ha) / 2.0f) * x;
            }
        } else {
            if (y > 1 - x) {
                // CMD
                return hc + (hd - hc) * y + (hm - (hd + hc) / 2.0f) * (1 - x);
            } else {
                // AMC
                return ha + (hc - ha) * x + (hm - (ha + hb) / 2.0f) * y;
            }
        }
    }

    public Point2f getSlope(float x, float y) {
        int x1, x2, y1, y2;
        float ha, hb, hc, hd, hm;
        x1 = (int) Math.floor(x);
        x2 = (int) Math.ceil(x);
        y1 = (int) Math.floor(y);
        y2 = (int) Math.ceil(y);

        x = x - x1;
        y = y - y1;

        ha = getHeight(x1, y1);
        hb = getHeight(x1, y2);
        hc = getHeight(x2, y1);
        hd = getHeight(x2, y2);

        if (ha == hb && hb == hc && hc == hd) {
            return new Point2f(0, 0);
        }
        hm = ha;
        if (hb > ha || hc > ha || hd > ha) {
            hm = ha + 0.5f;
        }
        if (hb < ha || hc < ha || hd < ha) {
            hm = ha - 0.5f;
        }
        if (y > x) {
            if (y > 1 - x) {
                // BMD
                return new Point2f(hd - hb, 2.0f * ((hd + hb) / 2.0f - hm));
            } else {
                // AMB
                return new Point2f(2.0f * (hm - (ha + hb) / 2.0f), hb - ha);
            }
        } else {
            if (y > 1 - x) {
                // CMD
                return new Point2f(2.0f * ((hc + hd) / 2.0f - hm), hd - hc);
            } else {
                // AMC
                return new Point2f(hc - ha, 2.0f * (hm - (ha + hc) / 2.0f));
            }
        }
    }

    public void randomize() {
        int n, m;
        int x, y;
        Random r = new Random();
        for (n = 0; n < 100; n++) {
            x = r.nextInt(width);
            y = r.nextInt(breadth);
            for (m = 0; m < r.nextInt(8); m++) {
                up(x, y);
            }
            for (m = 0; m < r.nextInt(8); m++) {
                up(x - 5 + r.nextInt(10), y - 5 + r.nextInt(10));
            }
            for (m = 0; m < r.nextInt(2); m++) {
                down(x - 5 + r.nextInt(10), -5 + r.nextInt(10));
            }
            for (m = 0; m < r.nextInt(2); m++) {
                down(x, y);
            }
        }
    }

    public void up(int x, int y) {
        setHeight(x, y, getHeight(x, y) + 1);
    }

    public void down(int x, int y) {
        setHeight(x, y, Math.max(getHeight(x, y) - 1, 0));
    }

    private void setHeight(int x, int y, int height) {
        if (x >= 0 && y >= 0 && x <= width && y <= breadth) {
            if (x < width && y < width) {
                b.put(bufPos(x, y, 0, VZ), height);
                b.put(bufPos(x, y, 10, VZ), height);
            }
            if (x >= 1 && y >= 1) {
                b.put(bufPos(x - 1, y - 1, 4, VZ), height);
                b.put(bufPos(x - 1, y - 1, 6, VZ), height);
            }
            if (y >= 1 && x < width) {
                b.put(bufPos(x, y - 1, 7, VZ), height);
                b.put(bufPos(x, y - 1, 9, VZ), height);
            }
            if (x >= 1 && y < width) {
                b.put(bufPos(x - 1, y, 1, VZ), height);
                b.put(bufPos(x - 1, y, 3, VZ), height);
            }
        }
    }

    public boolean isFlat(int x, int y) {
        int a = 0, b = 0, c = 0, d = 0;
        if (x < 0 || y < 0 || x + 1 >= width || y + 1 >= breadth) {
            return false;
        }
        a = getHeight(x, y);
        b = getHeight(x, y + 1);
        c = getHeight(x + 1, y);
        d = getHeight(x + 1, y + 1);
        return (a == b && b == c && c == d);
    }

   }