package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HouseUpdate;
import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import static java.lang.Math.*;

public class Houses {

    private Game game;
    private int[][] map;
    private int[][] newmap;
    private Vector<HouseUpdate.Detail> hds;
    private Vector<Point> newHouses;
    private Collection<House> allHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        newmap = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];

        allHouses = new Vector<House>();
        newHouses = new Vector<Point>();
        hds = new Vector<HouseUpdate.Detail>();
    }

    public void addHouse(Point p, Player player, float strength) {
        synchronized (allHouses) {
            if (canBuild(p)) {
                allHouses.add(new House(p, player, strength));
            }
        }
    }

    public boolean canBuild(Point p) {
        if (game.heightMap.tileInBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getHeight(p) > 0 && game.heightMap.isFlat(p) && !newHouses.contains(p));

        }
        return false;
    }

    public void step(float seconds) {
        for (int y = 0; y < game.heightMap.getBreadth(); y++) {
            for (int x = 0; x < game.heightMap.getWidth(); x++) {
                newmap[y][x] = 0;
            }
        }
        synchronized (allHouses) {
            newHouses.clear();
            Iterator<House> i = allHouses.iterator();
            House h;
            for (; i.hasNext();) {
                h = i.next();
                if (game.heightMap.isFlat(h.pos) && newmap[h.pos.x][h.pos.y] == 0) {
                    h.setLevel();
                    h.paintmap(newmap);
                    h.step(seconds);
                } else {
                    i.remove();
                    game.peons.addPeon(h.pos.x, h.pos.y, h.strength, h.player);
                    hds.add(new HouseUpdate.Detail(h.id, h.pos, h.player, -1));
                }
            }
            i = allHouses.iterator();
            for (; i.hasNext();) {
                h = i.next();
                if (newmap[h.pos.x][h.pos.y] != HOUSE) {
                    i.remove();
                    game.peons.addPeon(h.pos.x, h.pos.y, h.strength, h.player);
                    hds.add(new HouseUpdate.Detail(h.id, h.pos, h.player, -1));
                }
            }
        }

        synchronized (game.heightMap) {
            unpaint();
            int[][] t;
            t = map;
            map = newmap;
            newmap = t;
            paint();
        }
        if (!hds.isEmpty()) {
            game.sendAllPlayers(new HouseUpdate(hds));
            hds.clear();
        }
    }

    private void paint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTile(new Point(x, y), HeightMap.TILE_FARM);
                }
            }
        }
    }

    private void unpaint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTile(new Point(x, y), HeightMap.TILE_LAND);
                }
            }
        }
    }

    public int countFlatLand(Point pos) {
        int flat = 0;
        int h = game.heightMap.getHeight(pos);
        for (int radius = 1; radius <= 3; radius++) {
            for (Point offset : Helpers.rings[radius]) {
                Point p = new Point(pos.x + offset.x, pos.y + offset.y);

                if (game.heightMap.tileInBounds(p) && game.heightMap.getHeight(p) == h && game.heightMap.isFlat(p)) {
                    flat++;
                }
            }
            if (flat < (2 * radius + 1) * (2 * radius + 1) - 1) {
                break;
            }
        }
        return flat;
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
            newHouses.add(p);
        }

        private void paintmap(int[][] newmap) {
            newmap[pos.x][pos.y] = HOUSE;
            int radiuslimit = 0;
            if (level > 0) {
                radiuslimit = 1;
            }
            if (level > 8) {
                radiuslimit = 2;
            }
            if (level == 48) {
                radiuslimit = 3;
            }

            int h = game.heightMap.getHeight(pos);
            for (int radius = 1; radius <= radiuslimit; radius++) {
                for (Point offset : Helpers.rings[radius]) {
                    Point p = new Point(pos.x + offset.x, pos.y + offset.y);
                    if (game.heightMap.tileInBounds(p) && game.heightMap.getHeight(p) == h && game.heightMap.isFlat(p)) {
                        newmap[p.x][p.y] = FARM;
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

            strength += rate * seconds;
            if (strength > rate * 100.0f) {
                float houseStrength = rate * 100.0f - min(500.0f, rate * 100.0f / 2.0f);
                game.peons.addPeon(pos.x, pos.y, strength - houseStrength, player);
                strength = houseStrength;
            }

            if (changed) {
                changed = false;
                hds.add(new HouseUpdate.Detail(id, pos, player, level));
            }
        }
    }
}
