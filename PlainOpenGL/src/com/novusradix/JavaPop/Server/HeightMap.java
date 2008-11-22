/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.util.Random;

import javax.vecmath.Point2f;

import com.sun.opengl.util.BufferUtil;
import java.awt.Rectangle;
import java.nio.IntBuffer;
import java.util.Collection;

/**
 *
 * @author mom
 */
public class HeightMap {

    private int width,  breadth;
    private IntBuffer b;
    private static int rowstride;
    private static int VZ = 0;
    private Rectangle dirty;
    private Rectangle bounds;

    public HeightMap(int width, int breadth) {

        b = BufferUtil.newIntBuffer(width * breadth);

        this.breadth = breadth;
        this.width = width;
        rowstride = width;
        bounds = new Rectangle(0, 0, width, breadth);

        int x, y;
        for (y = 0; y < breadth; y++) {
            for (x = 0; x < width; x++) {


                b.put(0);

            }
        }
        b.flip();
    }

    public int[] getData() {
        int[] buf = new int[width * breadth];
        getData(buf);
        return buf;
    }

    public void getData(int[] buf) {
        b.position(0);
        b.get(buf);
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
            System.out.println("Array out of bounds in Server getHeight:" + x + ", " + y);
        }
        return 0;
    }

    public float getHeight2(int x, int y) {
        try {
            int ha, hb, hc, hd;
            ha = getHeight(x, y);
            hb = getHeight(x, y);
            hc = getHeight(x, y);
            hd = getHeight(x, y);

            return (Math.max(Math.max(ha, hb), Math.max(hb, hc)) + Math.min(Math.min(ha, hb), Math.min(hc, hd))) / 2.0f;
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

    public void randomize(int seed) {
        synchronized (this) {


            int n, m;
            int x, y;
            Random r = new Random(seed);

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
    }

    public void up(int x, int y) {
        synchronized (this) {
            setHeight(x, y, getHeight(x, y) + 1);
            conform(x, y);
        }
    }

    public void down(int x, int y) {
        synchronized (this) {
            setHeight(x, y, Math.max(getHeight(x, y) - 1, 0));
            conform(x, y);
        }
    }

    private void setHeight(int x, int y, int height) {
        if (x >= 0 && x < width && y >= 0 && y < breadth) {
            b.put(bufPos(x, y), height);
        }
    }

    private void conform(int x, int y) {
        conform(x, y, 1);
    }

    private void conform(int x, int y, int radius) {
        int ex, wy;
        int height = getHeight(x, y);
        boolean bChanged = false;
        for (ex = x - radius; ex <= x + radius; ex++) {
            for (wy = y - radius; wy <= y + radius; wy++) {
                if (ex >= 0 && ex <= width && wy >= 0 && wy <= breadth) {
                    if (getHeight(ex, wy) - height > radius) {
                        bChanged = true;
                        setHeight(ex, wy, height + radius);
                    } else if (height - getHeight(ex, wy) > radius) {
                        bChanged = true;
                        setHeight(ex, wy, height - radius);
                    }
                }
            }
        }
        if (bChanged) {
            conform(x, y, radius + 1);
        } else {
            markDirty(new Rectangle(x - radius, y - radius, radius * 2 + 1, radius * 2 + 1));
        }
    }

    public boolean isFlat(int x, int y) {
        int ha = 0, hb = 0, hc = 0, hd = 0;
        if (x < 0 || y < 0 || x + 1 >= width || y + 1 >= breadth) {
            return false;
        }
        ha = getHeight(x, y);
        hb = getHeight(x, y + 1);
        hc = getHeight(x + 1, y);
        hd = getHeight(x + 1, y + 1);
        return (ha == hb && hb == hc && hc == hd);
    }

    private void markDirty(int x, int y) {
        if (dirty == null) {
            dirty = new Rectangle(x, y);
        }
        if (x < dirty.x) {
            dirty.width += dirty.x - x;
            dirty.x = x;
        }
        if (x > dirty.x + dirty.width) {
            dirty.width = x - dirty.x;
        }
        if (y < dirty.y) {
            dirty.height += dirty.y - y;
            dirty.y = y;
        }
        if (y > dirty.height) {
            dirty.height = y - dirty.y;
        }
        dirty = dirty.intersection(bounds);
    }

    private void markDirty(Rectangle r) {
        if (dirty == null) {
            dirty = r;
        } else {
            dirty = dirty.union(r);
        }
        dirty = dirty.intersection(bounds);
    }

    public void sendUpdates(Collection<Player> players) {
        synchronized (this) {
            if (dirty != null) {
                HeightMapUpdate m = new HeightMapUpdate(dirty, b, width);

                for (Player p : players) {
                    p.sendMessage(m);
                }
            }
            dirty = null;
        }
    }
}