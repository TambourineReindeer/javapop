/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;


import com.novusradix.JavaPop.Messaging.GameStarted;

/**
 *
 * @author mom
 */
public class Game {

    public HeightMap heightMap;
    public Client client;
    public Peons peons;
    public Houses houses;
    
    public Game(GameStarted g, Client c) {
       heightMap = new HeightMap(g.gi.mapSize);
       client = c;
       peons = new Peons(this);
       houses = new Houses(this);
        
       new GameFrame(this);
        
        
    }
 }
