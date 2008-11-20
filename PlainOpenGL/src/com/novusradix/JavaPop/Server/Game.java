/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.GameStarted;
import com.novusradix.JavaPop.Messaging.JoinedGame;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class Game implements Runnable {

    public static int nextId = 0;
    private int id;
    public Vector<Player> players;
    private Player owner;
    private Server server;
    public HeightMap h;

    public Game(Player owner) {
        this.owner = owner;
        server = owner.s;
        players = new Vector<Player>();
        players.add(owner);
        owner.currentGame = this;
        owner.sendMessage(new JoinedGame(this));
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
        h = new HeightMap(128, 128);
        h.randomize(1);
        GameStarted go = new GameStarted(this);
        server.sendAllPlayers(go);
        h.sendUpdates(players);
        
        
        new Thread(this).start();
    }

    public void run() {
        //start a clock,
        //move people.
    }
}
