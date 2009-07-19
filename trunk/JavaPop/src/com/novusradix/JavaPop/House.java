package com.novusradix.JavaPop;

import java.awt.Point;

/**
 * The base class for attributes of a house that are common across the server and all clients
 * @author gef
 * 
 */
public abstract class House {

    /**
     * A unique ID
     */
    public int id;
    /**
     * The house's position on the map grid
     */
    public Point pos;
    /**
     * The development level of the house - 1 is smallest
     */
    public int level;
    /**
     * The owning player
     */
    public Player player;
    /**
     * The current strength of the house
     */
    public float strength;

    /**
     *
     * @return Whether or not the house currently suffer from the plague.
     */
    public abstract boolean isInfected();

    /**
     *
     * @return The per-second strength increase of the house.
     */
    public float getGrowthRate() {
        return level + 1;
    }

    /**
     * @return The maximum strength the house can contain - growing above this will spawn a new peon.
     */
    public float getMaxStrength() {
        return getGrowthRate() * 100;
    }
}
