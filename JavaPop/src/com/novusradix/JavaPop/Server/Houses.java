package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HouseUpdate;
import com.novusradix.JavaPop.Server.Peons.Peon;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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
    private Vector<Point> newHouses;
    private SortedMap<Point, House> allHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new byte[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        newmap = new byte[game.heightMap.getWidth()][game.heightMap.getBreadth()];

        allHouses = new TreeMap<Point, House>(new Comparator<Point>() {

            public int compare(Point o1, Point o2) {
                return (o1.x == o2.x ? o1.y - o2.y : o1.x - o2.x);
            }
        });
        newHouses = new Vector<Point>();
        hds = new Vector<HouseUpdate.Detail>();
    }

    public void addHouse(Point p, Player player, float strength) {
        synchronized (allHouses) {
            if (canBuild(p)) {
                allHouses.put(p, new House(p, player, strength));
            }
        }
    }

    public boolean canBuild(Point p) {
        if (game.heightMap.tileInBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getTile(p).isFertile && !newHouses.contains(p));

        }
        return false;
    }

    private Collection<House> affectedHouses(SortedSet<Point> mapChanges) {
        Collection<House> hs = new ArrayList<House>();
        for (House h : allHouses.values()) {
            Point max, min;
            min = new Point(h.pos.x - 3, h.pos.y - 3); //subset from here (inclusive)
            max = new Point(h.pos.x + 3, h.pos.y + 4); //to here (exclusive)
            for (Point p : mapChanges.subSet(min, max)) {
                if ((p.y <= h.pos.y + 3) && (p.y >= h.pos.y - 3)) {
                    hs.add(h);
                    break;
                }
            }
        }
        return hs;
    }

    public void step(float seconds) {
        SortedSet<Point> mapChanges = game.heightMap.takeHouseChanges();
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
                    hds.add(new HouseUpdate.Detail(h.id, h.pos, h.player, -1));
                    continue;
                }
                if (game.heightMap.isFlat(h.pos) && newmap[h.pos.x][h.pos.y] == 0) {
                    h.paintmap(newmap);
                    h.step(seconds);
                } else {
                    i.remove();
                    game.peons.addPeon(h.pos, h.strength, h.player);
                    hds.add(new HouseUpdate.Detail(h.id, h.pos, h.player, -1));
                }
            }
            i = allHouses.values().iterator();
            for (; i.hasNext();) {
                h = i.next();
                if (newmap[h.pos.x][h.pos.y] != HOUSE) {
                    i.remove();
                    if (h.strength > 0) {
                        game.peons.addPeon(h.pos, h.strength, h.player);
                    }
                    hds.add(new HouseUpdate.Detail(h.id, h.pos, h.player, -1));
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
            game.sendAllPlayers(new HouseUpdate(hds));
            hds.clear();
        }
    }

    public Houses.House getHouse(Point p) {
        return allHouses.get(p);
    }

    private void paint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTile(new Point(x, y), Tile.FARM);
                }
            }
        }
    }

    private void unpaint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    if (game.heightMap.getTile(new Point(x, y)).isFertile) {
                        game.heightMap.clearTile(new Point(x, y));
                    }
                }
            }
        }
    }

    private void repaint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (newmap[x][y] != map[x][y]) {
                    if (newmap[x][y] == EMPTY) {
                        if (game.heightMap.getTile(new Point(x, y)).isFertile) {
                            game.heightMap.clearTile(new Point(x, y));
                        }
                    } else {
                        game.heightMap.setTile(new Point(x, y), Tile.FARM);
                    }
                }
            }
        }
    }

    public int countFlatLand(Point pos) {
        int flat = 0;
        for (int radius = 0; radius <=
                3; radius++) {
            for (Point offset : Helpers.rings[radius]) {
                Point p = new Point(pos.x + offset.x, pos.y + offset.y);

                if (game.heightMap.tileInBounds(p)) {
                    Tile tile = game.heightMap.getTile(p);
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
                    d2 =
                            nd2;
                }

            }
        }
        if (nearest != null) {
            return nearest.pos;
        }

        return null;
    }
    private static int nextId;

    public class House {

        private int id;
        private Point pos;
        private int level;
        private Player player;
        private float strength;
        private boolean changed;

        public House(Point p, Player player, float strength) {
            id = nextId++;
            changed = true;
            pos = p;
            level = 1;
            this.player = player;
            this.strength = strength;
            setLevel();
            newHouses.add(p);
        }

        public void damage(float i) {
            strength -= i;
            player.info.mana -= i;
        }

        void addPeon(Peon p) {
            if (p.player == player) {
                strength += p.strength;
                return;
            }

            strength -= p.strength;
            p.player.info.mana -= p.strength;
            player.info.mana -= p.strength;
            if (strength < 0) {
                player.info.mana -= strength;
                player = p.player;
                strength = -strength;
                changed = true;
            }
        }

        private void paintmap(byte[][] newmap) {
            if (game.heightMap.getTile(pos).isFertile) {
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
                Point p = new Point();
                for (int radius = 1; radius <= radiuslimit; radius++) {
                    for (Point offset : Helpers.rings[radius]) {
                        p.x = pos.x + offset.x;
                        p.y = pos.y + offset.y;
                        if (game.heightMap.tileInBounds(p) && game.heightMap.getTile(p).isFertile) {
                            newmap[p.x][p.y] = FARM;
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
            int l = countFlatLand(pos);
            return l;
        }

        private void step(float seconds) {
            float rate = (level + 1);
            float newmana = rate * seconds;
            strength += newmana;
            player.info.mana += newmana;
            if (strength > rate * 100.0f) {
                float houseStrength = rate * 100.0f - min(500.0f, rate * 100.0f / 2.0f);
                game.peons.addPeon(pos, strength - houseStrength, player);
                strength = houseStrength;
            }

            if (changed) {
                changed = false;
                hds.add(new HouseUpdate.Detail(id, pos, player, level));
            }
        }
    }
}
