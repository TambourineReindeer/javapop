package com.novusradix.JavaPop.Server.Effects;

import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Server.Peons.Peon;
import java.awt.Point;
import java.util.Random;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class LightningEffect extends Effect {

    Point hit;

    public LightningEffect(Point target) {
        hit = target;
    }

    @Override
    public void execute(Game g) {
        for (Peon p : g.peons.getPeons(hit)) {
            p.hurt(500);
        }
        House h;
        h = g.houses.getHouse(hit);
        if (h != null) {
            h.damage(500);
        }
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
        float h = g.heightMap.getHeight(hit.x + 0.5f, hit.y + 0.5f);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(hit.x + 0.5f, hit.y + 0.5f, h);
        float x, y, z, c;
        Random r = new Random();
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glUseProgram(0);
        gl.glBegin(GL.GL_TRIANGLES);
        for (int n = 0; n < 4; n++) {
            x = r.nextFloat() - 0.5f;
            y = r.nextFloat() - 0.5f;
            z = r.nextFloat() + 0.5f;
            c = r.nextFloat();

            gl.glColor4f(0.9f, 0.9f, 1.0f, c);
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(x, y, z);
            gl.glVertex3f(x + 0.1f, y, z);
            
            gl.glVertex3f(x, y, z);
            gl.glVertex3f(x + 0.2f, y, z - 0.2f);
            gl.glVertex3f(x + 0.25f, y, z - 0.2f);
            
            gl.glVertex3f(x + 0.2f, y, z - 0.2f);
            gl.glVertex3f(x + 0.25f, y, z - 0.2f);
            gl.glVertex3f(x*2.0f, y*1.5f, 3.0f);

        }
        gl.glEnd();
        gl.glPopMatrix();

    }
}
