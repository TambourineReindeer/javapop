package com.novusradix.JavaPop;

import java.awt.Point;

/**
 * Aspects of a player that are common to the server and all clients are defined here
 * @author gef
 */
public interface Player {

    public String getName();
    public float[] getColour();
    public Point getPapalMagnet();
    public double getMana();
}
