/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.GameInfo;

/**
 *
 * @author gef
 */
public class JoinedGame extends Message {

    public GameInfo g;
    
    public JoinedGame(com.novusradix.JavaPop.Server.Game game)
    {
        g = new GameInfo(game);
    }
    
    @Override
    public void execute() {
        this.playerState.gamePanel.setGame(g);
        this.playerState.gamesPanel.setGame(g);
    }

}
