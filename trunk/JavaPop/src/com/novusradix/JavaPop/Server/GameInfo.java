package com.novusradix.JavaPop.Server;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gef
 */
public class GameInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
    public Dimension mapSize;
    public Map<Integer,ServerPlayer.Info> players;

    public GameInfo(ServerGame g) {
        id = g.getId();
        if (g.heightMap != null) {
            mapSize = new Dimension(g.heightMap.getWidth(), g.heightMap.getBreadth());
        }
        players = new HashMap<Integer, ServerPlayer.Info>();
        for (ServerPlayer p : g.players) {
            players.put(p.getId(), p.info);
        }
    }

    @Override
    public String toString() {
        return id + " (" + players.size() + ")";
    }
}
