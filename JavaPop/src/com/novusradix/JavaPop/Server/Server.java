package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.Lobby.GameList;
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
 * @author gef
 */
public class Server implements Runnable {

    private Map<Integer, ServerGame> games;
    private final Vector<ServerPlayer> players;
    int port;
    private boolean keepAlive = true;
    Announcer a;
    ServerForm form;
    private ServerSocket s;

    public void main() {
        new Server(13579);
    }

    public Server(int port) {
        games = new HashMap<Integer, ServerGame>();
        players = new Vector<ServerPlayer>();
        this.port = port;
        form = new ServerForm(this);
        new Thread(this, "Server").start();
        a = new Announcer(port);
        form.setBounds(500, 500, 150, 150);
        form.setVisible(true);


    }

    Collection<ServerGame> getGames() {
        return games.values();
    }

    void writeLog(String s) {
        form.writeLog(s);
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
        writeLog("Server started\n");
        try {
            s = new ServerSocket(port);
            while (keepAlive) {
                ServerPlayer p = new ServerPlayer(this, s.accept());
                players.add(p);
                GameList gl = new GameList(games.values());
                p.sendMessage(gl);
                writeLog("Player '" + p.getName() + "' conected from " + p.socket.toString() + "\n");
            }
        } catch (IOException ioe) {
        }
        form.dispose();
        a.kill();
        writeLog("Server quitting.\n");
    }

    public void newGame(ServerPlayer owner) {

        if (owner.currentGame != null) {
            owner.currentGame.removePlayer(owner);
        }

        ServerGame g = new ServerGame(owner);

        games.put(g.getId(), g);

        GameList gl = new GameList(games.values());
        sendAllPlayers(gl);
        writeLog("Game " + g.getId() + " started\n");
    }

    public void joinGame(int gId, ServerPlayer p) {

        ServerGame g = games.get(gId);
        if (g == null) {
            writeLog("Player '" + p.getName() + "' attempted to join game " + gId + " but the request failed\n");
            return;
        }
        g.addPlayer(p);

        GameList gl = new GameList(games.values());
        sendAllPlayers(gl);
        writeLog("Player '" + p.getName() + "' joined game " + gId + "\n");
    }

    public void removePlayer(ServerPlayer p) {

        players.remove(p);
        GameList gl = new GameList(games.values());
        sendAllPlayers(gl);
        writeLog("Player '" + p.getName() + "' has disconnected\n");

    }

    public void sendAllPlayers(Message m) {
        synchronized (players) {
            for (ServerPlayer pl : players) {
                pl.sendMessage(m);
            }
        }
    }
}
