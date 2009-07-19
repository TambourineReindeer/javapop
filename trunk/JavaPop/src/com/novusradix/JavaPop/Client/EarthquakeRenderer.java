package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector3;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

/**
 * This class draws the earthquake effects on the client.
 * @author gef
 */
public class EarthquakeRenderer implements GLObject {

    private final HashMap<Integer, QuakeTile> tiles;
    private Game g;
    private int stride;
    private Model side,  corner;
    private static Vector3 pos = new Vector3();
    private static Matrix4 basis = new Matrix4();
    private Matrix4 sideways;

    public EarthquakeRenderer(Game g, HeightMap h) {
        tiles = new HashMap<Integer, QuakeTile>();
        stride = h.width;
        sideways = new Matrix4(new Vector3(0, 1, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1));
        this.g = g;
    }

    public void addTile(int x, int y, Direction in, Direction out) {
        QuakeTile t;
        int index = x + y * stride;
        if (tiles.containsKey(index)) {
            t = tiles.get(index);
        } else {
            t = new QuakeTile(x, y);
            tiles.put(index, t);
        }
        if (in != null) {
            t.addCut(true, in);
        }
        if (out != null) {
            t.addCut(false, out);
        }
    }

    public void removeTile(int x, int y) {
        QuakeTile t;
        synchronized (tiles) {
            t = tiles.remove(x + y * stride);
        }
        if (t != null) {
            t.removeNeighbourCuts();
        }
    }

    public void display(GL gl, float time) {
        side.prepare(gl);
        synchronized (tiles) {
            for (QuakeTile t : tiles.values()) {
                t.displaysides(gl, time);
            }
        }
        corner.prepare(gl);
        synchronized (tiles) {
            for (QuakeTile t : tiles.values()) {
                t.displaycorners(gl, time);
            }
        }
    }

    public void init(GL gl) {

        try {
            side = g.modelFactory.getModel(getClass().getResource("/com/novusradix/JavaPop/models/earthquakeedge.model"), getClass().getResource("/com/novusradix/JavaPop/textures/tex.png"));
            corner = g.modelFactory.getModel(getClass().getResource("/com/novusradix/JavaPop/models/earthquakecorner.model"), getClass().getResource("/com/novusradix/JavaPop/textures/tex.png"));
        } catch (IOException ex) {
            Logger.getLogger(EarthquakeRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class QuakeTile {

        int x, y;
        boolean[] cuts;
        float firstRendered;

        public QuakeTile(int px, int py) {
            x = px;
            y = py;
            cuts = new boolean[4];
            firstRendered = 0;
        }

        public void addCut(boolean in, Direction d) {
            if (in) {
                cuts[(d.ordinal() + 2) % 4] = true;
            } else {
                cuts[d.ordinal()] = true;
            }
        }

        public void removeCut(Direction d) {
            cuts[d.ordinal()] = false;
        }

        public void displaysides(GL gl, float time) {
            if (firstRendered == 0) {
                firstRendered = time;
            }
            float scale = Math.max(0.2f, Math.min(1.0f, (10.0f + firstRendered - time)));

            Direction d;
            for (int n = 0; n < 4; n++) {
                if (!cuts[n]) {
                    d = Direction.directions[n];
                    switch (d) {
                        case WEST:
                            pos.set(x, y, g.heightMap.getHeight(x, y));
                            basis.set(Matrix4.identity);
                            basis.scale(scale, 1, 1);
                            side.display(pos, basis, gl);
                            break;
                        case EAST:
                            pos.set(x + 1, y, g.heightMap.getHeight(x, y));
                            basis.set(Matrix4.identity);
                            basis.scale(-scale, 1, 1);
                            side.display(pos, basis, gl);
                            break;
                        case SOUTH:
                            pos.set(x, y, g.heightMap.getHeight(x, y));
                            basis.set(sideways);
                            basis.scale(1, scale, 1);
                            side.display(pos, basis, gl);
                            break;
                        case NORTH:
                            pos.set(x, y + 1, g.heightMap.getHeight(x, y));
                            basis.set(sideways);
                            basis.scale(1, -scale, 1);
                            side.display(pos, basis, gl);
                            break;

                    }
                }
            }
        }

        public void displaycorners(GL gl, float time) {
            float scale = Math.max(0.2f, Math.min(1.0f, (10.0f + firstRendered - time)));

            Direction d;
            for (int n = 0; n < 4; n++) {
                if (cuts[n] && cuts[(n + 1) % 4]) {
                    d = Direction.directions[n];
                    switch (d) {
                        case SOUTH: //southwest corner
                            pos.set(x, y, g.heightMap.getHeight(x, y));
                            basis.set(Matrix4.identity);
                            basis.scale(scale, scale, 1);
                            side.display(pos, basis, gl);
                            break;
                        case WEST: //northwest corner
                            pos.set(x, y + 1, g.heightMap.getHeight(x, y));
                            basis.set(Matrix4.identity);
                            basis.scale(scale, -scale, 1);
                            side.display(pos, basis, gl);
                            break;
                        case NORTH: //northeast
                            pos.set(x + 1, y + 1, g.heightMap.getHeight(x, y));
                            basis.set(Matrix4.identity);
                            basis.scale(-scale, -scale, 1);
                            side.display(pos, basis, gl);
                            break;
                        case EAST:
                            pos.set(x + 1, y, g.heightMap.getHeight(x, y));
                            basis.set(Matrix4.identity);
                            basis.scale(-scale, scale, 1);
                            side.display(pos, basis, gl);
                            break;
                    }
                }
            }
        }

        private void removeNeighbourCuts() {
            int px, py;
            for (int n = 0; n < 4; n++) {
                if (cuts[n]) {
                    px = x + Direction.directions[n].dx;
                    py = y + Direction.directions[n].dy;
                    if (tiles.containsKey(px + stride * py)) {
                        tiles.get(px + stride * py).removeCut(Direction.directions[(n + 2) % 4]);
                    }
                }
            }
        }
    }
}
