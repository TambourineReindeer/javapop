/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Vector;

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
    public Vector<String> players;

    public GameInfo(Game g) {
        id = g.getId();
        if (g.heightMap != null) {
            mapSize = new Dimension(g.heightMap.getWidth(), g.heightMap.getBreadth());
        }
        players = new Vector<String>();
        for (Player p : g.players) {
            players.add(p.name);
        }
    }

    @Override
    public String toString() {
        return id + " (" + players.size() + ")";
    }
}
