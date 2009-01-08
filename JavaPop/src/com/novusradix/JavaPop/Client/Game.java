/*
 * This class is the container for all data required by an active game instance.
 * 
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Server.Effects.Effect;
import com.novusradix.JavaPop.Server.Player.Info;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author gef
 */
public class Game extends TimerTask {

    public com.novusradix.JavaPop.HeightMap heightMap;
    public Client client;
    public Peons peons;
    public AbstractHouses houses;
    private Timer timer;
    private float seconds;
    public Map<Integer, Player> players;
    public Player me;
    GameFrame frame;
    Collection<GLObject> objects;
    public Map<Integer, Effect> effects;
    
    protected Game() {
    }

    public Game(GameStarted g, Client c) {
        client = c;
        objects = new ArrayList<GLObject>();
        HeightMap hm1 = new HeightMap(g.gi.mapSize);
        heightMap = hm1;
        objects.add(hm1);
        Peons p1 = new Peons(this);
        peons = p1;
        objects.add(p1);
        Houses h1 = new Houses(this);
        houses = h1;
        objects.add(h1);

        effects = new HashMap<Integer, Effect>();
        players = new HashMap<Integer, Player>();
        int index = 0;
        for (Info i : g.gi.players.values()) {
            Player p = new Player(i, this, index++);
            players.put(i.id, p);
            objects.add(p);
            if (i.id == c.info.id) {
                me = p;
            }
        }
        startTimer();
        frame = new GameFrame(this);
    }

    public void startTimer() {
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
