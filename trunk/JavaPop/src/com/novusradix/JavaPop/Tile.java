package com.novusradix.JavaPop;

/**
 *
 * @author gef
 */
public enum Tile {
//The numerical values must be continuous from zero in the order declared so that the values[] array can be used
    SEA((byte) 0, true, false, false, true),
    EMPTY_FLAT((byte) 1, false, false, true, false),
    EMPTY_SLOPE((byte) 2, false, true, false, false),
    BURNT((byte) 3, false, false, false, false),
    FARM((byte) 4, false, false, true, false),
    SWAMP((byte) 5, false, false, false, false),
    ROCK((byte) 6, false, true, false, true),
    TREE((byte) 7, false, false, true, false),
    BASALT((byte) 8, true, true, false, false),
    LAVA((byte) 9, false, true, false, false);
    public final byte id;
    public final boolean canExistAtSeaLevel;
    public final boolean canExistOnSlope;
    public final boolean isFertile;
    public final boolean isObstruction;
    public final static Tile[] tiles;
    
    static {
        tiles = values();
    }

    private Tile(byte id, boolean sealevel, boolean slope, boolean fertile, boolean obstruction) {
        this.id = id;
        canExistAtSeaLevel = sealevel;
        canExistOnSlope = slope;
        isFertile = fertile;
        isObstruction = obstruction;
    }
}
