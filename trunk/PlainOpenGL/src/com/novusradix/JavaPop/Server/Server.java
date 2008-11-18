/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.GameList;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class Server implements Runnable {

    private Vector<Game> games;
    private Vector<Player> players;
    
    private int port;
    public Server(int port)
    {
        games=new Vector<Game>();
        players = new Vector<Player>();
        this.port = port;
        new Thread(this,"Server").start();
    }
    
    public void run() {
        try
        {
            ServerSocket s = new ServerSocket(port);
        
        while(s.isBound())
        {
            Player p = new Player(this, s.accept());
            players.add(p);
            GameList gl = new GameList(games);
            p.sendMessage(gl);
        }
        }
        catch (IOException ioe)
        {}
        System.out.print("Server quitting.\n");
    }
    
    public void newGame(Player p)
    {
        games.add(new Game(p));
        
        GameList gl = new GameList(games);
        for( Player pl:players)
            pl.sendMessage(gl);
    }

}
