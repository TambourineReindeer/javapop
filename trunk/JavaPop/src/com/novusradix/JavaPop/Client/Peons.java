package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Messaging.PeonUpdate.Detail;
import com.novusradix.JavaPop.Server.Peons.State;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.media.opengl.GL;

import static java.lang.Math.*;

public class Peons implements GLObject {

    public Game game;
    private Map<Integer, Peon> peons;
    private XModel peonModel;
    private boolean firstPeon=true;
    public Peons(Game g) {
        game = g;
        peons = new HashMap<Integer, Peon>();
        if (g.getClass() == com.novusradix.JavaPop.Client.Game.class) {
            peonModel = new XModel("/com/novusradix/JavaPop/models/peon5.x", "/com/novusradix/JavaPop/textures/peon.png");
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
                    if(firstPeon && game.players.get(d.playerId) == game.me)
                    {
                        firstPeon = false;
                        game.lookAt(new Point((int)d.pos.x, (int)d.pos.y));
                    }
                }
            }
        }
    } 
    
    public void display(GL gl, float time) {
        synchronized (peons) {
            for (Peon p : peons.values()) {
                p.display(gl, time);
                
            }
        }
    }

    public void init(GL gl) {
        peonModel.init(gl);
    }

    void step(float seconds) {
        synchronized (peons) {
            for (Peon p : peons.values()) {
                p.step(seconds);
            }
        }
    }

    private class Peon {
        private Vector2 pos;
        private Point dest;
        private float dx,  dy;
        private State state;
        private Player player;
        private Matrix4 basis;

        public Peon(Detail d) {
            pos = d.pos;
            dest = d.dest;
            dx = d.dx;
            dy = d.dy;
            state = d.state;
            player = game.players.get(d.playerId);
            calcBasis();
        }

        public void Update(Detail d) {
            pos = d.pos;
            dest = d.dest;
            dx = d.dx;
            dy = d.dy;
            state = d.state;
            calcBasis();
        }

        private void display(GL gl, float time) {
            time += hashCode() % 10000;
            Vector3 p = new Vector3(pos.x, pos.y, game.heightMap.getHeight(pos.x, pos.y));
            switch (state) {
                case DROWNING:
                    p.z+= (float) abs((float) sin(time * 4.0f) / 2.0f + 0.1f);
                default:
            }
            gl.glDisable(GL.GL_LIGHTING);
            gl.glColor3fv(player.colour, 0);
               
            peonModel.display(p, basis, gl, time);           
        }

        public void step(float seconds) {
            switch (state) {
                case WALKING:
                case WANDER:
                    //if already reached destination, wait there - there'll be another message along shortly!
                    if (signum(pos.x - (dest.x+0.5f)) == signum(dx) || signum(pos.y - (dest.y+0.5f)) == signum(dy)) {
                        break;
                    }
                    pos.x += seconds * dx;
                    pos.y += seconds * dy;
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
            Vector3 up, left;
            up = new Vector3(0, 0, 1);
            left = new Vector3();
            left.cross(front, up);
            basis = new Matrix4(front, left, up);
        }
    }
}