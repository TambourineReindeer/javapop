package com.novusradix.JavaPop.Client;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;

public class Houses {

    private Game game;
    private int[][] map;
    private Vector<House> houses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new Vector<House>();
    }

    public void addHouse(int x, int y, int team, float strength) {
        if (game.heightMap.isFlat(x, y) && canBuild(x, y)) {
            houses.add(new House(x, y, team, strength));
        }
    }

    public boolean canBuild(int x, int y) {
        return map[x][y] == EMPTY;
    }

    public void step() {
        int[][] newmap;
        newmap = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        Iterator<House> i = houses.iterator();
        House h;
        for (; i.hasNext();) {
            h = i.next();
            if (game.heightMap.isFlat(h.pos.x, h.pos.y) && newmap[h.pos.x][h.pos.y] == 0) {
                h.setLevel();
                h.paintmap(newmap);
                h.step(0.02f);
            } else {
                i.remove();
                game.peons.addPeon(h.pos.x, h.pos.y, h.strength);
            }
        }

        synchronized (game.heightMap) {
            unpaint();

            map = newmap;
            paint();
        }
    }

    private void paint() {


        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTexture(x, y, 2);
                }
            }
        }
    }

    private void unpaint() {


        int x, y;
        for (y = 0; y < game.heightMap.getBreadth(); y++) {
            for (x = 0; x < game.heightMap.getWidth(); x++) {
                if (map[x][y] != EMPTY) {
                    game.heightMap.setTexture(x, y, 1);
                }
            }
        }
    }

    public int countFlatLand(Point pos) {


        int x, y;
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

        public House(int x, int y, int team, float strength) {
            map[x][y] = HOUSE + team;
            pos = new Point(x, y);
            level = 0;
            this.team = team;
            this.strength = strength;
        }

        private void paintmap(int[][] newmap) {
            // TODO Auto-generated method stub
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


            int x, y;

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
            level = calcLevel();
        }

        private int calcLevel() {
            int l = countFlatLand(pos);

            return l;
        }

        private void step(float seconds) {
            
            float rate = (level +1);
            
            strength += rate*seconds;
            if(strength> rate*100)
            {
                float sprogStrength = Math.min(500.0f, strength/2.0f);
                strength -=sprogStrength;
                game.peons.addPeon(pos.x, pos.y, sprogStrength);
                        
            }
        }
    }
}
