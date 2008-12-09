/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

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
    public transient com.novusradix.JavaPop.Server.Server server;
    public transient com.novusradix.JavaPop.Server.Player serverPlayer;
    public transient com.novusradix.JavaPop.Server.Game serverGame;

    public void setClient(com.novusradix.JavaPop.Client.Client c) {
        client = c;
        if (c != null) {
            clientGame = c.game;
            if (c.game != null) {
                clientMap = c.game.heightMap;
            }
        }
    }
    transient com.novusradix.JavaPop.Client.Client client;
    transient com.novusradix.JavaPop.Client.Game clientGame;
    transient com.novusradix.JavaPop.Client.HeightMap clientMap;

    public abstract void execute();
}
