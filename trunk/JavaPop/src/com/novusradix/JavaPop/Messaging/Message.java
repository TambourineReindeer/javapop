/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import java.io.Externalizable;
import java.io.Serializable;

/**
 *
 * @author erinhowie
 */
public abstract class Message implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected transient com.novusradix.JavaPop.Server.Server server;
    protected transient com.novusradix.JavaPop.Server.Player serverPlayer;
    protected transient com.novusradix.JavaPop.Server.Game serverGame;
 
    protected transient com.novusradix.JavaPop.Client.Client client;
    protected transient com.novusradix.JavaPop.Client.Game clientGame;
    protected transient com.novusradix.JavaPop.HeightMap clientMap;

    public void setClient(com.novusradix.JavaPop.Client.Client c) {
        client = c;
        if (c != null) {
            clientGame = c.game;
            if (c.game != null) {
                clientMap = c.game.heightMap;
            }
        }
    }
   
    public void setServer(com.novusradix.JavaPop.Server.Server s, com.novusradix.JavaPop.Server.Game game, com.novusradix.JavaPop.Server.Player player)
    {
        server = s;
        serverGame = game;
        serverPlayer = player;
    }
    
    public abstract void execute();
}
