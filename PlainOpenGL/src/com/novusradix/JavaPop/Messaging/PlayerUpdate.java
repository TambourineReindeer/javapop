/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.Player.Info;

/**
 *
 * @author mom
 */
public class PlayerUpdate extends Message{
    private Info info;

    public PlayerUpdate(Info i)
    {
        this.info = i;
    }
    
    @Override
    public void execute() {
        clientGame.players.get(info.id).update(info);
    }
    
}
