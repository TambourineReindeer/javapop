package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Server.ServerGame;
import com.novusradix.JavaPop.Server.GameInfo;
import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

/**
 *
 * @author gef
 */
public class GameList extends Message implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Vector<GameInfo> games;

    public GameList(Collection<ServerGame> gs) {
        games = new Vector<GameInfo>();
        for (ServerGame g : gs) {
            games.add(new GameInfo(g));
        }
    }

    @Override
    public void execute() {

        if (client.lobby != null) {
            client.lobby.setGames(games);
        }
    }
}
