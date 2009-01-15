/*
 * This class is the client side map. It specifies creation, response to server update messages, and rendering.
 * Other client classes can also query points on the map.
 */
package com.novusradix.JavaPop.Client.AI;

import java.awt.Dimension;


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
        int x, y;
        synchronized (this) {
            if (!u.dirtyRegion.isEmpty()) {

                for (y = 0; y < u.dirtyRegion.height; y++) {
                    for (x = 0; x < u.dirtyRegion.width; x++) {
                        setHeight(u.dirtyRegion.x + x, u.dirtyRegion.y + y, u.heightData[x + y * u.dirtyRegion.width]);
                    }
                }

            }
            for (Entry<Integer, Byte> e : u.texture.entrySet()) {
                setTile(e.getKey() % width, e.getKey() / width, e.getValue());
            }
        }
    }

    @Override
    public byte getHeight(int x, int y) {
        return heights[x][y];
    }

    @Override
    protected void setHeight(int x, int y, byte b) {
        heights[x][y] = b;
    }

    public void setTile(int x, int y, byte t) {
        tiles[x][y] = t;
    }
}