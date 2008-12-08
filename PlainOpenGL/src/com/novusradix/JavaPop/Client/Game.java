/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.GameStarted;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author mom
 */
public class Game extends TimerTask {

    public HeightMap heightMap;
    public Client client;
    public Peons peons;
    public Houses houses;
    private Timer timer;
    private float seconds;

    public Game(GameStarted g, Client c) {
        heightMap = new HeightMap(g.gi.mapSize);
        client = c;
        peons = new Peons(this);
        houses = new Houses(this);

        timer = new Timer("Client Game Animator");
        seconds = 1.0f / 20.0f;
        timer.scheduleAtFixedRate(this, 0, (int) (seconds * 1000.0f));
        new GameFrame(this);
    }

    public void kill()
    {
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
