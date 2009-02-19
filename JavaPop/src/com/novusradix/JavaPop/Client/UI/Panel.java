package com.novusradix.JavaPop.Client.UI;

import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Stack;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class Panel implements Clickable {

    private Rectangle2D.Float bounds;
    private final GLText text;
    private static int[] view = new int[4];

   private Stack<Clickable> components;

    public Panel(Rectangle2D.Float r, GLText text) {
        bounds = (Rectangle2D.Float) r.clone();
        this.text = text;
        components = new Stack<Clickable>();
    }

    public void add(Clickable component)
    {
        components.add(component);
    }

    public void display(GL gl, float time, int screenWidth, int screenHeight) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor4f(0.0f, 0.4f, 0.05f, 1.0f);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glTranslatef(bounds.x, bounds.y, 0.0f);
        gl.glScalef(bounds.width, bounds.height, 1.0f);

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glVertex2f(1.0f, 0.0f);
        gl.glVertex2f(1.0f, 1.0f);
        gl.glVertex2f(0.0f, 1.0f);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor4f(0.8f, 0.8f, 0.05f, 1.0f);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glVertex2f(1.0f, 0.0f);
        gl.glColor4f(0.4f, 0.4f, 0.05f, 1.0f);
        gl.glVertex2f(1.0f, 1.0f);
        gl.glVertex2f(0.0f, 1.0f);
        gl.glColor4f(0.8f, 0.8f, 0.05f, 1.0f);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glEnd();

        text.drawString(gl, "Welcome to JavaPop!", 0.5f-0.5f*text.getWidth("Welcome to JavaPop!", 0.1f), 0.1f, 0.1f);

        for(Clickable c:components)
            c.display(gl, time, screenWidth, screenHeight);
    }

    public void init(GL gl) {
        for(Clickable c:components)
        {
            c.init(gl);
        }
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
