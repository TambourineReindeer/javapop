package com.novusradix.JavaPop.Client.UI;

import com.novusradix.JavaPop.Client.GLHelper;
import com.sun.opengl.util.texture.Texture;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class UIButton implements Clickable {

    Rectangle2D.Float bounds;
    String text;
    ActionListener action;
    Texture buttonTex;
    GLText textRenderer;

    public UIButton(Rectangle2D.Float bounds, String text, ActionListener action, GLText textRenderer) {
        this.bounds = bounds;
        this.text = text;
        this.action = action;
        this.textRenderer = textRenderer;
    }

    public Shape getShape() {
        return bounds;
    }

    public boolean anchorLeft() {
        return true;
    }

    public boolean anchorTop() {
        return true;
    }

    public boolean inScreenSpace() {
        return false;
    }

    public void mouseDown(MouseEvent e) {
    }

    public void mouseUp(MouseEvent e) {
    }

    public void mouseOver(MouseEvent e) {
    }

    public void mouseOut(MouseEvent e) {
    }

    public void mouseDrag(float oldX, float oldY, float newX, float newY) {
    }

    public boolean isVisible() {
        return true;
    }

    public void setVisible(boolean visible) {
    }

    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }

    public void display(GL gl, float time, int screenWidth, int screenHeight) {
        gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
        buttonTex.bind();
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

        textRenderer.drawString(gl, text, 0.5f-0.5f*textRenderer.getWidth(text, 1.0f), 0.8f, 1.0f);

        
    }

    public void init(GL gl) {
        try {
            buttonTex = GLHelper.glHelper.getTexture(gl, "/com/novusradix/JavaPop/textures/marble.png");
        } catch (IOException ex) {
            Logger.getLogger(UIButton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Clickable getClickableAtPoint(float x, float y) {
        return this;
    }
}
