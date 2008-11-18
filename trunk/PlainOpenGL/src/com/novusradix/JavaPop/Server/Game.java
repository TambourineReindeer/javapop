/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

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
    private HeightMap h;

    public Game(Player owner) {
        players = new Vector<Player>();
        players.add(owner);
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void addPlayer(Player p) {
        players.add(p);
        
        
    }

    public void startGame() {
        h = new HeightMap(128, 128);
        h.randomize(1);
        id = nextId++;
        h.sendUpdates(players);
        new Thread(this).start();
    }

    public void run() {
    }
}
