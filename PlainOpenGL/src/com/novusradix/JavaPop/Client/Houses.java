package com.novusradix.JavaPop.Client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

public class Houses implements AbstractHouses, GLObject {

    private Game game;
    private int[][] map;
    private Map<Integer, House> houses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    private XModel house1;
    
    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Integer, House>();
        house1 = new XModel("/com/novusradix/JavaPop/models/house1.x", "/com/novusradix/JavaPop/textures/house1.png");
    }

    public void updateHouse(int id, Point pos, Player p, int level) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    houses.remove(id);
                }
            } else {
                houses.put(id, new House(pos, p, level));
            }
        }
    }

    public boolean canBuild(Point p) {
        if (game.heightMap.tileInBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getHeight(p) > 0 && game.heightMap.isFlat(p));

        }
        return false;
    }

    public void display(GL gl, float time) {
        synchronized (houses) {
            if (houses != null) {
                gl.glUseProgram(0);
                gl.glMatrixMode(GL.GL_MODELVIEW);

                for (House h : houses.values()) {
                    gl.glPushMatrix();
                    gl.glTranslatef(h.pos.x + 0.5f, h.pos.y + 0.5f, game.heightMap.getHeight(h.pos.x, h.pos.y));
                    if (h.level > 9) {
                        gl.glScalef(3.0f, 3.0f, 1.0f);
                    }
                    if (h.level == 48) {
                        gl.glScalef(1.0f, 1.0f, 2.0f);
                    }
                    gl.glColor3f(1, 1, 1);
                    gl.glEnable(GL.GL_LIGHTING);
                    house1.display(gl, time);
                    gl.glDisable(GL.GL_LIGHTING);
                    gl.glDisable(GL.GL_TEXTURE_2D);
                
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

        private Point pos;
        private int level;
        private Player player;

        public House(Point p, Player player, int level) {
            map[p.x][p.y] = HOUSE;
            pos = (Point) p.clone();
            this.level = level;
            this.player = player;
        }
    }

    public void init(GL gl) {
        house1.init(gl);
    }
}
