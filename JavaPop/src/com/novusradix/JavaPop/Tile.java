package com.novusradix.JavaPop;

import com.novusradix.JavaPop.Server.ServerPeons.Action;

/**
 *
 * @author gef
 */
public enum Tile {
//The numerical values must be continuous from zero in the order declared so that the values[] array can be used
    SEA(true, false, false, true, Action.DROWN),
    EMPTY_FLAT(false, false, true, false, Action.NONE),
    EMPTY_SLOPE(false, true, false, false, Action.NONE),
    BURNT(false, false, false, false, Action.NONE),
    FARM(false, false, true, false, Action.NONE),
    SWAMP(false, false, false, false, Action.FALL),
    ROCK(false, true, false, true, Action.NONE),
    TREE(false, false, true, false, Action.NONE),
    BASALT(true, true, false, false, Action.NONE),
    LAVA(false, true, false, false, Action.BURN),
    EARTHQUAKE(false, false, false, false, Action.FALL);
    public final byte id;
    public final boolean canExistAtSeaLevel;
    public final boolean canExistOnSlope;
    public final boolean isFertile;
    public final boolean isObstruction;
    public final Action action;
    public final static Tile[] tiles;
    

    static {
        tiles = values();
    }

    private Tile(boolean sealevel, boolean slope, boolean fertile, boolean obstruction ,Action peonAction) {
        this.id = (byte) ordinal();
        canExistAtSeaLevel = sealevel;
        canExistOnSlope = slope;
        isFertile = fertile;
        isObstruction = obstruction;
        action = peonAction;
    }
}
