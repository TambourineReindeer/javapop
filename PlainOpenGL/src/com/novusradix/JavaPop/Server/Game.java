/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.GameStarted;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Messaging.JoinedGame;
import com.novusradix.JavaPop.Messaging.Message;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class Game extends TimerTask {

    public volatile static int nextId = 0;
    private int id;
    public Vector<Player> players;
    private Player owner;
    private Server server;
    private Timer timer;
    private float seconds;
    public HeightMap heightMap;
    public Peons peons;
    public Houses houses;

    public Game(Player owner) {
        this.owner = owner;
        server = owner.s;
        players = new Vector<Player>();
        players.add(owner);
        owner.currentGame = this;
        owner.sendMessage(new JoinedGame(this));
        id = nextId++;
    }

    public int getId() {
        return id;
    }

    public void addPlayer(Player p) {
        players.add(p);
        p.currentGame = this;
        p.sendMessage(new JoinedGame(this));

    }

    public void removePlayer(Player p) {
        players.remove(p);
        p.currentGame = null;
    //TODO: send a message?
    }

    public void PlayerReady(Player p) {
        p.ready = true;
        for (Player pl : players) {
            if (!pl.ready) {
                return;
            }
        }
        startGame();
    }

    public void startGame() {
        id = nextId++;
        heightMap = new HeightMap(128, 128);
        heightMap.randomize(1);
        peons = new Peons(this);
        houses = new Houses(this);

        for(Player p:players)
        {
            peons.addPeon(2.5f, 2.5f, 200,players.get(0));
        }
        GameStarted go = new GameStarted(this);
        server.sendAllPlayers(go);
        HeightMapUpdate m = heightMap.GetUpdate();
        if (m != null) {
            sendAllPlayers(m);
        }
        timer = new Timer("Game " + id);
        seconds = 1.0f / 20.0f;
        timer.scheduleAtFixedRate(this, 0, (int) (seconds * 1000.0f));
    }

    public void run() {
        //start a clock,
        //move people.

        if (players.isEmpty()) {
            timer.cancel();
        }
        HeightMapUpdate m;
        synchronized (heightMap) {
            peons.step(seconds);
            houses.step(seconds);
            m = heightMap.GetUpdate();
        }
        if (m != null) {
            sendAllPlayers(m);
        }
    }

    public void sendAllPlayers(Message m) {
        for (Player pl : players) {
            pl.sendMessage(m);
        }
    }
}
