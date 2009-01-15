package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;

/**
 *
 * @author gef
 */
public class Ready extends Message{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Ready(){}
    
    @Override
    public void execute() {
        serverGame.PlayerReady(serverPlayer);
    }

}
