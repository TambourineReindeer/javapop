/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import java.awt.Rectangle;
import java.io.Serializable;
import java.nio.IntBuffer;

/**
 *
 * @author mom
 */
public class HeightMapUpdate extends Message implements Serializable {

    public Rectangle dirtyRegion;
    public int[] heightData;

    public HeightMapUpdate(Rectangle dirty, IntBuffer b) {
        dirtyRegion = dirty;
        heightData = new int[dirty.width * dirty.height];

        for (int n = 0; n < dirty.height; n++) {
            b.position(dirty.x + n * dirty.height);
            b.get(heightData, 0, dirty.width);
        }
    }
    
    public void execute()
    {
        client.game.h.applyUpdate(this);
    }
}
