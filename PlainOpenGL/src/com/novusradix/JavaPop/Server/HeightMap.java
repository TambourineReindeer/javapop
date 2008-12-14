/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.util.Random;
import com.sun.opengl.util.BufferUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
/**
 *
 * @author mom
 */
public class HeightMap {

    private final int width,  breadth;
    private ByteBuffer b;
    private static int rowstride;
    private Rectangle dirty;
    public final Rectangle bounds;
    private Map<Point, Integer> texture;
    private int[][] tex;
    private int[][] oldTex;

    public HeightMap(int width, int breadth) {
        this.breadth = breadth;
        this.width = width;
        b = BufferUtil.newByteBuffer(width * breadth);
        texture = new HashMap<Point, Integer>();
        rowstride = width;
        bounds = new Rectangle(0, 0, width, breadth);
        tex = new int[width][breadth];
        oldTex = new int[width][breadth];
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

    private static int bufPos(Point p) {
        return p.y * rowstride + p.x;
    }

    public boolean inBounds(Point p) {
        return (p.x >= 0 && p.y >= 0 && p.x < width && p.y < breadth);
    }

    public byte getHeight(Point p) {
        if (inBounds(p)) {
            return b.get(bufPos(p));
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

        ha = getHeight(new Point(x1, y1));
        hb = getHeight(new Point(x1, y2));
        hc = getHeight(new Point(x2, y1));
        hd = getHeight(new Point(x2, y2));
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

    /*public Vector2 getSlope(float x, float y) {
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
    }*/
    void randomize(int seed) {
        synchronized (this) {
            int n, m;
            int x, y;
            Random r = new Random(seed);

            for (n = 0; n < 100; n++) {
                x = r.nextInt(width);
                y = r.nextInt(breadth);
                for (m = 0; m < r.nextInt(8); m++) {
                    up(new Point(x, y));
                }
                for (m = 0; m < r.nextInt(8); m++) {
                    up(new Point(x - 5 + r.nextInt(10), y - 5 + r.nextInt(10)));
                }
                for (m = 0; m < r.nextInt(2); m++) {
                    down(new Point(x - 5 + r.nextInt(10), -5 + r.nextInt(10)));
                }
                for (m = 0; m < r.nextInt(2); m++) {
                    down(new Point(x, y));
                }
            }
        }
    }

    public void up(Point p) {
        synchronized (this) {
            setHeight(p, (byte) (getHeight(p) + 1));
            conform(p);
        }
    }

    public void down(Point p) {
        synchronized (this) {
            setHeight(p, (byte) (max(getHeight(p) - 1, 0)));
            conform(p);
        }
    }

    public void setTexture(Point p, int i) {
        tex[p.x][p.y] = i;


    }

    private void difTex() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < breadth; y++) {
                if (tex[x][y] != oldTex[x][y]) {
                    texture.put(new Point(x, y), tex[x][y]);
                    oldTex[x][y] = tex[x][y];
                }
            }
        }
    }

    private void retexture(Point p, int r) {
        for (int ex = p.x - r; ex < p.x + r; ex++) {
            for (int wy = p.y - r; wy < p.y + r; wy++) {
                if (ex >= 0 && wy >= 0 && ex + 1 < width && wy + 1 < breadth) {
                    Point p2 = new Point(ex, wy);
                    if (isSea(p2)) {
                        setTexture(p2, 0);
                    } else {
                        setTexture(p2, 1);
                    }
                }
            }
        }
    }

    private void setHeight(Point p, byte height) {
        if (inBounds(p)) {
            b.put(bufPos(p), height);
        }
    }

    private void conform(Point p) {
        byte height = getHeight(p);
        boolean bChanged = false;
        Point p1;
        int r;
        for (r = 1; r < 64; r++) {
            bChanged = false;
            for (Point offset : Helpers.rings[r]) {
                p1 = new Point(p.x + offset.x, p.y + offset.y);
                if (bounds.contains(p1)) {
                    if (getHeight(p1) - height > r) {
                        bChanged = true;
                        setHeight(p1, (byte) (height + r));
                    } else if (height - getHeight(p1) > r) {
                        bChanged = true;
                        setHeight(p1, (byte) (height - r));
                    }
                }
            }

            if (!bChanged) {
                markDirty(new Rectangle(p.x - r + 1, p.y - r + 1, r * 2 - 1, r * 2 - 1));
                retexture(p, r);
                return;
            }
        }
    }

    public boolean isSea(Point p) {
        if (p.x < 0 || p.y < 0 || p.x + 1 >= width || p.y + 1 >= breadth) {
            return false;
        }
        return (0 == getHeight(p) &&
                0 == getHeight(new Point(p.x, p.y + 1)) &&
                0 == getHeight(new Point(p.x + 1, p.y)) &&
                0 == getHeight(new Point(p.x + 1, p.y + 1)));
    }

    public boolean isFlat(Point p) {
        int ha = 0,  hb = 0,  hc = 0, hd = 0;
        if (p.x < 0 || p.y < 0 || p.x + 1 >= width || p.y + 1 >= breadth) {
            return false;
        }
        ha = getHeight(p);
        hb = getHeight(new Point(p.x, p.y + 1));
        hc = getHeight(new Point(p.x + 1, p.y));
        hd = getHeight(new Point(p.x + 1, p.y + 1));
        return (ha == hb && hb == hc && hc == hd);
    }

    private void markDirty(Point p) {
        if (dirty == null) {
            dirty = new Rectangle(p);
        }
        if (p.x < dirty.x) {
            dirty.width += dirty.x - p.x;
            dirty.x = p.x;
        }
        if (p.x > dirty.x + dirty.width) {
            dirty.width = p.x - dirty.x;
        }
        if (p.y < dirty.y) {
            dirty.height += dirty.y - p.y;
            dirty.y = p.y;
        }
        if (p.y > dirty.height) {
            dirty.height = p.y - dirty.y;
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
        difTex();
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
