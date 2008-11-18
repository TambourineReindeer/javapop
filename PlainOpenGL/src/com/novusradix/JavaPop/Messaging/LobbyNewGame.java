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
        
        server.newGame(serverPlayer); 
       
    }
}
