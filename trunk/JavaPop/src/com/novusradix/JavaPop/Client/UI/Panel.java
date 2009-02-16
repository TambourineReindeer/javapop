package com.novusradix.JavaPop.Client.UI;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class Panel implements GLObject2D, Clickable {

    Rectangle2D.Float bounds;
    private static int[] view = new int[4];

    public Panel(Rectangle2D.Float r) {
        bounds = (Rectangle2D.Float) r.clone();
    }

    public void display(GL gl, float time, int screenWidth, int screenHeight) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor4f(0.5f, 0.9f, 0.5f, 1.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(this.bounds.x, this.bounds.y);
        gl.glVertex2f(this.bounds.x + this.bounds.width, this.bounds.y);
        gl.glVertex2f(this.bounds.x + this.bounds.width, this.bounds.y + this.bounds.height);
        gl.glVertex2f(this.bounds.x, this.bounds.y + this.bounds.height);

        gl.glEnd();
    }

    public void init(GL gl) {
    }

    public Shape getShape() {
        return bounds;
    }

    public void mouseDown(MouseEvent e) {
    }

    public void mouseUp(MouseEvent e) {
    }

    public void mouseOver(MouseEvent e) {
    }

    public void mouseOut(MouseEvent e) {
    }

    public boolean anchorLeft() {
        return true;
    }

    public boolean anchorTop() {
        return true;
    }

    public boolean isVisible() {
        return true;
    }

    public void setVisible(boolean visible) {
    }

    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }

    public boolean inScreenSpace() {
        return false;
    }

    public void mouseDrag(float oldX, float oldY, float newX, float newY) {
        bounds.x += newX - oldX;
        bounds.y += newY - oldY;
    }
}
