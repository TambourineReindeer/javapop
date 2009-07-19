package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.ServerHouses.ServerHouse;
import com.novusradix.JavaPop.Server.ServerPlayer;
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
 * @author gef
 */
public class HouseUpdate extends Message implements Externalizable {

    private static final long serialVersionUID = 1L;
    public Collection<Detail> details;
    Map<Integer, Integer> leaderHouses;

    @SuppressWarnings("unchecked")
    public HouseUpdate(Collection<Detail> ds, Map<ServerPlayer, ServerHouse> leaderMap) {
        details = new ArrayList<Detail>(ds);
        leaderHouses = new HashMap<Integer, Integer>();
        for (Entry<ServerPlayer, ServerHouse> e : leaderMap.entrySet()) {
            leaderHouses.put(e.getKey().getId(), e.getValue().id);
        }
    }

    @Override
    public void execute() {
        for (Detail d : details) {
            client.game.houses.updateHouse(d.id, d.x, d.y, client.game.players.get(d.playerId), d.level, d.strength, d.infected);
        }
        client.game.houses.setLeaders(leaderHouses);
    }

    public static class Detail implements Serializable {

        int id;
        int x, y;
        int level;
        int playerId;
        float strength;
        boolean infected;

        public Detail(int id, int x, int y, ServerPlayer p, int level, float strength, boolean infected) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.playerId = p.getId();
            this.level = level;
            this.strength = strength;
            this.infected = infected;
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
            out.writeInt(d.x);
            out.writeInt(d.y);
            out.writeInt(d.playerId);
            out.writeInt(d.level);
            out.writeFloat(d.strength);
            out.writeBoolean(d.infected);
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
            d.x = in.readInt();
            d.y = in.readInt();
            d.playerId = in.readInt();
            d.level = in.readInt();
            d.strength = in.readFloat();
            d.infected =in.readBoolean();
            details.add(d);
        }
        i = in.readInt();
        leaderHouses = new HashMap<Integer, Integer>();
        for (; i > 0; i--) {
            leaderHouses.put(in.readInt(), in.readInt());
        }
    }
}
