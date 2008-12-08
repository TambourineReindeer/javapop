package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import java.awt.Point;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.GL;

public class Peons {

    public Game game;

    public enum State {

        ALIVE, DEAD, SETTLED, WALKING, DROWNING
    };
    private Vector<Peon> peons;
    private int nextId = 0;

    public Peons(Game g) {
        game = g;
        peons = new Vector<Peon>();
    }

    public void addPeon(float x, float y, float strength) {
        peons.add(new Peon(x, y, strength));
    }

    public void step(float seconds) {
        if (peons != null) {
            Peon p;
            Vector<PeonUpdate.Detail> pds = new Vector<PeonUpdate.Detail>();
            PeonUpdate.Detail pd;
            for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

                p = i.next();

                pd = p.step(seconds);
                if (pd != null) {
                    pds.add(pd);
                }
                switch (p.state) {
                    case DEAD:
                        i.remove();
                        break;
                    case SETTLED:
                        i.remove();
                        game.houses.addHouse((int) p.pos.x, (int) p.pos.y, 1, p.strength);
                        break;
                }
            }
            if (pds.size() > 0) {
                game.sendAllPlayers(new PeonUpdate(pds));
                pds.clear();
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

        public int id;
        public Vector2 pos;
        public float strength;
        private Point dest; // destination to walk to.
        private State state;
        private float dx,  dy;

        public Peon(float x, float y, float strength) {
            id = nextId++;
            pos = new Vector2(x, y);
            this.strength = strength;
            state = State.ALIVE;
        }

        private PeonUpdate.Detail step(float seconds) {
            // what can a peon do?
            // drown, die of exhaustion, settle down?

            // drown?
            int x1, y1;

            x1 = (int) Math.floor(pos.x);
            y1 = (int) Math.floor(pos.y);


            strength -= seconds;
            if (strength < 1) {
                state = State.DEAD;
                return new PeonUpdate.Detail(id, state, pos, dest, dx, dy);
            }

            switch (state) {
                case WALKING:
                    if (game.heightMap.isFlat(x1, y1)) {
                        if (game.heightMap.getHeight(x1, y1) == 0) {
                            // you're drowning
                            // increment a drowning clock and PREPARE TO DIE
                            state = State.DROWNING;
                            return new PeonUpdate.Detail(id, state, pos, dest, dx, dy);
                        }
                    }
                    if (x1 == dest.x && y1 == dest.y) {
                        //Yay we're here
                        state = State.ALIVE;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy);
                    }

                    pos.x += seconds * dx;
                    pos.y += seconds * dy;
                    return null;

                case ALIVE:
                    if (game.houses.canBuild(x1, y1)) {
                        state = State.SETTLED;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy);
                    }

                    dest = findFlatLand(pos);

                    dx = dest.x + 0.5f - pos.x;
                    dy = dest.y + 0.5f - pos.y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    dx = dx / dist;
                    dy = dy / dist;
                    state = State.WALKING;
                    return new PeonUpdate.Detail(id, state, pos, dest, dx, dy);
                case DROWNING:
                    if (!(game.heightMap.getHeight(x1, y1) == 0 && game.heightMap.isFlat(x1, y1))) {
                        state = State.ALIVE;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy);
                    }
                    strength -= 10.0f * seconds;
                    return null;
            }
            return null;
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
