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

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Rectangle dirtyRegion;
    public int[] heightData;

    public HeightMapUpdate(Rectangle dirty, IntBuffer b, int stride) {
        dirtyRegion = dirty;
        heightData = new int[dirty.width * dirty.height];

        for (int n = 0; n < dirty.height; n++) {
            b.position(dirty.x + (dirty.y+n) * stride);
            b.get(heightData, n*dirty.width, dirty.width);
        }
    }
    
    public void execute()
    {
        client.game.heightMap.applyUpdate(this);
    }
}
