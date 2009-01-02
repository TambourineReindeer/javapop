/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author erinhowie
 */
public class GameInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
    public Dimension mapSize;
    public Map<Integer,Player.Info> players;

    public GameInfo(Game g) {
        id = g.getId();
        if (g.heightMap != null) {
            mapSize = new Dimension(g.heightMap.getWidth(), g.heightMap.getBreadth());
        }
        players = new HashMap<Integer, Player.Info>();
        for (Player p : g.players) {
            players.put(p.getId(), p.info);
        }
    }

    @Override
    public String toString() {
        return id + " (" + players.size() + ")";
    }
}
