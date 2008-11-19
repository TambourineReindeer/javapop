/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging;

/**
 *
 * @author mom
 */
public class JoinGame extends Message{

    int gameId;
    
    public JoinGame(int gameId)
    {
        this.gameId = gameId;
    }
    
    @Override
    public void execute() {
        this.server.joinGame(gameId, serverPlayer);
    }

}
