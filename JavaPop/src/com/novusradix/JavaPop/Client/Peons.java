package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Messaging.PeonUpdate.Detail;
import com.novusradix.JavaPop.Server.Peons.State;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.media.opengl.GL;

import static java.lang.Math.*;

public class Peons implements GLObject {

    public Game game;
    private Map<Integer, Peon> peons;
    private XModel peonModel,  ankhModel;
    private boolean firstPeon = true;
    Map<Player, Peon> leaders;

    public Peons(Game g) {
        game = g;
        peons = new HashMap<Integer, Peon>();
        leaders = new HashMap<Player, Peon>();
        if (g.getClass() == com.novusradix.JavaPop.Client.Game.class) {
            peonModel = new XModel("/com/novusradix/JavaPop/models/peon5.x", "/com/novusradix/JavaPop/textures/peon.png");
            ankhModel = new XModel("/com/novusradix/JavaPop/models/ankh.x", "/com/novusradix/JavaPop/textures/marble.png");
        }
    }

    public void Update(Detail d) {
        synchronized (peons) {
            if (peons.containsKey(d.id)) {
                if (d.state == State.DEAD || d.state == State.SETTLED) {
                    peons.remove(d.id);
                } else {
                    peons.get(d.id).Update(d);
                }
            } else {
                if (!(d.state == State.DEAD || d.state == State.SETTLED)) {
                    peons.put(d.id, new Peon(d));
                    if (firstPeon && game.players.get(d.playerId) == game.me) {
                        firstPeon = false;
                        game.lookAt(new Point((int) d.posx, (int) d.posy));
                    }
                }
            }
        }
    }

    public void display(GL gl, float time) {
        float t;
        Vector3 v = new Vector3();
        gl.glDisable(GL.GL_LIGHTING);
        peonModel.prepare(gl);
        synchronized (peons) {
            for (Peon p : peons.values()) {
                t = time + hashCode() % 10000;
                v.set(p.posx, p.posy, game.heightMap.getHeight(p.posx, p.posy));
                if (p.state == State.DROWNING) {
                    v.z += (float) abs((float) sin(t * 4.0f) / 2.0f + 0.1f);
                }
                gl.glColor3fv(p.player.colour, 0);
                peonModel.display(v, p.basis, gl, time);
            }
        }

        ankhModel.prepare(gl);
               
        synchronized (leaders) {
            for (Peon p : leaders.values()) {
                if (p != null) {
                    Vector3 pos = new Vector3();
                    Matrix4 basis = new Matrix4(Matrix4.identity);
                    pos.x = p.posx + 0.5f;
                    pos.y = p.posy + 0.5f;
                    pos.z = game.heightMap.getHeight(pos.x, pos.y) + 1.0f;

                    ankhModel.display(pos, basis, gl, time);
                }

            }
        }
    }

    public void init(GL gl) {
        peonModel.init(gl);
        ankhModel.init(gl);
    }

    public void setLeaders(Map<Integer, Integer> leadermap) {
        synchronized (leaders) {
            leaders.clear();
            for (Entry<Integer, Integer> e : leadermap.entrySet()) {
                leaders.put(game.players.get(e.getKey()), peons.get(e.getValue()));
            }

        }
    }

    void step(float seconds) {
        synchronized (peons) {
            for (Peon p : peons.values()) {
                p.step(seconds);
            }
        }
    }

    private class Peon {

        float posx, posy;
        private int destx,  desty;
        private float dx,  dy;
        State state;
        Player player;
        Matrix4 basis;

        public Peon(Detail d) {
            posx = d.posx;
            posy = d.posy;
            destx = d.destx;
            desty = d.desty;
            dx = d.dx;
            dy = d.dy;
            state = d.state;
            player = game.players.get(d.playerId);
            basis = new Matrix4();
            calcBasis();
        }

        public void Update(Detail d) {
            posx = d.posx;
            posy = d.posy;
            destx = d.destx;
            desty = d.desty;
            dx = d.dx;
            dy = d.dy;
            state = d.state;
            calcBasis();
        }

        public void step(float seconds) {
            switch (state) {
                case WALKING:
                case WANDER:
                    //if already reached destination, wait there - there'll be another message along shortly!
                    if (reachedDest()) {
                        break;
                    }
                    posx += seconds * dx;
                    posy += seconds * dy;
                    break;
            }
        }

        private void calcBasis() {
            Vector3 front = new Vector3(dx, dy, 0);
            if (front.length() == 0) {
                front.x = -1;
                front.y = -1;
            }
            front.normalize();
            Vector3 up,
                    left;
            up = new Vector3(0, 0, 1);
            left = new Vector3();
            left.cross(front, up);
            basis.setBasis(front, left, up);

        }

        private boolean reachedDest() {
            float ex = posx - (destx + 0.5f);
            float ey = posy - (desty + 0.5f);
            return ((abs(ex) < 0.1 && abs(ey) < 0.1));

        }
    }
}