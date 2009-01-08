package com.novusradix.JavaPop.Server.Effects;

import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Server.Peons.Peon;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class LightningEffect extends Effect {

    Point hit;
    transient Vector3[] strikes;
    transient float[] times;
    transient int nextStrike;
    transient float lastTime;
    transient Random r;

    public LightningEffect(Point target) {
        hit = target;
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        strikes = new Vector3[5];
        times = new float[5];
        nextStrike = 0;
        r = new Random();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
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

        float c;
        if (lastTime > 0) {
            for (int n = 0; n < nextPoisson(5.0*(time - lastTime)); n++) {
                
                times[nextStrike] = time;
                Vector3 v = new Vector3(r.nextFloat() - 0.5f, r.nextFloat() - 0.5f, r.nextFloat() + 0.5f);
                strikes[nextStrike] = v;
                nextStrike = (nextStrike + 1) % 5;
            }
        }
        lastTime = time;
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glUseProgram(0);
        gl.glBegin(GL.GL_TRIANGLES);
        for (int n = 0; n < 5; n++) {

            c = Math.max(0, 1.0f + times[n] - time);
            if (c > 0) {
                gl.glColor4f(0.9f, 0.9f, 1.0f, c);
                gl.glVertex3f(0.0f, 0.0f, 0.0f);
                gl.glVertex3f(strikes[n].x, strikes[n].y, strikes[n].z);
                gl.glVertex3f(strikes[n].x + 0.1f, strikes[n].y, strikes[n].z);

                gl.glVertex3f(strikes[n].x, strikes[n].y, strikes[n].z);
                gl.glVertex3f(strikes[n].x + 0.2f, strikes[n].y, strikes[n].z - 0.2f);
                gl.glVertex3f(strikes[n].x + 0.25f, strikes[n].y, strikes[n].z - 0.2f);

                gl.glVertex3f(strikes[n].x + 0.2f, strikes[n].y, strikes[n].z - 0.2f);
                gl.glVertex3f(strikes[n].x + 0.25f, strikes[n].y, strikes[n].z - 0.2f);
                gl.glVertex3f(strikes[n].x * 2.0f, strikes[n].y * 1.5f, 3.0f);
            }
        }
        gl.glEnd();
        gl.glPopMatrix();
    }

    public int nextPoisson(double lambda) {
        double elambda = Math.exp(-1 * lambda);
        double product = 1;
        int count = 0;
        int result = 0;
        while (product >= elambda) {
            product *= r.nextDouble();
            result = count;
            count++; // keep result one behind
        }
        return result;
    }
}
