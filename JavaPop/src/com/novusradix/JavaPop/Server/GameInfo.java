package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.PlayerUpdate;
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
    public Map<Integer,PlayerUpdate.Info> players;

    public GameInfo(ServerGame g) {
        id = g.getId();
        if (g.heightMap != null) {
            mapSize = new Dimension(g.heightMap.getWidth(), g.heightMap.getBreadth());
        }
        players = new HashMap<Integer, PlayerUpdate.Info>();
        for (ServerPlayer p : g.players) {
            players.put(p.getId(), new PlayerUpdate.Info(p));
        }
    }

    @Override
    public String toString() {
        return id + " (" + players.size() + ")";
    }
}
