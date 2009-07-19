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

/**
 * The house manager.
 * @author gef
 */
public class Houses implements AbstractHouses, GLObject {

    private final Game game;
    private final int[][] map;
    private final Map<Integer, House> houses;
    private static final int EMPTY = 0;
    private Model houseModel;
    private Model ankhModel;
    private Map<com.novusradix.JavaPop.Player, House> leaderHouses;
/**
 * Creates a new house manager for the specified game. There will be no houses on the map after this call.
 * @param g
 */
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

    /**
     * Call in response to a HouseUpdate message from the server.
     * @param id    The unique ID of the house in question.
     * @param x     The x coordinate of the house. This may potentially change in response to hurricanes, etc.
     * @param y     The y coordinate of the house.
     * @param p     The Player who owns the house. This can change after a battle.
     * @param level The house level. 1 is a small shack, and higher numbers indicate a larger, more luxurious dwelling.
     * @param strength  The strength of the house. This will increase over time, but updates are not sent if it is growing normally.
     * @param infected  True if the house suffers from the plague. Plagued houses do not contribute mana to their player.
     */
    public void updateHouse(int id, int x, int y, Player p, int level, float strength, boolean infected) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    houses.remove(id);
                }
            } else {
                if (houses.containsKey(id)) {
                    houses.get(id).update(p, level, strength, infected);
                } else {
                    houses.put(id, new House(x, y, p, level, strength, infected));
                }
            }
        }
    }

    /**
     * Check whether a house could be built at a specific location
     * @param x The x coordinate of the tile to query
     * @param y the y coordinate of the tile to query
     * @return True if a house can currently be built, according to the client's latest info.
     */
    public boolean canBuild(int x, int y) {
        if (game.heightMap.tileInBounds(x, y)) {
            return (map[x][y] == EMPTY && game.heightMap.getHeight(x, y) > 0 && game.heightMap.isFlat(x, y));
        }
        return false;
    }
    Vector3 p = new Vector3();
    Matrix4 basis = new Matrix4();

    /**
     * Draws all houses on the map
     * @param gl The GL object to use for drawing.
     * @param time Elapsed time, for animation.
     */
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

                    if (h.infected) {
                        gl.glColor3f(0.2f, 0.2f, 0.2f);
                    } else {
                        gl.glColor3fv(h.player.getColour(), 0);
                    }
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

    /**
     * Call whenever the relevant GL object needs to initialise.
     * @param gl
     */
    public void init(GL gl) {
        houseModel.init(gl);
        ankhModel.init(gl);
    }

    /**
     * Call if the server indicates the leaders houses have changed.
     * @param leaders The new map of player IDs to house IDs
     */
    public void setLeaders(Map<Integer, Integer> leaders) {
        leaderHouses.clear();
        for (Entry<Integer, Integer> e : leaders.entrySet()) {
            leaderHouses.put(game.players.get(e.getKey()), houses.get(e.getValue()));
        }
    }

    /**
     * Step all houses through game time. This keeps strength values, etc. in sync with the server.
     * @param seconds
     */
    public void step(float seconds) {
        synchronized (houses) {
            for (House h : houses.values()) {
                h.step(seconds);
            }
        }
    }

    /** Return the house if any at the given coordinates.
     * @param x The x coordinate of the tile in question
     * @param y The y coordinate of the tile in question
     * @return The House object that exists at that point, or null if there is no house there.
     */
    public House getHouse(int x, int y) {
        return houses.get(x + y * game.heightMap.width);
    }

    public class House extends com.novusradix.JavaPop.House {

        private boolean infected;

        public House(int x, int y, Player player, int level, float strength, boolean infected) {
            this.pos = new Point(x, y);
            this.level = level;
            this.player = player;
            this.strength = strength;
            this.infected = infected;
        }

        public void update(Player p, int level, float strength, boolean infected) {
            this.player = p;
            this.level = level;
            this.strength = strength;
            this.infected = infected;
        }

        private void step(float seconds) {
            strength += seconds * getGrowthRate();
        }

        @Override
        public boolean isInfected() {
            return infected;
        }
    }
}
