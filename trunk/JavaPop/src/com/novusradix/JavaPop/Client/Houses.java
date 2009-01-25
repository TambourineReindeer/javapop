package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector3;
import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

public class Houses implements AbstractHouses, GLObject {

    private final Game game;
    private final int[][] map;
    private final Map<Integer, House> houses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;
    private Model houseModel;
    private Model ankhModel;
    private Map<com.novusradix.JavaPop.Player, House> leaderHouses;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Integer, House>();
        try {
            houseModel = new Model(ModelData.fromURL(getClass().getResource("/com/novusradix/JavaPop/models/house1.model")), getClass().getResource("/com/novusradix/JavaPop/textures/house1.png"));
            ankhModel = new Model(ModelData.fromURL(getClass().getResource("/com/novusradix/JavaPop/models/ankh.model")), getClass().getResource("/com/novusradix/JavaPop/textures/marble.png"));
        } catch (IOException ex) {
            Logger.getLogger(Houses.class.getName()).log(Level.SEVERE, null, ex);
        }
        leaderHouses = new HashMap<com.novusradix.JavaPop.Player, House>();
    }

    public void updateHouse(int id, int x, int y, Player p, int level, float strength) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    houses.remove(id);
                }
            } else {
                if (houses.containsKey(id)) {
                    houses.get(id).update(p, level, strength);
                } else {
                    houses.put(id, new House(x, y, p, level, strength));
                }
            }
        }
    }

    public boolean canBuild(int x, int y) {
        if (game.heightMap.tileInBounds(x, y)) {
            return (map[x][y] == EMPTY && game.heightMap.getHeight(x, y) > 0 && game.heightMap.isFlat(x, y));

        }
        return false;
    }
    Vector3 p = new Vector3();
    Matrix4 basis = new Matrix4();

    public void display(GL gl, float time) {
        synchronized (houses) {
            if (houses != null) {
                gl.glUseProgram(0);
                houseModel.prepare(gl);
                for (House h : houses.values()) {

                    p.set(h.pos.x + 0.5f, h.pos.y + 0.5f, game.heightMap.getHeight(h.pos.x, h.pos.y));

                    basis.set(Matrix4.identity);

                    if (h.level > 9) {
                        basis.scale(3.0f, 3.0f, 1.0f);
                    }
                    if (h.level == 49) {
                        basis.scale(1.0f, 1.0f, 2.0f);
                    }

                    gl.glColor3f(1, 1, 1);
                    gl.glEnable(GL.GL_LIGHTING);
                    houseModel.display(p, basis, gl);

                }
                gl.glDisable(GL.GL_LIGHTING);
                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glDisable(GL.GL_TEXTURE_2D);
                gl.glUseProgram(0);
                for (House h : houses.values()) {
                    gl.glMatrixMode(GL.GL_MODELVIEW);

                    gl.glPushMatrix();
                    gl.glTranslatef(h.pos.x + 0.5f, h.pos.y + 0.5f, game.heightMap.getHeight(h.pos.x, h.pos.y));
                    if (h.level > 9) {
                        gl.glScalef(3.0f, 3.0f, 1.0f);
                    }
                    if (h.level == 49) {
                        gl.glScalef(1.0f, 1.0f, 2.0f);
                    }
                    float height = h.strength / h.getMaxStrength();
                    gl.glBegin(GL.GL_TRIANGLES);

                    gl.glColor3f(0.4f, 0.2f, 0.0f);
                    gl.glVertex3f(0.3f, -0.3f, 0f);
                    gl.glVertex3f(0.3f, -0.3f, 1.5f);
                    gl.glVertex3f(0.31f, -0.31f, 0f);
                    gl.glVertex3f(0.3f, -0.3f, 1.5f);
                    gl.glVertex3f(0.31f, -0.31f, 1.5f);
                    gl.glVertex3f(0.31f, -0.31f, 0f);

                    gl.glColor3fv(h.player.getColour(), 0);
                    gl.glVertex3f(0.31f, -0.31f, 1.5f * height - 0.2f);
                    gl.glVertex3f(0.31f, -0.31f, 1.5f * height);
                    gl.glVertex3f(0.4f, -0.4f, 1.5f * height - 0.1f);
                    gl.glEnd();
                    gl.glPopMatrix();
                }
            }
            ankhModel.prepare(gl);
            for (House h : leaderHouses.values()) {
                if (h != null) {
                    basis.set(Matrix4.identity);
                    p.x = h.pos.x + 0.5f;
                    p.y = h.pos.y + 0.5f;
                    p.z = game.heightMap.getHeight(p.x, p.y) + 1.0f;

                    ankhModel.display(p, basis, gl);
                }
            }
        }
    }

    public void init(GL gl) {
        houseModel.init(gl);
        ankhModel.init(gl);
    }

    public void setLeaders(Map<Integer, Integer> leaders) {
        leaderHouses.clear();
        for (Entry<Integer, Integer> e : leaders.entrySet()) {
            leaderHouses.put(game.players.get(e.getKey()), houses.get(e.getValue()));
        }
    }

    public void step(float seconds) {
        synchronized (houses) {
            for (House h : houses.values()) {
                h.step(seconds);
            }
        }
    }

    public class House extends com.novusradix.JavaPop.House {

        public House(int x, int y, Player player, int level, float strength) {
            this.pos = new Point(x, y);

            map[x][y] = HOUSE;
            this.level = level;
            this.player = player;
            this.strength = strength;
        }

        public void update(Player p, int level, float strength) {
            this.player = p;
            this.level = level;
            this.strength = strength;
        }

        private void step(float seconds) {
            strength += seconds * getGrowthRate();
        }
    }
}
