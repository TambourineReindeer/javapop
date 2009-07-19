/*
 * Abstract super class for heightmaps in server, client and AI.
 * 
 */
package com.novusradix.JavaPop;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Set;

/**
 * Aspects of a heightmap that are common to client and server are defined here.
 * @author gef
 */
public abstract class HeightMap {

    /**
     * The x axis size of the map
     */
    public final int width;
    /**
     * The y axis size of the map
     */
    public final int breadth;

    public HeightMap(Dimension mapSize) {
        width = mapSize.width;
        breadth = mapSize.height;
    }

    //todo remove useless property methods
    public int getWidth() {
        return width;
    }

    public int getBreadth() {
        return breadth;
    }

    public boolean inBounds(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < breadth);
    }

    public boolean tileInBounds(int x, int y) {
        return (x >= 0 && y >= 0 && x < width - 1 && y < breadth - 1);
    }

    public abstract byte getHeight(int x, int y);

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
                return hb + (hd - hb) * x + (2.0f * hm - (hd + hb)) * (1 - y);
            } else {
                // AMB
                return ha + (hb - ha) * y + (2.0f * hm - (hb + ha)) * x;
            }
        } else {
            if (y > 1 - x) {
                // CMD
                return hc + (hd - hc) * y + (2.0f * hm - (hd + hc)) * (1 - x);
            } else {
                // AMC
                return ha + (hc - ha) * x + (2.0f * hm - (ha + hc)) * y;
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

    public boolean isSeaLevel(int x, int y) {
        if (x < 0 || y < 0 || x + 1 >= width || y + 1 >= breadth) {
            return false;
        }
        return (0 == getHeight(x, y) &&
                0 == getHeight(x, y + 1) &&
                0 == getHeight(x + 1, y) &&
                0 == getHeight(x + 1, y + 1));
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
        return (ha == hb && ha == hc && ha == hd);
    }

    //public abstract void setTile(Point p, Tile t);
    protected abstract void setHeight(int x, int y, byte b);

    //Optional
    public void applyUpdate(HeightMapUpdate u) {
        throw new UnsupportedOperationException("Not implemented in superclass");
    }

    public abstract void addRocks(Set<Point> newRocks);

    public abstract void removeRocks(Set<Point> deadRocks);
}
