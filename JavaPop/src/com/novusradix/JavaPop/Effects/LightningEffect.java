package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Server.Peons.Peon;
import com.novusradix.JavaPop.Tile;
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

    Point[] hits;
    Point target;
    transient Vector3[] strikes;
    transient float[] times;
    // transient int[] strikeIx;
    transient int nextStrike;
    transient float lastTime;
    transient Random r;
    transient int maxStrikes;

    public LightningEffect(Point target) {
        this.target = target;
        r = new Random();
        int n = r.nextInt(3) + 1;
        hits = new Point[n];
        for (int m = 0; m < n; m++) {
            hits[m] = new Point(target.x + r.nextInt(3) - 1, target.y + r.nextInt(3) - 1);
        }
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        maxStrikes = 10;
        strikes = new Vector3[maxStrikes];
        times = new float[maxStrikes];
        // strikeIx = new int[maxStrikes];
        for (Point h : hits) {
            h.x = h.x - target.x;
            h.y = h.y - target.y;
        }

        nextStrike = 0;
        r = new Random();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    @Override
    public void execute(Game g) {
        for (Point hit : hits) {
            g.heightMap.setTile(hit, Tile.BURNT);
            for (Peon p : g.peons.getPeons(hit)) {
                p.hurt(50);
            }            
        }
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(target.x + 0.5f, target.y + 0.5f, 0.0f);

        float c;
        int newStrikes;
        if (lastTime == 0) {
            newStrikes = 2;
        }else
        {
            newStrikes = nextPoisson(5.0 * (time - lastTime) * hits.length);
        }
        for (int n = 0; n < newStrikes; n++) {
            int ix = r.nextInt(hits.length);
            //         strikeIx[nextStrike] = ix;
            times[nextStrike] = time;
            Vector3 v = new Vector3(hits[ix].x + r.nextFloat() - 0.5f, hits[ix].y + r.nextFloat() - 0.5f, r.nextFloat() + 1.0f);
            strikes[nextStrike] = v;
            nextStrike = (nextStrike + 1) % maxStrikes;
        }
        lastTime = time;
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glUseProgram(0);
        gl.glBegin(GL.GL_TRIANGLES);
        float h = g.heightMap.getHeight(target.x + 0.5f, target.y + 0.5f);
        for (int n = 0; n < maxStrikes; n++) {
            c = Math.max(0, 1.0f + times[n] - time);
            if (c > 0) {
                h = g.heightMap.getHeight(target.x + 0.5f + strikes[n].x, target.y + 0.5f + strikes[n].y);
                gl.glColor4f(0.9f, 0.9f, 1.0f, c);
                gl.glVertex3f(strikes[n].x, strikes[n].y, h);
                gl.glVertex3f(strikes[n].x * 0.9f, strikes[n].y * 0.9f, strikes[n].z + h);
                gl.glVertex3f(strikes[n].x * 0.9f + 0.1f, strikes[n].y * 0.9f, strikes[n].z + h);

                gl.glVertex3f(strikes[n].x * 0.9f, strikes[n].y * 0.9f, strikes[n].z + h);
                gl.glVertex3f(strikes[n].x + 0.2f, strikes[n].y, strikes[n].z - 0.2f + h);
                gl.glVertex3f(strikes[n].x + 0.25f, strikes[n].y, strikes[n].z - 0.2f + h);

                gl.glVertex3f(strikes[n].x + 0.2f, strikes[n].y, strikes[n].z - 0.2f + h);
                gl.glVertex3f(strikes[n].x + 0.25f, strikes[n].y, strikes[n].z - 0.2f + h);
                gl.glVertex3f(strikes[n].x * 0.7f, strikes[n].y * 0.5f, 3.0f + h);
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
        while (product > elambda) {
            product *= r.nextDouble();
            result = count;
            count++; // keep result one behind
        }
        return result;
    }
}
