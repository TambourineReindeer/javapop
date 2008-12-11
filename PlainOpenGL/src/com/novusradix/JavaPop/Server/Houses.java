package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.HouseUpdate;
import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;

public class Houses {

    private Game game;
    private int[][] map;
    private int[][] newmap;
    private Vector<House> houses;
    private Vector<HouseUpdate.Detail> hds;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        newmap = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new Vector<House>();
        hds = new Vector<HouseUpdate.Detail>();
    }

    public void addHouse(int x, int y, int team, float strength) {
        synchronized (houses) {
            if (canBuild(x, y)) {
                houses.add(new House(x, y, team, strength));
            }
        }
    }

    public boolean canBuild(int x, int y) {
        if (x < 0 || y < 0 || x >= game.heightMap.getWidth() || y >= game.heightMap.getBreadth()) {
            return false;
        }
        return (map[x][y] == EMPTY && game.heightMap.getHeight(x, y) > 0 && game.heightMap.isFlat(x, y));
    }

    public void step(float seconds) {
        for (int y = 0; y < game.heightMap.getBreadth(); y++) {
            for (int x = 0; x < game.heightMap.getWidth(); x++) {
                newmap[y][x] = 0;
            }
        }
        synchronized (houses) {
            Iterator<House> i = houses.iterator();
            House h;
            for (; i.hasNext();) {
                h = i.next();
                if (game.heightMap.isFlat(h.pos.x, h.pos.y) && newmap[h.pos.x][h.pos.y] == 0) {
                    h.setLevel();
                    h.paintmap(newmap);
                    h.step(seconds);
                } else {
                    i.remove();
                    game.peons.addPeon(h.pos.x, h.pos.y, h.strength);
                    hds.add(new HouseUpdate.Detail(h.pos, 0, -1));
                }
            }
        }
        synchronized (game.heightMap) {
            repaint();
            int[][] t;
            t = map;
            map = newmap;
            newmap = t;
            
        }
        if (!hds.isEmpty()) {
            game.sendAllPlayers(new HouseUpdate(hds));
            hds.clear();
        }
    }

    private void repaint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (newmap[x][y] != map[x][y]) {
                    switch (newmap[x][y]) {
                        case EMPTY:
                            game.heightMap.setTexture(new Point(x, y), 1);
                            break;
                        default:
                            game.heightMap.setTexture(new Point(x, y), 2);
                    }
                }
            }
        }
    }

    private void paint() {
        int x,  y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTexture(new Point(x, y), 2);
                }
            }
        }
    }

    private void unpaint() {
        int x,  y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTexture(new Point(x, y), 1);
                }
            }
        }
    }

    public int countFlatLand(Point pos) {
        int x,  y;
        int flat = 0;
        int h = game.heightMap.getHeight(pos.x, pos.y);
        for (int radius = 1; radius <= 3; radius++) {
            y = -radius;
            for (x = -radius; x < radius; x++) {
                if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                    flat++;
                }
            }
            x = radius;
            for (y = -radius; y < radius; y++) {
                if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                    flat++;
                }
            }
            y = radius;
            for (x = radius; x > -radius; x--) {
                if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                    flat++;
                }
            }
            x = -radius;
            for (y = radius; y > -radius; y--) {
                if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                    flat++;
                }
            }
            if (flat < (2 * radius + 1) * (2 * radius + 1) - 1) {
                break;
            }
        }
        return flat;
    }

    public void display(GL gl) {
        if (houses != null) {
            for (House h : houses) {
                gl.glPushMatrix();
                gl.glTranslatef(h.pos.x, h.pos.y, game.heightMap.getHeight(h.pos.x, h.pos.y));

                gl.glEnable(GL.GL_LIGHTING);
                gl.glBegin(GL.GL_QUADS);
                gl.glColor3f(1, 1, 1);
                gl.glNormal3f(-1, 0, 0);
                gl.glVertex3f(0.2f, 0.2f, 0.0f);
                gl.glVertex3f(0.2f, 0.8f, 0.0f);
                gl.glVertex3f(0.2f, 0.8f, 0.8f);
                gl.glVertex3f(0.2f, 0.2f, 0.8f);

                gl.glNormal3f(1, 0, 0);
                gl.glVertex3f(0.8f, 0.2f, 0.0f);
                gl.glVertex3f(0.8f, 0.8f, 0.0f);
                gl.glVertex3f(0.8f, 0.8f, 0.8f);
                gl.glVertex3f(0.8f, 0.2f, 0.8f);

                gl.glNormal3f(0, -1, 0);
                gl.glVertex3f(0.2f, 0.2f, 0.0f);
                gl.glVertex3f(0.8f, 0.2f, 0.0f);
                gl.glVertex3f(0.8f, 0.2f, 0.8f);
                gl.glVertex3f(0.2f, 0.2f, 0.8f);

                gl.glNormal3f(0, 1, 0);
                gl.glVertex3f(0.2f, 0.8f, 0.0f);
                gl.glVertex3f(0.8f, 0.8f, 0.0f);
                gl.glVertex3f(0.8f, 0.8f, 0.8f);
                gl.glVertex3f(0.2f, 0.8f, 0.8f);

                gl.glEnd();
                gl.glPopMatrix();
            }
        }
    }

    public class House {

        private Point pos;
        private int level;
        private int team;
        private float strength;
        private boolean changed;

        public House(int x, int y, int team, float strength) {
            changed = true;
            pos = new Point(x, y);
            level = 1;
            this.team = team;
            this.strength = strength;
        }

        private void paintmap(int[][] newmap) {
            newmap[pos.x][pos.y] = HOUSE + team;
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
            int x,  y;

            int h = game.heightMap.getHeight(pos.x, pos.y);
            for (int radius = 1; radius <= radiuslimit; radius++) {
                y = -radius;
                for (x = -radius; x < radius; x++) {
                    if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                        newmap[pos.x + x][pos.y + y] = FARM + team;
                    }
                }
                x = radius;
                for (y = -radius; y < radius; y++) {
                    if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                        newmap[pos.x + x][pos.y + y] = FARM + team;
                    }
                }
                y = radius;
                for (x = radius; x > -radius; x--) {
                    if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                        newmap[pos.x + x][pos.y + y] = FARM + team;
                    }
                }
                x = -radius;
                for (y = radius; y > -radius; y--) {
                    if (game.heightMap.getHeight(pos.x + x, pos.y + y) == h && game.heightMap.isFlat(pos.x + x, pos.y + y)) {
                        newmap[pos.x + x][pos.y + y] = FARM + team;
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
                float houseStrength = rate * 100.0f - Math.min(500.0f, rate * 100.0f / 2.0f);
                game.peons.addPeon(pos.x, pos.y, strength - houseStrength);
                strength = houseStrength;
            }

            if (changed) {
                changed = false;
                hds.add(new HouseUpdate.Detail(pos, team, level));
            }
        }
    }
}
