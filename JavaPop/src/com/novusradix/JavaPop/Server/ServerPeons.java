package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.MultiMap;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import com.novusradix.JavaPop.Messaging.Tools.Hero.Type;
import com.novusradix.JavaPop.Server.Peons.Peon;
import com.novusradix.JavaPop.Server.Peons.Peon.State;
import com.novusradix.JavaPop.Server.Peons.Perseus;
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

    public void infectWithPlague(int x, int y) {
        for (Peon victim : getPeons(new Point(x, y))) {
            victim.infectWithPlague();
        }
    }

    public void makeHero(Peon p, Type type) {
        synchronized (peons) {
            p.setState(State.DEAD);
            Peon hero;
            switch (type) {
                default:
                case PERSEUS:
                    hero = new Perseus(p);
            }
            peons.add(hero);
        }
    }

    public enum Action {

        DROWN, BURN, FALL, NONE;
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

    public Peon addPeon(int x, int y, float strength, ServerPlayer player, boolean leader, boolean infected) {
        Peon p = new Peon(x + 0.5f, y + 0.5f, strength, infected, player, game);
        synchronized (peons) {
            peons.add(p);
            map.put(p.getPoint(), p);
            if (leader) {
                leaders.put(player, p);
            }
        }
        return p;
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
            for (ServerPlayer sp : game.players) {
                sp.peonMana = 0;
            }
            Vector<PeonUpdate.Detail> pds = new Vector<PeonUpdate.Detail>();
            PeonUpdate.Detail pd;

            synchronized (peons) {
                for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

                    p = i.next();
                    map.remove(p.getPoint(), p);
                    pd = p.step(seconds);
                    map.put(p.getPoint(), p);
                    p.player.peonMana += Math.max(0, p.strength);
                    if (pd != null) {
                        pds.add(pd);

                        switch (pd.state) {
                            case DEAD:
                                if (leaders.containsValue(p)) {
                                    p.player.setPapalMagnet(p.getPoint().x, p.getPoint().y);
                                    leaders.remove(p.player);
                                }
                                i.remove();
                                map.remove(p.getPoint(), p);
                                break;
                            case SETTLED:
                                if (leaders.containsValue(p)) {
                                    leaders.remove(p.player);
                                }
                                i.remove();
                                map.remove(p.getPoint(), p);
                                break;
                        }
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
