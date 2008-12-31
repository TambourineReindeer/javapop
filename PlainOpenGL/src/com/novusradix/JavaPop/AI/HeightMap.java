/*
 * This class is the client side map. It specifies creation, response to server update messages, and rendering.
 * Other client classes can also query points on the map.
 */
package com.novusradix.JavaPop.AI;

import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Point;
import java.util.Map.Entry;

public class HeightMap extends com.novusradix.JavaPop.HeightMap {

    byte[][] heights;
    byte[][] tiles;

    public HeightMap(Dimension mapSize) {
        super(mapSize);
        heights = new byte[mapSize.width][mapSize.height];
        tiles = new byte[mapSize.width - 1][mapSize.height - 1];
    }

    @Override
    public void applyUpdate(HeightMapUpdate u) {
         synchronized (this) {
            if (!u.dirtyRegion.isEmpty()) {
                int x,    y;

                for (y = 0; y < u.dirtyRegion.height; y++) {
                    for (x = 0; x < u.dirtyRegion.width; x++) {
                        setHeight(new Point(u.dirtyRegion.x + x, u.dirtyRegion.y + y), u.heightData[x + y * u.dirtyRegion.width]);
                    }
                }
                
            }
            for (Entry<Point, Byte> e : u.texture.entrySet()) {
                setTile(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public byte getHeight(Point p) {
        return heights[p.x][p.y];
    }

    @Override
    protected void setHeight(Point p, byte b) {
        heights[p.x][p.y] = b;
    }

    @Override
    public void setTile(Point p, byte t) {
        tiles[p.x][p.y] = t;
    }
}