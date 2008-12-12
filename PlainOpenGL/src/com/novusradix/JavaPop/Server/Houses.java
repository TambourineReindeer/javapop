package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.HouseUpdate;
import java.awt.Point;
import java.awt.Rectangle;
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

    public void addHouse(Point p, int team, float strength) {
        synchronized (houses) {
            if (canBuild(p)) {
                houses.add(new House(p, team, strength));
            }
        }
    }

    public boolean canBuild(Point p) {
        if (game.heightMap.inBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getHeight(p) > 0 && game.heightMap.isFlat(p));

        }
        return false;
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
                if (game.heightMap.isFlat(h.pos) && newmap[h.pos.x][h.pos.y] == 0) {
                    h.setLevel();
                    h.paintmap(newmap);
                    h.step(seconds);
                } else {
                    i.remove();
                    game.peons.addPeon(h.pos.x + 0.5f, h.pos.y + 0.5f, h.strength);
                    hds.add(new HouseUpdate.Detail(h.pos, 0, -1));
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
                    game.heightMap.setTexture(new Point(x, y), 2);
                }
            }
        }
    }

    private void unpaint() {
        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTexture(new Point(x, y), 1);
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

                if (game.heightMap.getHeight(p) == h && game.heightMap.isFlat(p)) {
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
                gl.glTranslatef(h.pos.x, h.pos.y, game.heightMap.getHeight(h.pos));

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

        public House(Point p, int team, float strength) {
            changed = true;
            pos = p;
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

            int h = game.heightMap.getHeight(pos);
            for (int radius = 1; radius <= radiuslimit; radius++) {
                for (Point offset : Helpers.rings[radius]) {
                    Point p = new Point(pos.x + offset.x, pos.y + offset.y);
                    if (game.heightMap.getHeight(p) == h && game.heightMap.isFlat(p)) {
                        newmap[p.x][p.y] = FARM + team;
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
