package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;

/**
 *
 * @author gef
 */
interface HeightMapImpl{

    void initialise(HeightMap h);
    void setHeight(int x, int y, byte height);
    byte getHeight(int x, int y);
    void applyUpdate(HeightMapUpdate u);
    void setTile(int x, int y, byte t);
    
}
