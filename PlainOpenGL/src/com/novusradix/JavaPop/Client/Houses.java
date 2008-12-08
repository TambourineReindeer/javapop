package com.novusradix.JavaPop.Client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.media.opengl.GL;

public class Houses {

    private Game game;
    private int[][] map;
    private Map<Point, House> houses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Point, House>();
    }

    public void updateHouse(Point pos, int team, int level) {
        synchronized(houses){
        if (level < 1) {
            //remove
            if (houses.containsKey(pos)) {
                houses.remove(pos);
            }
        } else {

            houses.put(pos, new House(pos, team, level));
        }
        }
    }

    public boolean canBuild(int x, int y) {
        if (x < 0 || y < 0 || x >= game.heightMap.getWidth() || y >= game.heightMap.getBreadth()) {
            return false;
        }
        return (map[x][y] == EMPTY && game.heightMap.getHeight(x, y) > 0 && game.heightMap.isFlat(x, y));
    }

    public void display(GL gl) {
        synchronized(houses){
        if (houses != null) {
            for (House h : houses.values()) {
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
    }

    public class House {

        private Point pos;
        private int level;
        private int team;

        public House(Point p, int team, int level) {
            map[p.x][p.y] = HOUSE + team;
            pos = (Point) p.clone();
            this.level = level;
            this.team = team;
        }
    }
}
