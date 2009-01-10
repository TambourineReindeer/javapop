package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Tile;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

/**
 *
 * @author gef
 */
public class HeightMap extends com.novusradix.JavaPop.HeightMap {

    //private ByteBuffer b;
    private byte[] heights;
    private static int rowstride;
    private Rectangle dirty;
    public final Rectangle bounds;
    private Map<Point, Byte> textureChanges;
    private byte[][] tex;
    private byte[][] oldTex;
    private boolean[][] flat;

    public HeightMap(Dimension mapSize) {
        super(mapSize);
        //b = BufferUtil.newByteBuffer(width * breadth);
        heights = new byte[width * breadth];
        textureChanges = new HashMap<Point, Byte>();
        rowstride = width;
        bounds = new Rectangle(0, 0, width, breadth);
        tex = new byte[width - 1][breadth - 1];
        oldTex = new byte[width - 1][breadth - 1];

        flat = new boolean[width - 1][breadth - 1];
        int x, y;
        for (y = 0; y < breadth; y++) {
            for (x = 0; x < width; x++) {
                //b.put((byte) 0);
                if (x < width - 1 && y < breadth - 1) {
                    flat[x][y] = true;
                }
            }
        }
    //b.flip();
    }

    private static int bufPos(Point p) {
        return p.y * rowstride + p.x;
    }

    public byte getHeight(Point p) {
        if (inBounds(p)) {
            //return b.get(bufPos(p));
            return heights[bufPos(p)];
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

    public void setTile(Point p, Tile t) {
        if (tileInBounds(p)) {
            boolean bflat = isFlat(p);
            boolean bseaLevel = isSeaLevel(p);
            if (!bflat && !t.canExistOnSlope) {
                return;
            }
            if (bseaLevel && !t.canExistAtSeaLevel) {
                return;
            }
            tex[p.x][p.y] = t.id;
        }
    }

    public void clearTile(Point p) {
        boolean bflat = isFlat(p);
        boolean bseaLevel = isSeaLevel(p);
        if (bseaLevel) {
            tex[p.x][p.y] = Tile.SEA.id;

            return;
        }
        if (bflat) {
            tex[p.x][p.y] = Tile.EMPTY_FLAT.id;
        } else {
            tex[p.x][p.y] = Tile.EMPTY_SLOPE.id;
        }
    }

    public Tile getTile(Point p) {
        return Tile.values()[tex[p.x][p.y]];
    }

    private void difTex() {
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < breadth - 1; y++) {
                if (tex[x][y] != oldTex[x][y]) {
                    textureChanges.put(new Point(x, y), tex[x][y]);
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
                    Tile oldTile = getTile(p2);
                    if (isSeaLevel(p2)) {
                        if (!oldTile.canExistAtSeaLevel) {
                            setTile(p2, Tile.SEA);
                        }
                    } else {
                        if (isFlat(p2)) {
                            if (oldTile == Tile.EMPTY_SLOPE || oldTile == Tile.SEA) {
                                setTile(p2, Tile.EMPTY_FLAT);
                            }
                        } else {
                            if (!oldTile.canExistOnSlope) {
                                setTile(p2, Tile.EMPTY_SLOPE);
                            }
                        }
                    }
                }
            }
        }
    }

    protected void setHeight(Point p, byte height) {
        if (inBounds(p)) {
            //b.put(bufPos(p), height);
            heights[bufPos(p)] = height;
        }

    }

    private void conform(Point p) {
        byte height = getHeight(p);
        boolean bChanged = false;
        Point p1;

        int r;
        for (r = 1; r <
                64; r++) {
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
                reFlat(p, r);
                retexture(p, r);
                return;

            }




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
            if (dirty != null || textureChanges.size() > 0) {
                m = new HeightMapUpdate(dirty, heights, width, textureChanges);
            }

            dirty = null;
            textureChanges.clear();
        }

        return m;
    }
}
