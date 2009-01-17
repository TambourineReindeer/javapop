package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.Random;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class BasaltEffect extends Effect {

    Point pos;
    transient Direction direction;
    transient int age = 0;

    public BasaltEffect(Point o, Direction d) {
        pos = o;
        direction = d;
        age = 0;
    }

    @Override
    public void execute(Game g) {
        age++;
        if (age % 10 == 0) {
            if (age == 100) {
                g.deleteEffect(this);
                return;
            }
            if (g.heightMap.getTile(pos.x, pos.y) == Tile.SEA) {
                g.heightMap.setTile(pos.x, pos.y, Tile.BASALT);
                pos.x += direction.dx;
                pos.y += direction.dy;
                g.addEffect(this); //update on the client
            } else {
                g.deleteEffect(this);
            }
        }
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
        Random r = new Random();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(pos.x, pos.y, 0);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glBegin(GL.GL_TRIANGLES);
        for (int n = 0; n < 5; n++) {
            gl.glVertex3f(0.5f, 0.5f, 0.0f);
            gl.glVertex3f(r.nextFloat(), r.nextFloat(), 2.0f);
            gl.glVertex3f(r.nextFloat(), r.nextFloat(), 2.0f);
        }
        gl.glEnd();
        gl.glPopMatrix();

    }
}
