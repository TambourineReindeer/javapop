package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Server.GameInfo;

/**
 *
 * @author gef
 */
public class GameStarted extends Message{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GameInfo gi;
    
    public GameStarted(Game g)
    {
      gi = new GameInfo(g);
    
    }

    @Override
    public void execute() {
        client.newGame(this);
    }
    
    
}
