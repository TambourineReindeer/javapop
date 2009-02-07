package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HouseUpdate;
import com.novusradix.JavaPop.Server.Peons.Peon;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;

import static java.lang.Math.*;

public class ServerHouses {

    private final ServerGame game;
    private byte[][] map;
    private byte[][] newmap;
    private final Vector<HouseUpdate.Detail> hds;
    private final Vector<Integer> newHouses;
    private final SortedMap<Integer, ServerHouse> allHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;
    private final Map<ServerPlayer, ServerHouse> leaderHouses;

    public ServerHouses(ServerGame g) {
        game = g;
        leaderHouses = new HashMap<ServerPlayer, ServerHouse>();
        map = new byte[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        newmap = new byte[game.heightMap.getWidth()][game.heightMap.getBreadth()];

        allHouses = new TreeMap<Integer, ServerHouse>();
        newHouses = new Vector<Integer>();
        hds = new Vector<HouseUpdate.Detail>();
    }

    public void addHouse(int x, int y, ServerPlayer player, float strength, boolean leader) {
        synchronized (allHouses) {
            if (canBuild(x, y)) {
                ServerHouse h = new ServerHouse(x, y, player, strength);
                allHouses.put(x + y * game.heightMap.width, h);
                if (leader) {
                    leaderHouses.put(player, h);
                }
            }
        }
    }

    public boolean canBuild(int x, int y) {
        if (game.heightMap.tileInBounds(x, y)) {
            return (map[x][y] == EMPTY && game.heightMap.getTile(x, y).isFertile && !newHouses.contains(x + y * game.heightMap.width));
        }
        return false;
    }

    public ServerHouse getLeaderHouse(ServerPlayer p) {
        return leaderHouses.get(p);
    }

    private Collection<ServerHouse> affectedHouses(SortedSet<Integer> mapChanges) {
        Collection<ServerHouse> hs = new ArrayList<ServerHouse>();
        if (mapChanges.size() == 0) {
            return hs;
        }
        int firstChange, lastChange;
        firstChange = mapChanges.first() - 3 - 3 * game.heightMap.width;
        lastChange = mapChanges.last() + 3 + 3 * game.heightMap.width + 1;
        for (ServerHouse h : allHouses.subMap(firstChange, lastChange).values()) {
            int y;
            Integer max, min;
            min = h.pos.x - 3 + (h.pos.y - 3) * game.heightMap.width; //subset from here (inclusive)
            max = h.pos.x + 3 + (h.pos.y + 3) * game.heightMap.width + 1; //to here (exclusive)
            for (int p : mapChanges.subSet(min, max)) {
                y = p / game.heightMap.width;
                if ((y <= h.pos.y + 3) && (y >= h.pos.y - 3)) {
                    hs.add(h);
                    break;
                }
            }
        }
        return hs;
    }

    public void step(float seconds) {
        for (ServerPlayer sp : game.players) {
            sp.houseMana = 0;
        }

        SortedSet<Integer> mapChanges = game.heightMap.takeHouseChanges();
        Collection<ServerHouse> hs = affectedHouses(mapChanges);
        for (ServerHouse h : hs) {
            h.setLevel();
        }
        for (int y = 0; y < game.heightMap.getBreadth(); y++) {
            for (int x = 0; x < game.heightMap.getWidth(); x++) {
                newmap[y][x] = 0;
            }
        }
        synchronized (allHouses) {
            newHouses.clear();
            Iterator<ServerHouse> i = allHouses.values().iterator();
            ServerHouse h;
            for (; i.hasNext();) {
                h = i.next();
                if (h.strength <= 0) {
                    i.remove();
                    removeHouse(h);
                    continue;
                }
                if (game.heightMap.isFlat(h.pos.x, h.pos.y) && newmap[h.pos.x][h.pos.y] == 0) {
                    h.paintmap(newmap);
                    h.step(seconds);
                } else {
                    i.remove();
                    removeHouse(h);
                }
            }
            i = allHouses.values().iterator();
            for (; i.hasNext();) {
                h = i.next();
                if (newmap[h.pos.x][h.pos.y] != HOUSE) {
                    i.remove();
                    removeHouse(h);
                }
                else
                {
                    h.serverPlayer.houseMana +=h.strength;
                }
            }
        }

        //synchronized (game.heightMap) {
        //   unpaint();
        repaint();
        byte[][] t;
        t = map;
        map = newmap;
        newmap = t;
        //    paint();
        //}
        if (!hds.isEmpty()) {
            game.sendAllPlayers(new HouseUpdate(hds, leaderHouses));
            hds.clear();
        }
    }

    private Peon removeHouse(ServerHouse h) {
        boolean leader = leaderHouses.containsValue(h);
        hds.add(new HouseUpdate.Detail(h.id, h.pos.x, h.pos.y, h.serverPlayer, -1, 0));
        if (leader) {
            leaderHouses.remove(h.serverPlayer);
        }
        if (h.strength > 0) {
            return game.peons.addPeon(h.pos.x, h.pos.y, h.strength, h.serverPlayer, leader);
        }
        return null;
    }

    public ServerHouses.ServerHouse getHouse(int x, int y) {
        return allHouses.get(x + y * game.heightMap.width);
    }

    private void repaint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (newmap[x][y] != map[x][y]) {
                    if (newmap[x][y] == EMPTY) {
                        if (game.heightMap.getTile(x, y).isFertile) {
                            game.heightMap.clearTile(x, y);
                        }
                    } else {
                        game.heightMap.setTile(x, y, Tile.FARM);
                    }
                }
            }
        }
    }

    public int countFlatLand(int x, int y) {
        int flat = 0;
        int px, py;
        for (int radius = 0; radius <= 3; radius++) {
            for (Point offset : Helpers.rings[radius]) {
                px = x + offset.x;
                py = y + offset.y;

                if (game.heightMap.tileInBounds(px, py)) {
                    Tile tile = game.heightMap.getTile(px, py);
                    if (tile.isFertile) {
                        flat++;
                    }

                }
            }
            if (flat < (2 * radius + 1) * (2 * radius + 1)) {
                break;
            }

        }
        return flat;
    }

    public Point nearestHouse(Point p, Set<ServerPlayer> players) {
        int d2 = game.heightMap.getWidth() * game.heightMap.getBreadth() + 1;
        ServerHouse nearest = null;
        for (ServerHouse h : allHouses.values()) {
            int nd2 = (p.x - h.pos.x) * (p.x - h.pos.x) + (p.y - h.pos.y) * (p.y - h.pos.y);
            if (nd2 < d2) {
                if (players.contains(h.serverPlayer)) {
                    nearest = h;
                    d2 = nd2;
                }
            }
        }
        if (nearest != null) {
            return nearest.pos;
        }

        return null;
    }

    public class ServerHouse extends com.novusradix.JavaPop.House {

        private ServerPlayer serverPlayer;
        private boolean changed;

        public ServerHouse(int x, int y, ServerPlayer player, float strength) {
            id = x + y * game.heightMap.width;
            changed = true;
            pos = new Point(x, y);
            level = 1;
            this.player = player;
            serverPlayer = player;
            this.strength = strength;
            setLevel();
            newHouses.add(x + y * game.heightMap.width);
        }

        public void damage(float i) {
            strength -= i;
            if (strength <= 0) {
                burnFarms();
            }
        }

        public void sprog() {
            strength /= 2;
            boolean leader = leaderHouses.containsValue(this);
            if (leader) {
                leaderHouses.remove(serverPlayer);
            }
            game.peons.addPeon(pos.x, pos.y, strength, serverPlayer, leader);

            changed = true;
        }

        public Peon knockDown() {
            boolean leader = leaderHouses.containsValue(this);
            Peon p = game.peons.addPeon(pos.x, pos.y, strength, serverPlayer, leader);
            strength = 0;
            return p;
        }

        public Peon.State addPeon(Peon p, boolean leader) {
            changed = true;
            if (p.player == player) {
                strength += p.strength;
                if (leader) {
                    leaderHouses.put(serverPlayer, this);
                    changed = true;
                }
                return Peon.State.SETTLED;
            }

            strength -= p.strength;
            if (strength < 0) {
                if (leaderHouses.containsValue(this)) {
                    leaderHouses.remove(serverPlayer);
                    serverPlayer.setPapalMagnet(pos.x,pos.y);
                }
                player = p.player;
                serverPlayer = p.player;
                strength = -strength;
                if (leader) {
                    leaderHouses.put(serverPlayer, this);
                }
                return Peon.State.SETTLED;
            }
            return Peon.State.DEAD;
        }

        void makeLeader() {
            leaderHouses.put(serverPlayer, this);
            changed = true;
        }

        private void burnFarms() {
            int radius = 0;
            if (level > 0) {
                radius = 1;
            }
            if (level > 9) {
                radius = 2;
            }
            if (level == 49) {
                radius = 3;
            }
            int px, py;
            for (px = pos.x - radius; px <= pos.x + radius; px++) {
                for (py = pos.y - radius; py <= pos.y + radius; py++)
                {

                        if (game.heightMap.tileInBounds(px, py) && game.heightMap.getTile(px, py).isFertile) {
                            game.heightMap.setTile(px, py, Tile.BURNT);
                        }

                }
            }
        }

        private void paintmap(byte[][] newmap) {
            if (game.heightMap.getTile(pos.x, pos.y).isFertile) {
                newmap[pos.x][pos.y] = HOUSE;
                int radius = 0;
                if (level > 0) {
                    radius = 1;
                }
                if (level > 9) {
                    radius = 2;
                }
                if (level == 49) {
                    radius = 3;
                }
                int px, py;
                for (px = pos.x - radius; px <= pos.x + radius; px++) {
                    for (py = pos.y - radius; py <= pos.y + radius; py++) //for (int radius = 1; radius <= radiuslimit; radius++) {
                    // for (Point offset : Helpers.rings[radius]) {
                    //  px = pos.x + offset.x;
                    // py = pos.y + offset.y;
                    {
                        if (!(px == pos.x && py == pos.y)) {
                            if (game.heightMap.tileInBounds(px, py) && game.heightMap.getTile(px, py).isFertile) {
                                newmap[px][py] = FARM;
                            }
                        }
                    }
                }
            //}
            //}
            }
        }

        private void setLevel() {
            int oldLevel = level;
            level = calcLevel();
            if (level != oldLevel) {
                changed = true;
            }
        }

        private int calcLevel() {
            int l = countFlatLand(pos.x, pos.y);
            return l;
        }

        private void step(float seconds) {

            float rate = (level + 1);
            float newmana = rate * seconds;
            strength += newmana;
            if (strength > rate * 100.0f) {
                float houseStrength = rate * 100.0f - min(500.0f, rate * 100.0f / 2.0f);
                changed = true;
                boolean leader = leaderHouses.containsValue(this);
                if (leader) {
                    leaderHouses.remove(serverPlayer);
                }
                game.peons.addPeon(pos.x, pos.y, strength - houseStrength, serverPlayer, leader);

                strength = houseStrength;
            }

            if (changed) {
                changed = false;
                hds.add(new HouseUpdate.Detail(id, pos.x, pos.y, serverPlayer, level, strength));
            }
        }
    }
}
