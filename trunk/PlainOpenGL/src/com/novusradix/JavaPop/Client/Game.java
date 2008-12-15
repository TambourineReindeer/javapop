/*
 * This class is the container for all data required by an active game instance.
 * 
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.GameStarted;
import com.novusradix.JavaPop.Server.Player.Info;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author gef
 */
public class Game extends TimerTask {

    public HeightMap heightMap;
    public Client client;
    public Peons peons;
    public Houses houses;
    private Timer timer;
    private float seconds;
    public Map<Integer, Player> players;
    public Player me;

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
        timer = new Timer("Client Game Animator");
        seconds = 1.0f / 20.0f;
        timer.scheduleAtFixedRate(this, 0, (int) (seconds * 1000.0f));
        
    }

    public void kill() {
        timer.cancel();
        timer = null;
    }

    public void run() {
        //start a clock,
        //move people.
        peons.step(seconds);
    //houses.step(seconds);
    }
}
