package com.novusradix.JavaPop;

import java.awt.Point;

/**
 *
 * @author gef
 */
public abstract class House {

    public int id;
    public Point pos;
    public int level;
    public Player player;
    public float strength;

    public float getGrowthRate()
    {
        return level+1;
    }

    public float getMaxStrength()
    {
        return getGrowthRate()*100;
    }
    
}
