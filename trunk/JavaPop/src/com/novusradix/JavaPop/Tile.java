package com.novusradix.JavaPop;

import com.novusradix.JavaPop.Server.ServerPeons.Action;

/**
 * Each of the possible tile types is defined in this class. Each tile has properties that define where it can exist and how entities can interact with it.
 * @author gef
 */
public enum Tile {
    SEA(true, false, false, true, Action.DROWN),
    EMPTY_FLAT(false, false, true, false, Action.NONE),
    EMPTY_SLOPE(false, true, false, false, Action.NONE),
    BURNT(false, false, false, false, Action.NONE),
    FARM(false, false, true, false, Action.NONE),
    SWAMP(false, false, false, false, Action.FALL),
    FUNGUS(false, false, false, false, Action.FALL),
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

    /**
     *
     * @param sealevel  Can the tile exist at sealevel? If not, it will change to SEA if it is flattened to sea level.
     * @param slope Can the tile exist on a slope? If not, it will be replaced by EMPTY_SLOPE if it becomes sloped.
     * @param fertile   Can houses use this land as farmland?
     * @param obstruction   Can people walk on the tile?
     * @param peonAction What action should be performed on a peon when he enters the tile?
     */
    private Tile(boolean sealevel, boolean slope, boolean fertile, boolean obstruction ,Action peonAction) {
        this.id = (byte) ordinal();
        canExistAtSeaLevel = sealevel;
        canExistOnSlope = slope;
        isFertile = fertile;
        isObstruction = obstruction;
        action = peonAction;
    }
}
