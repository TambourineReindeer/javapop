/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Lobby;

import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Server.GameInfo;
import java.util.Collection;

/**
 *
 * @author erinhowie
 */
public class Lobby {

    public Game currentGame;
    public Client client;
    private DefaultListModel gameList;
    public Vector<GameInfo> games;
    private DefaultListModel serverList;
    private LobbyFrame f;
    AnnounceListener a;
    GameListener gl;
    
    public Lobby() {
        gameList = new DefaultListModel();
        serverList = new DefaultListModel();

        f = new LobbyFrame(this);
        a = new AnnounceListener(this);
        gl = f.getGamePanel();
    }

    public void newGame(GameInfo gi) {
        currentGame = new Game(gi);
        gl.setGame(currentGame);
    }

    public ListModel getGameList() {
        return gameList;
    }

    public ListModel getServerList() {
        return serverList;
    }
    
    public void setGame(Game g)
    {
        gl.setGame(g);
    }

    public void setGames(Vector<GameInfo> gs) {
        games = gs;
        gameList.clear();
        for (GameInfo gi : gs) {
            gameList.addElement(gi);
            if (currentGame != null) {
                if (currentGame.id == gi.id) {
                    currentGame.update(gi);
                }
            }
        }
    }

    public void hide() {
        f.setVisible(false);
        this.currentGame = null;
        gl.setGame(currentGame);
    }

    public void show() {
        f.setVisible(true);
    }

    void addServer(String hostName) {
        if (serverList.indexOf(hostName) == -1) {
            serverList.addElement(hostName);
        }
    }

    void newClient(String s) {
        client = new Client(s, this);
        if (!client.isConnected())
            client = null;
    }

    void quit() {
        if (a != null) {
            a.kill();
        }
        if (f != null) {
            f.dispose();
        }
        if (client != null) {
            client.quit();
        }
    }
}
