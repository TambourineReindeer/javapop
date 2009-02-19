package com.novusradix.JavaPop.Client.UI;

import com.novusradix.JavaPop.Client.GLHelper;
import com.sun.opengl.impl.GLObjectTracker;
import com.sun.opengl.util.texture.Texture;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class UIButton implements Clickable {

    Shape shape;
    String text;
    ActionListener action;
    Texture buttonTex;

    public UIButton(Shape shape, String text, ActionListener action) {
        this.shape = shape;
        this.text = text;
        this.action = action;
    }

    public Shape getShape() {
        return shape;
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
        PathIterator p = shape.getPathIterator(null);
        float[] coords = new float[6];
        int segment;
        float moveToX = 0, moveToY = 0;
        buttonTex.bind();
        do {
            segment = p.currentSegment(coords);
            switch (segment) {
                case PathIterator.SEG_MOVETO:
                    moveToX = coords[0];
                    moveToY = coords[1];
                    gl.glBegin(GL.GL_POLYGON);
                    gl.glTexCoord2f(moveToX, moveToY);
                    gl.glVertex2f(moveToX, moveToY);
                    break;
                case PathIterator.SEG_LINETO:
                case PathIterator.SEG_CUBICTO:
                case PathIterator.SEG_QUADTO:
                    gl.glTexCoord2f(coords[0], coords[1]);
                    gl.glVertex2f(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_CLOSE:
                    gl.glTexCoord2f(moveToX, moveToY);
                    gl.glVertex2f(moveToX, moveToY);
                    gl.glEnd();
                    break;
            }
            p.next();
        } while (!p.isDone());
        
    }

    public void init(GL gl) {
        try {
            buttonTex = GLHelper.glHelper.getTexture(gl, "/com/novusradix/JavaPop/textures/marble.png");
        } catch (IOException ex) {
            Logger.getLogger(UIButton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
