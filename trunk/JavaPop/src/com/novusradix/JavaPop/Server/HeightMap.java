package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.*;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Messaging.RockUpdate;
import com.novusradix.JavaPop.Tile;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import static java.lang.Math.*;

/**
 *
 * @author gef
 */
public class HeightMap extends com.novusradix.JavaPop.HeightMap {

    //private ByteBuffer b;
    private final byte[] heights;
    private final int rowstride;
    private Rectangle dirty;
    public final Rectangle bounds;
    private final Map<Integer, Byte> textureChanges;
    private final byte[][] tex;
    private final byte[][] oldTex;
    private final boolean[][] flat;
    private final SortedSet<Integer> houseChanges;
    private final Set<Point> staticRocks;
    private final Map<Point, MutableFloat> dynamicRocks;
    private final Set<Point> newRocks;

    public HeightMap(Dimension mapSize) {
        super(mapSize);
        rowstride = width;
        staticRocks = new HashSet<Point>();
        newRocks = new HashSet<Point>();
        dynamicRocks = new HashMap<Point, MutableFloat>();
        houseChanges = new TreeSet<Integer>();

        heights = new byte[width * breadth];
        textureChanges = new HashMap<Integer, Byte>();
        bounds = new Rectangle(0, 0, width, breadth);
        tex = new byte[width - 1][breadth - 1];
        oldTex = new byte[width - 1][breadth - 1];

        flat = new boolean[width - 1][breadth - 1];
        int x, y;
        for (y = 0; y < breadth; y++) {
            for (x = 0; x < width; x++) {
                if (x < width - 1 && y < breadth - 1) {
                    flat[x][y] = true;
                }
            }
        }
    }

    private int bufPos(int x, int y) {
        return y * rowstride + x;
    }

