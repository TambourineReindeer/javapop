package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Server.ServerPlayer.Info;
import java.awt.Point;
import javax.media.opengl.GL;

import static java.lang.Math.*;

/**
 *
 * @author gef
 */
public class Player implements GLObject,com.novusradix.JavaPop.Player  {

    public String name;
    public float[] colour;
    public Point ankh;
    public double mana;
    public  final int id;
    private final Game game;
    private final int index;
    
    public Player(Info i, Game g, int index) {
        game = g;
        ankh = new Point();
        this.index = index;
        id = i.id;
        update(i);
    }

    public void update(Info i) {
        name = i.name;
        colour = i.colour;
        ankh.setLocation(i.ankh);
        mana = i.mana;
    }

    public void display(GL gl, float time) {
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(ankh.x + 0.5f, ankh.y + 0.5f, (float) (game.heightMap.getHeight(ankh.x, ankh.y) + abs(sin(time * 3.0))));
        gl.glRotated(time * 720.0, 0, 0, 1);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glUseProgram(0);

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
        gl.glBegin(GL.GL_QUADS);

        gl.glVertex3f(-0.95f + index * 0.1f, 0.85f, 0.0f);
        gl.glVertex3f(-0.95f + index * 0.1f, (float) (0.85f + max(0, log(mana) * 0.01f)), 0.0f);
        gl.glVertex3f(-0.9f + index * 0.1f, (float) (0.85f + max(0, log(mana) * 0.01f)), 0.0f);
        gl.glVertex3f(-0.9f + index * 0.1f, 0.85f, 0.0f);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    public void init(GL gl) {
    }

    public float[] getColour() {
        return colour;
    }

    public String getName() {
        return name;
    }

    public Point getPapalMagnet() {
        return ankh;
    }

    public double getMana() {
        return mana;
    }
}
