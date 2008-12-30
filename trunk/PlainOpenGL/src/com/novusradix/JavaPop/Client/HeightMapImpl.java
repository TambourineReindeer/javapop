package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import java.awt.Dimension;
import java.awt.Point;
import javax.media.opengl.*;

/**
 *
 * @author gef
 */
interface HeightMapImpl {

    void initialise(Dimension mapsize);
    void setHeight(Point p, byte height);
    byte getHeight(Point p);
    void init(final GLAutoDrawable glDrawable);
    void display(GL gl, double time);
    void applyUpdate(HeightMapUpdate u);
    void setTile(Point p, byte t);
}
