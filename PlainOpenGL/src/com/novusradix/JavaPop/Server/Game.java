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
public class Game implements Runnable{

    public Vector<Player> players;
    
    public Game()
    {
        players = new Vector<Player>();
    }
    
    public void addPlayer(Player p)
    {
        players.add(p);
    }
    
    public void startGame()
    {
        new Thread(this).start();   
    }

    public void run() {
 
    }
    
   

}
