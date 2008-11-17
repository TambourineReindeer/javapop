/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

/**
 *
 * @author erinhowie
 */
public class LobbyNewGame extends Message{

    public void execute()
    {
        com.novusradix.JavaPop.Server.Game g;
        g = server.newGame();
        serverPlayer.currentGame = g;
        g.addPlayer(serverPlayer);
    }
}
