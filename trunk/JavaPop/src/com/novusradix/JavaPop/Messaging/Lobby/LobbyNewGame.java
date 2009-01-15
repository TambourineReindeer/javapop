package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;

/**
 *
 * @author gef
 */
public class LobbyNewGame extends Message{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute()
    {
        
        server.newGame(serverPlayer); 
       
    }
}
