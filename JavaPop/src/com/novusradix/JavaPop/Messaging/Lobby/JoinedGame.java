package com.novusradix.JavaPop.Messaging.Lobby;

import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Server.GameInfo;

/**
 *
 * @author gef
 */
public class JoinedGame extends Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public GameInfo gi;

    public JoinedGame(com.novusradix.JavaPop.Server.ServerGame game) {
        gi = new GameInfo(game);
    }

    @Override
    public void execute() {
        if (client.lobby != null) {
            client.lobby.newGame(gi);
        }
    }
}
