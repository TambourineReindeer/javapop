package com.novusradix.JavaPop;

/**
 *
 * @author gef
 */
public enum Tile {
//The numerical values must be continuous from zero in the order declared so that the values[] array can be used
    SEA(true, false, false, true),
    EMPTY_FLAT(false, false, true, false),
    EMPTY_SLOPE(false, true, false, false),
    BURNT(false, false, false, false),
    FARM(false, false, true, false),
    SWAMP(false, false, false, false),
    ROCK(false, true, false, true),
    TREE(false, false, true, false),
    BASALT(true, true, false, false),
    LAVA(false, true, false, false),
    EARTHQUAKE(false, false, false, false);
    public final byte id;
    public final boolean canExistAtSeaLevel;
    public final boolean canExistOnSlope;
    public final boolean isFertile;
    public final boolean isObstruction;
    public final static Tile[] tiles;
    

    static {
        tiles = values();
    }

    private Tile(boolean sealevel, boolean slope, boolean fertile, boolean obstruction) {
        this.id = (byte) ordinal();
        canExistAtSeaLevel = sealevel;
        canExistOnSlope = slope;
        isFertile = fertile;
        isObstruction = obstruction;
    }
}
