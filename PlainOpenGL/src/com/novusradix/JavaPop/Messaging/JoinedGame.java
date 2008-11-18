/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging;

/**
 *
 * @author mom
 */
public class JoinedGame extends Message {

    public int gameId;
    
    public JoinedGame(com.novusradix.JavaPop.Server.Game g)
    {
        gameId = g.getId();
    }
    
    @Override
    public void execute() {
       
    }

}
