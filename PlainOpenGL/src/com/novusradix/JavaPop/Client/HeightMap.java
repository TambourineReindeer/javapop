/*
 * This class is the client side map. It specifies creation, response to server update messages, and rendering.
 * Other client classes can also query points on the map.
 */
package com.novusradix.JavaPop.Client;

import java.awt.Dimension;

import javax.media.opengl.GL;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Point;

public class HeightMap extends com.novusradix.JavaPop.HeightMap implements GLObject {

    HeightMapImpl implementation;
    GLObject renderer;
    public HeightMap(Dimension mapSize) {
        super(mapSize);

        HeightMapNoShader h = new HeightMapNoShader();
        implementation = h;
        renderer = h;
        implementation.initialise(mapSize);
    }

    public void display(GL gl, float time) {
        renderer.display(gl, time);
    }

    public void init(GL gl) {
        renderer.init(gl);
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