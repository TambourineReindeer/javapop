package com.novusradix.JavaPop.Client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

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
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(pos)) {
                    houses.remove(pos);
                }
            } else {
                houses.put(pos, new House(pos, team, level));
            }
        }
    }

    public boolean canBuild(Point p) {
        if (game.heightMap.inBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getHeight(p) > 0 && game.heightMap.isFlat(p));

        }
        return false;
    }    

    public void display(GL gl, double time) {
        synchronized (houses) {
            if (houses != null) {
                for (House h : houses.values()) {
                    gl.glPushMatrix();
                    gl.glTranslatef(h.pos.x + 0.5f, h.pos.y + 0.5f, game.heightMap.getHeight(h.pos.x, h.pos.y));
                    if (h.level > 9) {
                        gl.glScalef(3.0f, 3.0f, 1.0f);
                    }
                    if (h.level == 48) {
                        gl.glScalef(1.0f, 1.0f, 2.0f);
                    }
                    gl.glEnable(GL.GL_LIGHTING);
                    gl.glBegin(GL.GL_QUADS);
                    gl.glColor3f(1, 1, 1);
                    gl.glNormal3f(-1, 0, 0);
                    gl.glVertex3f(-0.3f, -0.3f, 0.0f);
                    gl.glVertex3f(-0.3f, 0.3f, 0.0f);
                    gl.glVertex3f(-0.3f, 0.3f, 1.3f);
                    gl.glVertex3f(-0.3f, -0.3f, 1.3f);

                    gl.glNormal3f(1, 0, 0);
                    gl.glVertex3f(0.3f, -0.3f, 0.0f);
                    gl.glVertex3f(0.3f, 0.3f, 0.0f);
                    gl.glVertex3f(0.3f, 0.3f, 1.3f);
                    gl.glVertex3f(0.3f, -0.3f, 1.3f);

                    gl.glNormal3f(0, -1, 0);
                    gl.glVertex3f(-0.3f, -0.3f, 0.0f);
                    gl.glVertex3f(0.3f, -0.3f, 0.0f);
                    gl.glVertex3f(0.3f, -0.3f, 1.3f);
                    gl.glVertex3f(-0.3f, -0.3f, 1.3f);

                    gl.glNormal3f(0, 1, 0);
                    gl.glVertex3f(-0.3f, 0.3f, 0.0f);
                    gl.glVertex3f(0.3f, 0.3f, 0.0f);
                    gl.glVertex3f(0.3f, 0.3f, 1.3f);
                    gl.glVertex3f(-0.3f, 0.3f, 1.3f);

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
