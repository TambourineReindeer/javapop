/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author mom
 */
public class HeightMapUpdate extends Message implements Externalizable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Rectangle dirtyRegion;
    public byte[] heightData;
    public Map<Point, Integer> texture;

    public HeightMapUpdate() {
    }

    public HeightMapUpdate(Rectangle dirty, ByteBuffer b, int stride, Map<Point, Integer> texture) {
        dirtyRegion = dirty;
        if (dirtyRegion != null) {
            heightData = new byte[dirty.width * dirty.height];
            for (int n = 0; n < dirty.height; n++) {
                b.position(dirty.x + (dirty.y + n) * stride);
                b.get(heightData, n * dirty.width, dirty.width);
            }
        }
        this.texture = new HashMap<Point, Integer>(texture);
    }

    public void execute() {
        clientMap.applyUpdate(this);
    }

    public void writeExternal(ObjectOutput o) throws IOException {
        o.writeInt(dirtyRegion.x);
        o.writeInt(dirtyRegion.y);
        o.writeInt(dirtyRegion.width);
        o.writeInt(dirtyRegion.height);
        o.writeInt(heightData.length);
        o.write(heightData);
        o.writeInt(texture.size());
        for (Entry<Point, Integer> e : texture.entrySet()) {
            o.writeInt(e.getKey().x);
            o.writeInt(e.getKey().y);
            o.writeInt(e.getValue());
        }

    }

    public void readExternal(ObjectInput i) throws IOException, ClassNotFoundException {
        dirtyRegion = new Rectangle();
        dirtyRegion.x = i.readInt();
        dirtyRegion.y = i.readInt();
        dirtyRegion.width = i.readInt();
        dirtyRegion.height = i.readInt();


        heightData = new byte[i.readInt()];
        i.read(heightData, 0, heightData.length);
        int texcount = i.readInt();
        texture = new HashMap<Point, Integer>();
        
        int x,y,n;
        for(;texcount>0;texcount--)
        {
            x = i.readInt();
            y=i.readInt();
            n = i.readInt();
            texture.put(new Point(x,y), n);
        }

    }
}
