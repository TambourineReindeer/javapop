/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.GameList;
import com.novusradix.JavaPop.Messaging.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mom
 */
public class Server implements Runnable {

    private Map<Integer, Game> games;
    private Vector<Player> players;
    private int port;
    private boolean keepAlive = true;
    Announcer a;
    ServerForm form;
    ServerSocket s;

    public Server(int port) {
        games = new HashMap<Integer, Game>();
        players = new Vector<Player>();
        this.port = port;
        form = new ServerForm(this);
        new Thread(this, "Server").start();
        a = new Announcer(13579);
        form.setBounds(500,500,150, 150);
        form.setVisible(true);
    }

    Collection<Game> getGames() {
        return games.values();
    }

    void kill() {
        keepAlive = false;
        form.dispose();
        a.kill();
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            s = new ServerSocket(port);
            while (keepAlive) {
                Player p = new Player(this, s.accept());
                players.add(p);
                GameList gl = new GameList(games.values());
                p.sendMessage(gl);
            }
        } catch (IOException ioe) {
        }

        System.out.print("Server quitting.\n");
    }

    public void newGame(Player p) {

        if (p.currentGame != null) {
            p.currentGame.removePlayer(p);
        }

        Game g = new Game(p);

        games.put(g.getId(), g);

        GameList gl = new GameList(games.values());
        sendAllPlayers(gl);
    }

    public void joinGame(int gId, Player p) {
        Game g = games.get(gId);

        g.addPlayer(p);

        GameList gl = new GameList(games.values());
        sendAllPlayers(gl);
    }

    public void removePlayer(Player p) {
        players.remove(p);
        GameList gl = new GameList(games.values());
        sendAllPlayers(gl);
    }

    public void sendAllPlayers(Message m) {
        for (Player pl : players) {
            pl.sendMessage(m);
        }
    }
}
