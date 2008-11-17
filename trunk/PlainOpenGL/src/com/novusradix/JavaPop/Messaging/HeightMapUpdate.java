/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import java.awt.Rectangle;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author mom
 */
public class HeightMapUpdate implements Serializable {

    public Rectangle dirtyRegion;
    public int heightData[];

    public HeightMapUpdate(int x, int y, int width, int breadth, IntBuffer b) {
        dirtyRegion = new Rectangle(x, y, width, breadth);
        heightData = new int[width * breadth];

        for (int n = 0; n < breadth; n++) {
            b.position(x + n * breadth);
            b.get(heightData, 0, width);
        }
    }
}
