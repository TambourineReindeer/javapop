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
public class Player {

    public String name;
    public float[] colour;
    public Point ankh;
    private Game game;

    Player(Info i, Game g) {
        update(i);
        game = g;
    }

    public void update(Info i) {
        name = i.name;
        colour = i.colour;
        ankh = i.ankh;
    }

    public void display(GL gl, double time) {
        gl.glPushMatrix();
        gl.glTranslatef(ankh.x+0.5f, ankh.y+0.5f,(float) (game.heightMap.getHeight(ankh) + abs(sin(time*3.0))));
        gl.glRotated(time*720.0, 0, 0, 1);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glBegin(GL.GL_TRIANGLES);

        gl.glColor3fv(colour, 0);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(-0.5f, 0.5f, 2.0f);
        gl.glVertex3f(0.5f, -0.5f, 2.0f);

        gl.glEnd();
        gl.glPopMatrix();

    }
}
