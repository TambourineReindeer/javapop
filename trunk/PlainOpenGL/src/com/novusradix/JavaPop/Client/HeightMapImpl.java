package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author gef
 */
interface HeightMapImpl {

    void initialise(Dimension mapsize);
    void setHeight(Point p, byte height);
    byte getHeight(Point p);
    void applyUpdate(HeightMapUpdate u);
    void setTile(Point p, byte t);
}
