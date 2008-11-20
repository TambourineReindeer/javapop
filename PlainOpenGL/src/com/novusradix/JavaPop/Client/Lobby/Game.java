/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Lobby;

import com.novusradix.JavaPop.Messaging.JoinedGame;
import com.novusradix.JavaPop.Server.GameInfo;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
    Game(GameInfo gi) {
        id = gi.id;
        playerList = new DefaultListModel();
        for (String player : gi
 * @author erinhowie
 */
public class Game {

    DefaultListModel playerList;
    int id;

    Game(GameInfo gi) {
        id = gi.id;
        playerList = new DefaultListModel();
        for (String player : gi.players) {
            playerList.addElement(player);
        }
    }

    public ListModel getPlayerList() {
        return playerList;
    }

    void update(GameInfo gi) {

        playerList.clear();
        for (String player : gi.players) {
            playerList.addElement(player);
        }
    }
}
