/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.util.Random;
import com.sun.opengl.util.BufferUtil;
import java.awt.Dimension;
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
public class HeightMap extends com.novusradix.JavaPop.HeightMap {

    private ByteBuffer b;
    private static int rowstride;
    private Rectangle dirty;
    public final Rectangle bounds;
    private Map<Point, Byte> texture;
    private byte[][] tex;
    private byte[][] oldTex;
    private boolean[][] flat;

    public HeightMap(Dimension mapSize) {
        super(mapSize);
        b = BufferUtil.newByteBuffer(width * breadth);
        texture = new HashMap<Point, Byte>();
        rowstride = width;
        bounds = new Rectangle(0, 0, width, breadth);
        tex = new byte[width][breadth];
        oldTex = new byte[width][breadth];
        flat = new boolean[width][breadth];
        int x, y;
        for (y = 0; y < breadth; y++) {
            for (x = 0; x < width; x++) {
                b.put((byte) 0);
                flat[x][y] = true;
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

    private static int bufPos(Point p) {
        return p.y * rowstride + p.x;
    }

    public byte getHeight(Point p) {
        if (inBounds(p)) {
            return b.get(bufPos(p));
        }
        return 0;
    }

    void randomize(int seed) {
        synchronized (this) {
            int n, m;
            int x, y;
            Random r = new Random(seed);

            for (n = 0; n < width * breadth / 80; n++) {
                x = r.nextInt(width);
                y = r.nextInt(breadth);
                for (m = 0; m < r.nextInt(8); m++) {
                    up(new Point(x, y));
                }
                for (m = 0; m < r.nextInt(8); m++) {
                    up(new Point(x - 5 + r.nextInt(10), y - 5 + r.nextInt(10)));
                }
                for (m = 0; m < r.nextInt(8); m++) {
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

    public void setTile(Point p, byte i) {
        tex[p.x][p.y] = i;
    }

    public int getTexture(Point p) {
        return tex[p.x][p.y];
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

    @Override
    public boolean isFlat(Point p) {
        return flat[p.x][p.y];
    }

    private void reFlat(Point p, int r) {
        for (int ex = p.x - r; ex < p.x + r; ex++) {
            for (int wy = p.y - r; wy < p.y + r; wy++) {
                if (ex >= 0 && wy >= 0 && ex + 1 < width && wy + 1 < breadth) {
                    Point p2 = new Point(ex, wy);
                    flat[ex][wy] = super.isFlat(p2);
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
                        setTile(p2, (byte) 0);
                    } else {
                        setTile(p2, (byte) 1);
                    }
                }
            }
        }
    }

    protected void setHeight(Point p, byte height) {
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
                reFlat(p, r);
                return;
            }
        }
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
        if (dirty.isEmpty()) {
            dirty = null;
        }
    }

    private void markDirty(Rectangle r) {
        if (dirty == null) {
            dirty = r;
        } else {
            dirty = dirty.union(r);
        }
        dirty = dirty.intersection(bounds);
        if (dirty.isEmpty()) {
            dirty = null;
        }
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
