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

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute()
    {
        
        server.newGame(serverPlayer); 
       
    }
}
