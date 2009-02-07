package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Server.Peons.Peon;
import com.novusradix.JavaPop.Server.Peons.Peon.State;
import com.novusradix.JavaPop.Server.Peons.Perseus;
import com.novusradix.JavaPop.Server.ServerHouses.ServerHouse;

/**
 *
 * @author gef
 */
public class Hero extends Message {

    public enum Type {

        PERSEUS, ADONIS, HERACLES, ODYSSEUS, HELEN
    }
    private Type type;

    public Hero(Type t) {
        type = t;
    }

    @Override
    public void execute() {
        Peon p;
        p = serverGame.peons.getLeaders().get(serverPlayer);
        if (p == null) {
            ServerHouse h = serverGame.houses.getLeaderHouse(serverPlayer);
            if (h != null) {
                p = h.knockDown();
            }
        }
        if (p != null) {
            serverGame.peons.makeHero(p, type);
        } 
    }
}
