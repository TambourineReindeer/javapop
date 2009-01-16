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

public class Houses {

    private Game game;
    private byte[][] map;
    private byte[][] newmap;
    private Vector<HouseUpdate.Detail> hds;
    private Vector<Integer> newHouses;
    private SortedMap<Integer, House> allHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;
    private Map<Player, House> leaderHouses;

    public Houses(Game g) {
        game = g;
        map = new byte[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        newmap = new byte[game.heightMap.getWidth()][game.heightMap.getBreadth()];

        allHouses = new TreeMap<Integer, House>();
        newHouses = new Vector<Integer>();
        hds = new Vector<HouseUpdate.Detail>();
        leaderHouses = new HashMap<Player, House>();
    }

    public void addHouse(int x, int y, Player player, float strength, boolean leader) {
        synchronized (allHouses) {
            if (canBuild(x, y)) {
                House h = new House(x, y, player, strength);
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

    private Collection<House> affectedHouses(SortedSet<Integer> mapChanges) {
        Collection<House> hs = new ArrayList<House>();
        for (House h : allHouses.values()) {
            int max, min, y;
            min = h.pos.x - 3 + (h.pos.y - 3) * game.heightMap.width; //subset from here (inclusive)
            max = h.pos.x + 3 + (h.pos.y + 4) * game.heightMap.width; //to here (exclusive)
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
        SortedSet<Integer> mapChanges = game.heightMap.takeHouseChanges();
        Collection<House> hs = affectedHouses(mapChanges);
        for (House h : hs) {
            h.setLevel();
        }
        for (int y = 0; y < game.heightMap.getBreadth(); y++) {
            for (int x = 0; x < game.heightMap.getWidth(); x++) {
                newmap[y][x] = 0;
            }
        }
        synchronized (allHouses) {
            newHouses.clear();
            Iterator<House> i = allHouses.values().iterator();
            House h;
            for (; i.hasNext();) {
                h = i.next();
                if (h.strength < 0) {
                    i.remove();
                    hds.add(new HouseUpdate.Detail(h.id, h.pos.x, h.pos.y, h.player, -1));
                    continue;
                }
                if (game.heightMap.isFlat(h.pos.x, h.pos.y) && newmap[h.pos.x][h.pos.y] == 0) {
                    h.paintmap(newmap);
                    h.step(seconds);
                } else {
                    i.remove();
                    game.peons.addPeon(h.pos, h.strength, h.player, leaderHouses.containsValue(h));
                    if (leaderHouses.containsValue(h)) {
                        leaderHouses.remove(h.player);
                    }
                    hds.add(new HouseUpdate.Detail(h.id, h.pos.x, h.pos.y, h.player, -1));
                }
            }
            i = allHouses.values().iterator();
            for (; i.hasNext();) {
                h = i.next();
                if (newmap[h.pos.x][h.pos.y] != HOUSE) {
                    i.remove();
                    if (h.strength > 0) {
                        game.peons.addPeon(h.pos, h.strength, h.player, leaderHouses.containsValue(h));
                    }
                    if (leaderHouses.containsValue(h)) {
                        leaderHouses.remove(h.player);
                    }
                    hds.add(new HouseUpdate.Detail(h.id, h.pos.x, h.pos.y, h.player, -1));
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

    public Houses.House getHouse(int x, int y) {
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

    public Point nearestHouse(
            Point p, Set<Player> players) {
        int d2 = game.heightMap.getWidth() * game.heightMap.getBreadth() + 1;
        House nearest = null;
        for (House h : allHouses.values()) {
            int nd2 = (p.x - h.pos.x) * (p.x - h.pos.x) + (p.y - h.pos.y) * (p.y - h.pos.y);
            if (nd2 < d2) {
                if (players.contains(h.player)) {
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
    private static int nextId = 1;

    public class House {

        public int id;
        private Point pos;
        private int level;
        private Player player;
        private float strength;
        private boolean changed;

        public House(int x, int y, Player player, float strength) {
            id = nextId++;
            changed = true;
            pos = new Point(x, y);
            level = 1;
            this.player = player;
            this.strength = strength;
            setLevel();
            newHouses.add(x + y * game.heightMap.width);
        }

        public void damage(float i) {
            strength -= i;
            player.info.mana -= i;
        }

        Peons.State addPeon(Peon p, boolean leader) {
            if (p.player == player) {
                strength += p.strength;
                if (leader) {
                    leaderHouses.put(player, this);
                    changed = true;
                }
                return Peons.State.SETTLED;
            }

            strength -= p.strength;
            p.player.info.mana -= p.strength;
            player.info.mana -= p.strength;
            if (strength < 0) {
                if (leaderHouses.containsValue(this)) {
                    leaderHouses.remove(player);
                    player.info.ankh.setLocation(this.pos);
                }
                player.info.mana -= strength;
                player = p.player;
                strength = -strength;
                changed = true;
                if (leader) {
                    leaderHouses.put(player, this);
                }
                changed = true;
                return Peons.State.SETTLED;
            }
            return Peons.State.DEAD;
        }

        void makeLeader() {
            leaderHouses.put(this.player, this);
            changed = true;
        }

        private void paintmap(byte[][] newmap) {
            if (game.heightMap.getTile(pos.x, pos.y).isFertile) {
                newmap[pos.x][pos.y] = HOUSE;
                int radiuslimit = 0;
                if (level > 0) {
                    radiuslimit = 1;
                }
                if (level > 9) {
                    radiuslimit = 2;
                }
                if (level == 49) {
                    radiuslimit = 3;
                }
                int px, py;
                for (int radius = 1; radius <= radiuslimit; radius++) {
                    for (Point offset : Helpers.rings[radius]) {
                        px = pos.x + offset.x;
                        py = pos.y + offset.y;
                        if (game.heightMap.tileInBounds(px, py) && game.heightMap.getTile(px, py).isFertile) {
                            newmap[px][py] = FARM;
                        }
                    }
                }
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
            player.info.mana += newmana;
            if (strength > rate * 100.0f) {
                float houseStrength = rate * 100.0f - min(500.0f, rate * 100.0f / 2.0f);
                game.peons.addPeon(pos, strength - houseStrength, player, leaderHouses.containsValue(this));
                if (leaderHouses.containsValue(this)) {
                    leaderHouses.remove(player);
                    changed = true;
                }
                strength = houseStrength;
            }

            if (changed) {
                changed = false;
                hds.add(new HouseUpdate.Detail(id, pos.x, pos.y, player, level));
            }
        }
    }
}
