/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Server.Player;
import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author mom
 */
public class HouseUpdate extends Message implements Externalizable {

    private static final long serialVersionUID = 1L;
    public Collection<Detail> details;
    Map<Integer, Integer> leaderHouses;

    @SuppressWarnings("unchecked")
    public HouseUpdate(Collection<Detail> ds, Map<Player, House> leaderMap) {
        details = new ArrayList<Detail>(ds);
        leaderHouses = new HashMap<Integer, Integer>();
        for (Entry<Player, House> e : leaderMap.entrySet()) {
            leaderHouses.put(e.getKey().getId(), e.getValue().id);
        }
    }

    @Override
    public void execute() {
        for (Detail d : details) {
            client.game.houses.updateHouse(d.id, d.pos, client.game.players.get(d.playerId), d.level);
        }
        client.game.houses.setLeaders(leaderHouses);
    }

    public static class Detail implements Serializable {

        int id;
        Point pos;
        int level;
        int playerId;

        public Detail(int id, Point pos, Player p, int level) {
            this.id = id;
            this.pos = pos;
            this.playerId = p.getId();
            this.level = level;
        }

        private Detail() {
        }
    }

    public HouseUpdate() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(details.size());
        for (Detail d : details) {
            out.writeInt(d.id);
            out.writeInt(d.pos.x);
            out.writeInt(d.pos.y);
            out.writeInt(d.playerId);
            out.writeInt(d.level);
        }
        out.writeInt(leaderHouses.size());
        for (Entry<Integer, Integer> e : leaderHouses.entrySet()) {
            out.writeInt(e.getKey());
            out.writeInt(e.getValue());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        details = new ArrayList<Detail>();
        int i = in.readInt();
        for (; i > 0; i--) {
            Detail d = new Detail();
            d.id = in.readInt();
            d.pos = new Point();
            d.pos.x = in.readInt();
            d.pos.y = in.readInt();
            d.playerId = in.readInt();
            d.level = in.readInt();

            details.add(d);
        }
        i = in.readInt();
        leaderHouses = new HashMap<Integer, Integer>();
        for (; i > 0; i--) {
            leaderHouses.put(in.readInt(), in.readInt());
        }
    }
}
