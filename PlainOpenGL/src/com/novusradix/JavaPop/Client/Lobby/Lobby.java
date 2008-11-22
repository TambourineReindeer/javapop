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

/**
 *
 * @author erinhowie
 */
public class Lobby {

    public Game currentGame;
    public Client client;
    private DefaultListModel gameList;
    private LobbyFrame f;

    public Lobby() {
        gameList = new DefaultListModel();
        f = new LobbyFrame(this);
        f.setVisible(true);
    }

    public void newGame(GameInfo gi) {
        currentGame = new Game(gi);
        f.getGamePanel().setLobby(this);
    }

    public ListModel getGameList() {
        return gameList;
    }

    public void setGames(Vector<GameInfo> gs) {
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
    }

    void quit() {
        f.dispose();
        client.quit();
    }
}
