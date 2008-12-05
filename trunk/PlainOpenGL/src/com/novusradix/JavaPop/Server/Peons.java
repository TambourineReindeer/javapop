package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.Vector2;
import java.awt.Point;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.GL;

public class Peons {

    public Game game;
    public static final int ALIVE = 0;
    public static final int DEAD = 1;
    public static final int SETTLED = 3;
    private static Vector<Peon> peons;

    public Peons(Game g) {
        game = g;
        peons = new Vector<Peon>();
    }

    public void addPeon(float x, float y, float strength) {
        peons.add(new Peon(x, y, strength));
    }

    public void step(float seconds) {
        if (peons != null) {
            int status;
            Peon p;
            for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

                p = i.next();
                status = p.step(seconds);
                if (status == DEAD) {
                    i.remove();
                }

                if (status == SETTLED) {
                    if (game.houses.canBuild((int) p.pos.x, (int) p.pos.y)) {
                        i.remove();
                        game.houses.addHouse((int) p.pos.x, (int) p.pos.y, 1, p.strength);
                    }
                }
            }
        }
    }

    public void display(GL gl) {
        for (Peon p : peons) {
            gl.glPushMatrix();
            gl.glTranslatef(p.pos.x, p.pos.y, game.heightMap.getHeight(p.pos.x, p.pos.y));
            p.display(gl);
            gl.glPopMatrix();
        }
    }

    private Point findFlatLand(Vector2 start) {
        // TODO Auto-generated method stub
        Random r = new Random();
        Point dest = new Point();
        dest.x = r.nextInt(game.heightMap.getWidth());
        dest.y = r.nextInt(game.heightMap.getBreadth());
        int x, y;
        for (Point[] ring : Helpers.rings) {
            for (Point offset : ring) {
                x = (int) start.x + offset.x;
                y = (int) start.y + offset.y;
                if (x >= 0 && x < game.heightMap.getWidth() && y >= 0 && y < game.heightMap.getBreadth()) {
                    if (game.houses.canBuild(x, y)) {
                        dest.x = (int) start.x + offset.x;
                        dest.y = (int) start.y + offset.y;
                        return dest;
                    }
                }
            }
        }
        return dest;
    }

    private class Peon {

        public Vector2 pos;
        public float strength;
        private Point dest; // destination to walk to.

        public Peon(float x, float y, float strength) {
            pos = new Vector2(x, y);
            this.strength = strength;
        }

        private int step(float seconds) {
            // returns a peon status, e.g. DEAD

            // what can a peon do?
            // drown, die of exhaustion, settle down?

            // drown?
            int x1, y1;

            x1 = (int) Math.floor(pos.x);
            y1 = (int) Math.floor(pos.y);

            strength -= seconds;
            if (strength < 1) {
                return DEAD;
            }
            if (game.heightMap.isFlat(x1, y1)) {
                if (game.heightMap.getHeight(x1, y1) == 0) {
                    // you're drowning
                    // increment a drowning clock and PREPARE TO DIE
                    return DEAD;
                } else {
                    // we're on flat ground
                    if (game.houses.canBuild(x1, y1)) {
                        return SETTLED;
                    }
                }
            }
            // We're on a hill or farm of some sort. Find a flat place to live.
            dest = findFlatLand(pos);

            float fdx = dest.x + 0.5f - pos.x;
            float fdy = dest.y + 0.5f - pos.y;
            float dist = (float) Math.sqrt(fdx * fdx + fdy * fdy);

            fdx = seconds * fdx / dist;
            fdy = seconds * fdy / dist;

            pos.x += fdx;
            pos.y += fdy;

            return ALIVE;
        }

        private void display(GL gl) {
            gl.glBegin(GL.GL_TRIANGLES);
            gl.glColor3f(0, 0, 1);

            gl.glVertex3f(0, 0, 0.3f);
            gl.glVertex3f(0.1f, -0.1f, 0);
            gl.glVertex3f(-0.1f, +0.1f, 0);

            gl.glEnd();

        }
    }
}
