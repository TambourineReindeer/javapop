/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.Player.Info;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mom
 */
public class PlayerUpdate extends Message {

    private Collection<Info> info;

    public PlayerUpdate(Info i) {
        this.info = new ArrayList<Info>(1);
        this.info.add(i);
    }
    
    public PlayerUpdate(Collection<Info> is)
    {
        this.info = is;
    }
    
    @Override
    public void execute() {
        for (Info i : info) {
            clientGame.players.get(i.id).update(i);
        }
    }
}
