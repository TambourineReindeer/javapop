/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Server;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author erinhowie
 */
public class GameInfo implements Serializable{
    int id;
    Vector<String> players;
    
    public GameInfo(Game g)
    {
        this.id = g.getId();
        players = new Vector<String>();
        for (Player p:g.players)
            players.add(p.name);
    }
    
    @Override
    public String toString()
    {
        return id + " (" + players.size() + ")";
    }
}
