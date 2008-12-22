/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;

/**
 *
 * @author mom
 */
public class JoinGame extends Message{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int gameId;

    
    public JoinGame(int gameId)
    {
        this.gameId = gameId;

    }
    
    @Override
    public void execute() {
        if(serverGame == null){
            server.joinGame(gameId, serverPlayer);
        }
        else if(serverGame.getId() != gameId)
        {
            serverGame.removePlayer(serverPlayer);
            server.joinGame(gameId, serverPlayer);
        }
            
    }

}
