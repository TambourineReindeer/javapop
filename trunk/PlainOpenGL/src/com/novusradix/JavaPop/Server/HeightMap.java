/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.util.Random;
import com.sun.opengl.util.BufferUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mom
 */
public class HeightMap {

    private final int width,  breadth;
    private ByteBuffer b;
//    private IntBuffer b;
    private static int rowstride;
    private static int VZ = 0;
    private Rectangle dirty;
    private Rectangle bounds;
    private Map<Point, Integer> texture;

    public HeightMap(int width, int breadth) {
        this.breadth = breadth;
        this.width = width;
        b = BufferUtil.newByteBuffer(width * breadth);
        texture = new HashMap<Point, Integer>();

        rowstride = width;
        bounds = new Rectangle(0, 0, width, breadth);
        int x, y;
        for (y = 0; y < breadth; y++) {
            for (x = 0; x < width; x++) {
                b.put((byte) 0);
            }
        }
        b.flip();
    }

    public byte[] getData() {
        byte[] buf = new byte[width * breadth];
        getData(buf);
        return buf;
    }

    public void getData(byte[] buf) {
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

    public boolean inBounds(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < breadth);
    }

    public byte getHeight(int x, int y) {
        if (inBounds(x, y)) {
            return b.get(bufPos(x, y, 0, VZ));
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

    public Vector2 getSlope(float x, float y) {
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
            return new Vector2(0, 0);
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
                return new Vector2(hd - hb, 2.0f * ((hd + hb) / 2.0f - hm));
            } else {
                // AMB
                return new Vector2(2.0f * (hm - (ha + hb) / 2.0f), hb - ha);
            }
        } else {
            if (y > 1 - x) {
                // CMD
                return new Vector2(2.0f * ((hc + hd) / 2.0f - hm), hd - hc);
            } else {
                // AMC
                return new Vector2(hc - ha, 2.0f * (hm - (ha + hc) / 2.0f));
            }
        }
    }

    void randomize(int seed) {
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
            setHeight(x, y, (byte) (getHeight(x, y) + 1));
            conform(x, y);
        }
    }

    public void down(int x, int y) {
        synchronized (this) {
            setHeight(x, y, (byte) (Math.max(getHeight(x, y) - 1, 0)));
            conform(x, y);
        }
    }

    public void setTexture(Point p, int i) {
        synchronized (this) {
            texture.put(p, i);
        }
    }

    private void setHeight(int x, int y, byte height) {
        if (x >= 0 && x < width && y >= 0 && y < breadth) {
            b.put(bufPos(x, y), height);
        }
    }

    private void conform(int x, int y) {
        conform(x, y, (byte) 1);
    }

    private void conform(int x, int y, byte r) {
        int ex, wy;
        byte height = getHeight(x, y);
        boolean bChanged = false;
        for (ex = x - r; ex <= x + r; ex++) {
            for (wy = y - r; wy <= y + r; wy++) {
                if (ex >= 0 && ex <= width && wy >= 0 && wy <= breadth) {
                    if (getHeight(ex, wy) - height > r) {
                        bChanged = true;
                        setHeight(ex, wy, (byte) (height + r));
                    } else if (height - getHeight(ex, wy) > r) {
                        bChanged = true;
                        setHeight(ex, wy, (byte) (height - r));
                    }
                }
            }
        }
        if (bChanged) {
            conform(x, y, (byte) (r + 1));
        } else {
            markDirty(new Rectangle(x - r, y - r, r * 2 + 1, r * 2 + 1));
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

    public HeightMapUpdate GetUpdate() {
        HeightMapUpdate m = null;
        synchronized (this) {
            if (dirty != null || texture.size() > 0) {
                m = new HeightMapUpdate(dirty, b, width, texture);
            }
            dirty = null;
            texture.clear();
        }
        return m;
    }
}