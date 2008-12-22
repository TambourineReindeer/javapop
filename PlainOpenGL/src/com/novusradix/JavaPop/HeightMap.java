/*
 * Abstract super class for heightmaps in server, client and AI.
 * 
 */
package com.novusradix.JavaPop;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Dimension;
import java.awt.Point;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

/**
 *
 * @author gef
 */
public abstract class HeightMap {

    protected final int width,  breadth;

    public HeightMap(Dimension mapSize) {
        width = mapSize.width;
        breadth = mapSize.height;
    }

    public int getWidth() {
        return width;
    }

    public int getBreadth() {
        return breadth;
    }

    public boolean inBounds(Point p) {
        return (p.x >= 0 && p.y >= 0 && p.x < width && p.y < breadth);
    }

    public abstract byte getHeight(Point p);

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
        int ha = 0, hb = 0, hc = 0, hd = 0;
        if (p.x < 0 || p.y < 0 || p.x + 1 >= width || p.y + 1 >= breadth) {
            return false;
        }
        ha = getHeight(p);
        hb = getHeight(new Point(p.x, p.y + 1));
        hc = getHeight(new Point(p.x + 1, p.y));
        hd = getHeight(new Point(p.x + 1, p.y + 1));
        return (ha == hb && hb == hc && hc == hd);
    }

    public abstract void setTexture(Point p, byte t);

    protected abstract void setHeight(Point point, byte b);

    public void applyUpdate(HeightMapUpdate u) {
        throw new UnsupportedOperationException("Not implemented in superclass");
    }

    public void init(GLAutoDrawable glDrawable) {
        throw new UnsupportedOperationException("Not implemented in superclass");
    }

    public void display(GL gl, double time) {
        throw new UnsupportedOperationException("Not implemented in superclass");
    }
}
