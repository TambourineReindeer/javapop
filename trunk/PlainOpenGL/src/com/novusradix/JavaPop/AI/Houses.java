package com.novusradix.JavaPop.AI;

import com.novusradix.JavaPop.Client.*;
import com.novusradix.JavaPop.Math.MultiMap;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

public class Houses implements AbstractHouses {

    private Game game;
    private int[][] map;
    private Map<Integer, House> houses;
    private MultiMap<Player, House> playerHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Integer, House>();
        playerHouses = new MultiMap<Player, House>();
    }

    public Collection<House> getHouses(Player p) {
        return playerHouses.get(p);
    }

    public void updateHouse(int id, Point pos, Player p, int level) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    playerHouses.remove(p, houses.get(id));
                    houses.remove(id);
                }

            } else {
                if (houses.containsKey(id)) {
                    houses.get(id).level = level;
                } else {
                    House h = new House(pos, p, level);
                    houses.put(id, h);
                    playerHouses.put(p, h);
                }
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
                    gl.glUseProgram(0);
                    gl.glDisable(GL.GL_TEXTURE_2D);
                    gl.glEnable(GL.GL_LIGHTING);
                    gl.glColor3f(1, 1, 1);
                    gl.glBegin(GL.GL_QUADS);

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
                    gl.glDisable(GL.GL_LIGHTING);
                    gl.glColor3fv(h.player.colour, 0);
                    gl.glBegin(GL.GL_TRIANGLES);
                    gl.glVertex3f(0.3f, -0.3f, 1.3f);
                    gl.glVertex3f(0.3f, -0.3f, 1.5f);
                    gl.glVertex3f(0.4f, -0.4f, 1.4f);
                    gl.glEnd();
                    gl.glPopMatrix();
                }
            }
        }
    }

    public class House {

        public Point pos;
        public int level;
        public Player player;

        public House(Point p, Player player, int level) {
            map[p.x][p.y] = HOUSE;
            pos = (Point) p.clone();
            this.level = level;
            this.player = player;
        }
    }
}
