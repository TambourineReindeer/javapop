/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class Server implements Runnable {

    private Vector<Game> games;
    
    private int port;
    public Server(int port)
    {
        this.port = port;
        new Thread(this).start();
    }
    
    public void run() {
        try
        {
            ServerSocket s = new ServerSocket(port);
        
        while(s.isBound())
        {
            new Player(this, s.accept());
        }
        }
        catch (IOException ioe)
        {}
        port=0;
    }
    
    public Game newGame(Player p)
    {
        return new Game(p);
    }

}
