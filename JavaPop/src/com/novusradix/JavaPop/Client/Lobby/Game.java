package com.novusradix.JavaPop.Client.Lobby;

import com.novusradix.JavaPop.Messaging.PlayerUpdate.Info;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.novusradix.JavaPop.Server.GameInfo;

/**
 *
 * @author gef
 */
public class Game {

    DefaultListModel playerList;
    int id;

    Game(GameInfo gi) {
        id = gi.id;
        playerList = new DefaultListModel();
        for (Info player : gi.players.values()) {
            playerList.addElement(player.name);
        }
    }

    public ListModel getPlayerList() {
        return playerList;
    }

    void update(GameInfo gi) {
        playerList.clear();
        for (Info player : gi.players.values()) {
            playerList.addElement(player.name);
        }
    }
}
