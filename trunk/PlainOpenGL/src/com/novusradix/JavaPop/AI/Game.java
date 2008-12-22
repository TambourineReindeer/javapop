/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.AI;

import com.novusradix.JavaPop.Client.HeightMap;
import com.novusradix.JavaPop.Client.Peons;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Client.Houses;
import com.novusradix.JavaPop.Client.Player;
import com.novusradix.JavaPop.Server.Player.Info;
import java.util.HashMap;

/**
 *
 * @author mom
 */
public class Game extends com.novusradix.JavaPop.Client.Game {

    public Game(GameStarted g, Client c) {
        heightMap = new HeightMap(g.gi.mapSize);
        client = c;
        peons = new Peons(this);
        houses = new Houses(this);
        players = new HashMap<Integer, Player>();
        for (Info i : g.gi.players.values()) {
            Player p = new Player(i, this);
            players.put(i.id, p);
            if (i.id == c.info.id) {
                me = p;
            }
        }
    }
}
