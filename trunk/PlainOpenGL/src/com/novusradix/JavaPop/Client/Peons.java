package com.novusradix.JavaPop.Client;

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

    public void display(GL gl) {
        for (Peon p : peons) {
            gl.glPushMatrix();
            gl.glTranslatef(p.pos.x, p.pos.y, game.heightMap.getHeight(p.pos.x, p.pos.y));
            p.display(gl);
            gl.glPopMatrix();
        }
    }

   
    private class Peon {

        public Vector2 pos;
        public float strength;
        private Point dest; // destination to walk to.

        public Peon(float x, float y, float strength) {
            pos = new Vector2(x, y);
            this.strength = strength;
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
