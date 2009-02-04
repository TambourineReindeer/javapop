package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.MultiMap;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import com.novusradix.JavaPop.Server.Peons.Peon;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class ServerPeons {

    public final ServerGame game;
    private final Vector<Peon> peons;
    private final MultiMap<Point, Peon> map;
    private final Map<ServerPlayer, Peon> leaders;
    private int nextId = 1;

    public enum Action {

        DROWN, BURN, FALL, NONE;
    }

    public enum State {

        WAITING, WALKING, DEAD, SETTLED, DROWNING, ELECTRIFIED, FALLING, BURNT, FIGHTING
    }

    public ServerPeons(ServerGame g) {
        game = g;
        peons = new Vector<Peon>();
        map = new MultiMap<Point, Peon>();
        leaders = new HashMap<ServerPlayer, Peon>();
    }

    public Collection<Peon> getPeons(Point p) {
        return map.get(p);
    }

    public void addPeon(int x, int y, float strength, ServerPlayer player, boolean leader) {
        Peon p = new Peon(x + 0.5f, y + 0.5f, strength, player);
        peons.add(p);
        map.put(p.getPoint(), p);
        if (leader) {
            leaders.put(player, p);
        }
    }

    public void addPeon(Point p, float strength, ServerPlayer player, boolean leader) {
        addPeon(p.x, p.y, strength, player, leader);
    }

    public Collection<Peon> getPeons() {
        return peons;
    }

    public boolean isLeader(Peon p) {
        return leaders.containsValue(p);
    }

    public Map<ServerPlayer, Peon> getLeaders() {
        return leaders;
    }

    public void step(float seconds) {
        if (peons != null) {
            Peon p;

            Vector<PeonUpdate.Detail> pds = new Vector<PeonUpdate.Detail>();
            PeonUpdate.Detail pd;

            for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

                p = i.next();
                map.remove(p.getPoint(), p);
                pd = p.step(seconds);
                map.put(p.getPoint(), p);
                if (pd != null) {
                    pds.add(pd);

                    switch (pd.state) {
                        case DEAD:
                            if (leaders.containsValue(p)) {
                                p.player.info.ankh.setLocation(p.getPoint());
                                leaders.remove(p.player);
                            }
                        case SETTLED:
                            i.remove();
                            map.remove(p.getPoint(), p);
                            break;
                    }
                }
            }
            if (pds.size() > 0) {
                game.sendAllPlayers(new PeonUpdate(pds, leaders));
                pds.clear();
            }
        }
    }
}