    public byte getHeight(int x, int y) {
        if (inBounds(x, y)) {
            //return b.get(bufPos(p));
            return heights[bufPos(x, y)];
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
                int ups = r.nextInt(8);
                for (m = 0; m < ups; m++) {
                    up(x, y);
                }
                for (m = 0; m < r.nextInt(8); m++) {
                    up(x - 5 + r.nextInt(10), y - 5 + r.nextInt(10));
                }
                for (m = 0; m < r.nextInt(ups * 2 + 1); m++) {
                    down(x - ups + r.nextInt(ups * 2 + 1), y - ups + r.nextInt(ups * 2 + 1));
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
            setHeight(x, y, (byte) (max(getHeight(x, y) - 1, 0)));
            conform(x, y);
        }
    }

    public SortedSet<Integer> takeHouseChanges() {
        synchronized (houseChanges) {
            SortedSet<Integer> temp ;
            temp = new TreeSet<Integer>(houseChanges);
            houseChanges.clear();
            return temp;
        }
    }

    public void setTile(int x, int y, Tile t) {
        if (tileInBounds(x, y)) {
            Tile oldTile = Tile.tiles[tex[x][y]];
            if (t == oldTile) {
                return;
            }
            boolean bflat = isFlat(x, y);
            boolean bseaLevel = isSeaLevel(x, y);
            if (!bflat && !t.canExistOnSlope) {
                return;
            }
            if (bseaLevel && !t.canExistAtSeaLevel) {
                return;
            }

            Point p = new Point(x, y);
            if (!bseaLevel) {
                if (staticRocks.contains(p) || dynamicRocks.containsKey(p)) { //Rock beats scissors          
                    t = Tile.ROCK;
                }
            }
            if (t.isFertile != oldTile.isFertile) {
                synchronized (houseChanges) {
                    houseChanges.add(x + y * width);
                }
            }
            if (oldTile == Tile.ROCK) {
                if (!dynamicRocks.containsKey(p)) {
                    dynamicRocks.put(p, new MutableFloat(1.0f));
                }
                staticRocks.remove(p);
            }
            if (t == Tile.ROCK) {
                if (!dynamicRocks.containsKey(p)) {
                    dynamicRocks.put(p, new MutableFloat(0.0f));
                    newRocks.add(p);
                }
                staticRocks.remove(p);
            }
            tex[x][y] = t.id;
        }
    }

    public void clearTile(int x, int y) {
        boolean bflat = isFlat(x, y);
        boolean bseaLevel = isSeaLevel(x, y);
        Tile t;
        if (bseaLevel) {
            t = Tile.SEA;
            return;
        }
        if (bflat) {
            t = Tile.EMPTY_FLAT;
        } else {
            t = Tile.EMPTY_SLOPE;
        }
        setTile(x, y, t);
    }

    public Tile getTile(int x, int y) {
        return Tile.tiles[tex[x][y]];
    }

    private void difTex() {
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < breadth - 1; y++) {
                if (tex[x][y] != oldTex[x][y]) {
                    textureChanges.put(x + y * width, tex[x][y]);
                    oldTex[x][y] = tex[x][y];
                }
            }
        }
    }

    @Override
    public boolean isFlat(int x, int y) {
        return flat[x][y];
    }

    private void reFlat(int x, int y, int r) {
        for (int ex = x - r; ex < x + r; ex++) {
            for (int wy = y - r; wy < y + r; wy++) {
                if (ex >= 0 && wy >= 0 && ex + 1 < width && wy + 1 < breadth) {
                    flat[ex][wy] = super.isFlat(ex, wy);
                }
            }
        }
    }

    private void retexture(int x, int y, int r) {
        for (int ex = x - r; ex < x + r; ex++) {
            for (int wy = y - r; wy < y + r; wy++) {
                if (ex >= 0 && wy >= 0 && ex + 1 < width && wy + 1 < breadth) {
                    Tile oldTile = getTile(ex, wy);
                    if (isSeaLevel(ex, wy)) {
                        if (!oldTile.canExistAtSeaLevel) {
                            setTile(ex, wy, Tile.SEA);
                        }
                    } else {
                        if (isFlat(ex, wy)) {
                            if (oldTile == Tile.EMPTY_SLOPE || oldTile == Tile.SEA) {
                                setTile(ex, wy, Tile.EMPTY_FLAT);
                            }
                        } else {
                            if (!oldTile.canExistOnSlope) {
                                setTile(ex, wy, Tile.EMPTY_SLOPE);
                            }
                        }
                    }
                }
            }
        }
    }

    protected void setHeight(int x, int y, byte height) {
        if (inBounds(x, y)) {
            //b.put(bufPos(p), height);
            heights[bufPos(x, y)] = height;
        }

    }

    private void conform(int x, int y) {
        byte height = getHeight(x, y);
        boolean bChanged = false;
        int px, py;

        int r;
        for (r = 1; r < 64; r++) {
            bChanged = false;
            for (Point offset : Helpers.rings[r]) {
                px = x + offset.x;
                py = y + offset.y;
                if (bounds.contains(px, py)) {
                    if (getHeight(px, py) - height > r) {
                        bChanged = true;
                        setHeight(px, py, (byte) (height + r));
                    } else if (height - getHeight(px, py) > r) {
                        bChanged = true;
                        setHeight(px, py, (byte) (height - r));
                    }

                }
            }

            if (!bChanged) {
                markDirty(new Rectangle(x - r + 1, y - r + 1, r * 2 - 1, r * 2 - 1));
                reFlat(x, y, r);
                retexture(x, y, r);
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

    public RockUpdate updateRocks(float timeElapsed) {
        Set<Point> deadRocks;
        deadRocks = new HashSet<Point>();
        Iterator<Entry<Point, MutableFloat>> i;
        Entry<Point, MutableFloat> e;
        Point p;
        MutableFloat f;
        boolean sea;
        i = dynamicRocks.entrySet().iterator();
        while (i.hasNext()) {
            e = i.next();
            p = e.getKey();
            f = e.getValue();
            sea = isSeaLevel(p.x, p.y);
            if (sea) {
                f.f -= timeElapsed / 2.0f;
                if (f.f < 0.0f) {
                    deadRocks.add(p);
                    i.remove();
                }
            } else {
                f.f += timeElapsed / 2.0f;
                if (f.f > 1.0f) {
                    staticRocks.add(p);
                    i.remove();
                }
            }
        }
        RockUpdate ru = null;
        if (deadRocks.size() + newRocks.size() > 0) {
            ru = new RockUpdate(newRocks, deadRocks);
        }
        newRocks.clear();
        return ru;
    }

    @Override
    public void addRocks(Set<Point> newRocks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeRocks(Set<Point> deadRocks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
