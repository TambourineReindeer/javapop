package com.novusradix.JavaPop.Client.UI;

import com.novusradix.JavaPop.Client.*;
import com.sun.opengl.util.texture.Texture;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;

/**
 *
 * @author gef
 * */
public abstract class Button implements Clickable {

    Polygon buttonShape;
    boolean enabled;
    boolean visible;
    String texname;
    Texture tex, marble;
    boolean flipx, flipy;

    private Button() {
    }

    protected Button(ClickableHandler ch) {
        enabled = true;
        visible = false;
        ch.addClickable(this);
    }

    protected abstract boolean isSelected();

    public abstract void select();

    public void display(GL gl, float time, int screenWidth, int screenHeight) {
        //TODO: most of this only needs to be done once per frame, not per button.
        if (!visible) {
            return;
        }
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        if (flipx) {
            gl.glTranslatef(1.0f, 0.0f, 0.0f);
            gl.glScalef(-1.0f, 1.0f, 1.0f);
        }
        if (flipy) {
            gl.glTranslatef(0.0f, 1.0f, 0.0f);
            gl.glScalef(1.0f, -1.0f, 1.0f);
        }
        gl.glScalef(1.0f / screenWidth, 1.0f / screenHeight, 1.0f);
        if (isSelected() || (mDown && mOver)) {
            gl.glColor3f(0.6f, 0.6f, 0.8f);
        } else {
            gl.glColor3f(0.8f, 0.8f, 0.8f);
        }
        gl.glActiveTexture(GL.GL_TEXTURE0);
        marble.enable();
        marble.bind();
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glBegin(GL.GL_POLYGON);

        for (int n = 0; n < buttonShape.npoints; n++) {
            gl.glTexCoord2f(n / 2, (n + n / 2 + 1) % 2);
            gl.glVertex3f(buttonShape.xpoints[n], buttonShape.ypoints[n], -0.5f);
        }
        gl.glEnd();

        tex.enable();
        tex.bind();
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glBegin(GL.GL_POLYGON);

        for (int n = 0; n < buttonShape.npoints; n++) {
            gl.glTexCoord2f(n / 2, (n + n / 2 + 1) % 2);
            gl.glVertex3f(buttonShape.xpoints[n], buttonShape.ypoints[n], -1.0f);
        }
        gl.glEnd();
        gl.glPopMatrix();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();

    }

    public void init(GL gl) {

        try {
            tex = MainCanvas.glHelper.getTexture(gl, texname);
            marble = MainCanvas.glHelper.getTexture(gl, "/com/novusradix/JavaPop/textures/marble.png");
        } catch (IOException ex) {
            Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLException ex) {
            Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Shape getShape() {
        return buttonShape;
    }
    private boolean mDown,  mOver;

    public void mouseDown(MouseEvent e) {
        mDown = true;
    }

    public void mouseUp(MouseEvent e) {
        if (mOver && mDown) {
            select();
        }
        mDown = false;
    }

    public void mouseOver(MouseEvent e) {
        mOver = true;
    }

    public void mouseOut(MouseEvent e) {
        mOver = false;
    }

    public void mouseDrag(float oldX, float oldY, float newX, float newY) {
    }

    public boolean anchorLeft() {
        return !flipx;
    }

    public boolean anchorTop() {
        return !flipy;
    }

    public boolean inScreenSpace() {
        return true;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Clickable getClickableAtPoint(float x, float y) {
        return this;
    }
}
