/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Server.Player.Info;
import java.awt.Point;
import javax.media.opengl.GL;

import static java.lang.Math.*;

/**
 *
 * @author mom
 */
public class Player implements GLObject {

    public String name;
    public float[] colour;
    public Point ankh;
    public double mana;
    private Game game;
    private int index;

    public Player(Info i, Game g, int index) {
        update(i);
        game = g;
        this.index = index;
    }

    public void update(Info i) {
        name = i.name;
        colour = i.colour;
        ankh = i.ankh;
        mana = i.mana;
    }

    public void display(GL gl, float time) {
        gl.glPushMatrix();
        gl.glTranslatef(ankh.x + 0.5f, ankh.y + 0.5f, (float) (game.heightMap.getHeight(ankh) + abs(sin(time * 3.0))));
        gl.glRotated(time * 720.0, 0, 0, 1);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glBegin(GL.GL_TRIANGLES);

        gl.glColor3fv(colour, 0);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(-0.5f, 0.5f, 2.0f);
        gl.glVertex3f(0.5f, -0.5f, 2.0f);

        gl.glEnd();

        gl.glLoadIdentity();

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glBegin(GL.GL_QUADS);

        gl.glVertex3f(-0.95f + index * 0.1f, 0.85f, 0.0f);
        gl.glVertex3f(-0.95f + index * 0.1f,(float) (0.85f + max(0,log(mana) * 0.01f)), 0.0f);
        gl.glVertex3f(-0.9f + index * 0.1f, (float) (0.85f + max(0,log(mana) * 0.01f)), 0.0f);
        gl.glVertex3f(-0.9f + index * 0.1f, 0.85f, 0.0f);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    public void init(GL gl) {
    }
}
