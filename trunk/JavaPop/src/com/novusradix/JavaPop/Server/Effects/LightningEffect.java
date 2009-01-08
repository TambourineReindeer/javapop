package com.novusradix.JavaPop.Server.Effects;

import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Server.Peons.Peon;
import java.awt.Point;
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
        float h = g.heightMap.getHeight(hit.x+0.5f, hit.y+0.5f);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(hit.x, hit.y, h);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glUseProgram(0);    
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(0.9f, 0.9f, 1.0f);
        gl.glVertex3f(0.5f,0.5f,0.0f);
        gl.glVertex3f(0.5f,0.5f,3.0f);
        gl.glVertex3f(0.5f,0.6f,3.0f);
        gl.glEnd();
        gl.glPopMatrix();
        
    }
}
