/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Server.GameInfo;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author erinhowie
 */
public class GameList extends Message implements Serializable{

    public Vector<GameInfo> games;
    
    public GameList(Vector<Game> gs)
    {
        games = new Vector<GameInfo>();
        for(Game g:gs)
            games.add(new GameInfo(g));
    }
    
    @Override
    public void execute() {
       
        this.playerState.gamesPanel.setGames(games);
    }
}
