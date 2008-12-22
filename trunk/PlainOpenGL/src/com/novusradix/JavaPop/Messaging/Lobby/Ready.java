/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;

/**
 *
 * @author erinhowie
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
