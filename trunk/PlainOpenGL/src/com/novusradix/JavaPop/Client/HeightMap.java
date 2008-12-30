/*
 * This class is the client side map. It specifies creation, response to server update messages, and rendering.
 * Other client classes can also query points on the map.
 */
package com.novusradix.JavaPop.Client;

import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Point;

public class HeightMap extends com.novusradix.JavaPop.HeightMap {

    HeightMapImpl implementation;

    public HeightMap(Dimension mapSize) {
        super(mapSize);

        implementation = new HeightMapNoShader();
        implementation.initialise(mapSize);
    }

    @Override
    public void display(GL gl, double time) {
        implementation.display(gl, time);
    }

    @Override
    public void init(final GLAutoDrawable glDrawable) {
        implementation.init(glDrawable);
    }

    @Override
    public void applyUpdate(HeightMapUpdate u) {
        implementation.applyUpdate(u);
    }

    @Override
    public byte getHeight(Point p) {
        return implementation.getHeight(p);
    }

    @Override
    protected void setHeight(Point point, byte b) {
        implementation.setHeight(point, b);
    }

    @Override
    public void setTile(Point p, byte t) {
        implementation.setTile(p, t);
    }
}