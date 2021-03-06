package com.novusradix.JavaPop.Messaging;

import java.awt.Rectangle;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author gef
 */
public class HeightMapUpdate extends Message implements Externalizable {


    private static final long serialVersionUID = 1L;
    public Rectangle dirtyRegion;
    public byte[] heightData;
    public Map<Integer, Byte> texture;

    public HeightMapUpdate() {
    }

    public HeightMapUpdate(Rectangle dirty, byte[] b, int stride, Map<Integer, Byte> texture) {
        dirtyRegion = dirty;
        if (dirtyRegion != null) {
            heightData = new byte[dirty.width * dirty.height];
            for (int n = 0; n < dirty.height; n++) {
                //b.position(dirty.x + (dirty.y + n) * stride);
                //b.get(heightData, n * dirty.width, dirty.width);
                System.arraycopy(b, dirty.x+(dirty.y+n)*stride, heightData, n*dirty.width, dirty.width);
            }

        } else {
            dirtyRegion = new Rectangle();
        }
        this.texture = new HashMap<Integer, Byte>(texture);
    }

    public void execute() {
        clientMap.applyUpdate(this);
    }

    public void writeExternal(ObjectOutput o) throws IOException {
        o.writeInt(dirtyRegion.x);
        o.writeInt(dirtyRegion.y);
        o.writeInt(dirtyRegion.width);
        o.writeInt(dirtyRegion.height);
        if (heightData != null) {
            o.writeInt(heightData.length);
            o.write(heightData, 0, heightData.length);
        } else {
            o.writeInt(0);
        }
        o.writeInt(texture.size());
        for (Entry<Integer, Byte> e : texture.entrySet()) {
            o.writeInt(e.getKey());
            o.writeByte(e.getValue());
        }
    }

    public void readExternal(ObjectInput i) throws IOException, ClassNotFoundException {
        dirtyRegion = new Rectangle();
        dirtyRegion.x = i.readInt();
        dirtyRegion.y = i.readInt();
        dirtyRegion.width = i.readInt();
        dirtyRegion.height = i.readInt();

        int heightBytes = i.readInt();
        int readBytes = 0;
        heightData = new byte[heightBytes];
        do {
            readBytes += i.read(heightData, readBytes, heightBytes);
            heightBytes = heightData.length - readBytes;
        } while (heightBytes > 0);

        int texcount = i.readInt();
        texture = new HashMap<Integer, Byte>();

        int x;
        byte n;
        for (; texcount > 0; texcount--) {
            x = i.readInt();
            n = i.readByte();
            texture.put(x, n);
        }
    }
}
