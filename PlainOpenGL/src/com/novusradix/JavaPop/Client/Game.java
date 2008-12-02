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

    public HeightMap h;
    public Client client;
    
    public Game(GameStarted g, Client c) {
       h = new HeightMap(g.gi.mapSize);
       client = c;
        
       new GameFrame(h, client);
        
        
    }
 }
