package com.novusradix.JavaPop;

import java.awt.Point;

/**
 *
 * @author gef
 */
public interface Player {

    public String getName();
    public float[] getColour();
    public Point getPapalMagnet();
    //public void setPapalMagnet(Point p);
    public double getMana();
}
