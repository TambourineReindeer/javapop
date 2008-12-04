package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Vector2;
import java.awt.Point;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.GL;

public class Peons {

    public Game game;
    private float searchRadius = 0;
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

    public void step() {
        if (peons != null) {
            int status;
            Peon p;
            for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

                p = i.next();
                status = p.step(0.02f);
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
        float arcstep, arc;
        float deltax, deltay;
        Random r = new Random();
        Point dest = new Point();
        dest.x = r.nextInt(game.heightMap.getWidth());
        dest.y = r.nextInt(game.heightMap.getBreadth());

        for (searchRadius = 0; searchRadius < 10; searchRadius += 0.5) {
            arcstep = 1.0f / (2.0f * 3.14159f * searchRadius);
            for (arc = 0; arc < 2.0 * 3.14159; arc += arcstep) {
                deltax = searchRadius * (float) Math.sin(arc);
                deltay = searchRadius * (float) Math.cos(arc);

                if (game.heightMap.getHeight((int) (start.x + 0.5f + deltax), (int) (start.y + 0.5f + deltay)) > 0 
                        && game.heightMap.isFlat((int) (start.x + 0.5f + deltax), (int) (start.y + 0.5f + deltay)) 
                        && game.houses.canBuild((int) (start.x + 0.5f + deltax), (int) (start.y + 0.5f + deltay))) {
                    dest.x = (int) (start.x + 0.5f + deltax);
                    dest.y = (int) (start.y + 0.5f + deltay);
                    searchRadius = 0;
                    return dest;

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

            strength-=seconds;
            if(strength<1)
                return DEAD;
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
