package com.novusradix.JavaPop;

/**
 *
 * @author gef
 */
public enum Tile {
//The numerical values must be continuous from zero in the order declared so that the values[] array can be used
    SEA((byte) 0, true, false, false),
    EMPTY_FLAT((byte) 1, false, false, true),
    EMPTY_SLOPE((byte) 2, false, true, false),
    BURNT((byte) 3, false, false, false),
    FARM((byte) 4, false, false, true),
    SWAMP((byte) 5, false, false, false),
    ROCK((byte) 6, false, true, false),
    TREE((byte) 7, false, false, true),
    BASALT((byte) 8, true, true, false),
    LAVA((byte) 9, false, true, false);
    
    public final byte id;
    public final boolean canExistAtSeaLevel;
    public final boolean canExistOnSlope;
    public final boolean isFertile;

    private Tile(byte id, boolean sealevel, boolean slope, boolean fertile) {
        this.id = id;
        canExistAtSeaLevel = sealevel;
        canExistOnSlope = slope;
        isFertile = fertile;
    }
}
