/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.UpDown;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.sun.opengl.util.Animator;
import java.awt.event.MouseWheelListener;

public class MainCanvas extends GLCanvas implements GLEventListener, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final float fHeightScale = 0.4082f;
    private HeightMap heightMap;
    private Water water;
    private float xPos,  yPos,  xOrig,  yOrig;
    private int height,  width;
    private Point dragOrigin;
    private Point selected;
    private Matrix4f mvpInverse;
    private Client client;

    public MainCanvas(HeightMap h, GLCapabilities caps, Client c) {
        super(caps);

        this.client = c;
        mvpInverse = new Matrix4f();
        heightMap = h;
        Peon.init(h);
        House.init(h);

        water = new Water(heightMap);

        selected = new Point();

        addGLEventListener(this);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);

        getBounds();

        requestFocus();
        xPos = 0;
        yPos = 0;
    }

    public void display(final GLAutoDrawable glAD) {
        // TODO Auto-generated method stub
        final GL gl = glAD.getGL();
        gl.glClearColor(0, 0, 0, 0);
        gl.glEnable(GL.GL_LIGHTING);

        gl.glShadeModel(GL.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();

        gl.glRotatef(-60.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef((0.70711f * xPos - yPos / 0.70711f), -(yPos / 0.70711f) - xPos * 0.70711f, -25);

        gl.glScalef(1.0f, 1.0f, fHeightScale);

        float[] buf = new float[16];
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, buf, 0);

        Matrix4f m_mv, m_p;
        m_mv = new Matrix4f();
        m_p = new Matrix4f();
        m_mv.set(buf);
        m_mv.transpose();
        gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, buf, 0);
        m_p.set(buf);
        m_p.transpose();
        mvpInverse.mul(m_p, m_mv);
        mvpInverse.invert();

        gl.glEnable(GL.GL_BLEND);
        gl.glEnable(GL.GL_MULTISAMPLE);

        heightMap.display(gl);

        Peon.stepall();
        House.stepAll();

        Peon.displayall(gl);
        House.display(gl);

        displayCursor(gl);

        gl.glPopMatrix();
        gl.glFlush();
    }

    private void displayCursor(final GL gl) {
        float cW, cH;
        cW = 0.02f;
        cH = 0.1f;
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glDisable(GL.GL_DEPTH_TEST);

        gl.glPushMatrix();
        gl.glTranslatef(selected.x, selected.y, heightMap.getHeight(selected.x, selected.y));
        gl.glBegin(GL.GL_TRIANGLES);

        gl.glColor4f(1.0f, 1, 1, 1);
        gl.glVertex3f(cW, -cW, 0);
        gl.glVertex3f(-cW, cW, 0);

        gl.glColor4f(1.0f, 1, 1, 0.0f);
        gl.glVertex3f(0, 0, 2.0f * cH / fHeightScale);

        gl.glColor4f(1.0f, 1, 1, 1);
        gl.glVertex3f(cW, -cW, 0);
        gl.glVertex3f(-cW, cW, 0);

        gl.glColor4f(1.0f, 1, 1, 0);
        gl.glVertex3f(0, 0, -2.0f * cH / fHeightScale);

        gl.glColor4f(1.0f, 1, 1, 1);
        gl.glVertex3f(0, 0, 2.0f * cW / fHeightScale);
        gl.glVertex3f(0, 0, -2.0f * cW / fHeightScale);

        gl.glColor4f(1.0f, 1, 1, 0);
        gl.glVertex3f(-cH, +cH, 0);

        gl.glColor4f(1.0f, 1, 1, 1);
        gl.glVertex3f(0, 0, 2.0f * cW / fHeightScale);
        gl.glVertex3f(0, 0, -2.0f * cW / fHeightScale);

        gl.glColor4f(1.0f, 1, 1, 0);
        gl.glVertex3f(cH, -cH, 0);

        gl.glEnd();
        gl.glPopMatrix();
    }

    public void displayChanged(final GLAutoDrawable arg0, final boolean arg1, final boolean arg2) {
        // TODO Auto-generated method stub
    }

    public void init(final GLAutoDrawable glDrawable) {
        final GL gl = glDrawable.getGL();
        /*
         * final boolean VBOsupported = gl.isFunctionAvailable("glGenBuffersARB") &&
         * gl.isFunctionAvailable("glBindBufferARB") && gl.isFunctionAvailable("glBufferDataARB") &&
         * gl.isFunctionAvailable("glDeleteBuffersARB");
         */

        gl.glEnable(GL.GL_LIGHTING);
        float global_ambient[] = {0.1f, 0.1f, 0.1f, 1.0f};
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global_ambient));

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f}));

        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        heightMap.init(glDrawable);
    }

    public void reshape(final GLAutoDrawable glDrawable, final int x, final int y, final int w, int h) {
        final GL gl = glDrawable.getGL();
        height = h;
        width = w;

        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-width / 64.0f, width / 64.0f, -height / 64.0f, height / 64.0f, 1, 100);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void keyPressed(final KeyEvent e) {
        // TODO Auto-generated method stub

        if (e.getKeyCode() == KeyEvent.VK_N) {
            Peon.addPeon(selected.x, selected.y);
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            water.addSpring(selected.x, selected.y);
        }

        if (e.getKeyCode() == KeyEvent.VK_W) {
            for (int n = 0; n < 10; n++) {
                Random r = new Random();
                water.drop(selected.x + r.nextFloat(), selected.y + r.nextFloat());
            }
        }
    }

    public void keyReleased(final KeyEvent e) {
        // TODO Auto-generated method stub
    }

    public void keyTyped(final KeyEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            xPos = xOrig + (e.getX() - dragOrigin.x) / 32.0f;
            yPos = yOrig + (e.getY() - dragOrigin.y) / 32.0f;
        }
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        float xMouse, yMouse;
        xMouse = (2.0f * e.getX() / width - 1.0f);
        yMouse = (-2.0f * e.getY() / height + 1.0f);

        float l;
        Vector4f z0, z1, s;
        z0 = new Vector4f(xMouse, yMouse, 10, 1);
        z1 = new Vector4f(xMouse, yMouse, 11, 1);

        mvpInverse.transform(z0);
        mvpInverse.transform(z1);
        Vector3f v0, v1, p;
        v0 = new Vector3f(z0.x, z0.y, z0.z);
        v1 = new Vector3f(z1.x, z1.y, z1.z);
        z1.sub(z0);
        l = -z0.z / z1.z;
        s = new Vector4f();
        s.scaleAdd(l, z1, z0);
        selected.x = Math.max(Math.min((int)Math.round(s.x), heightMap.getWidth()-1),0);
        selected.y = Math.max(Math.min((int)Math.round(s.y), heightMap.getBreadth()-1),0);

        float d, oldD;
        p = new Vector3f(selected.x, selected.y, heightMap.getHeight(selected.x, selected.y));
        d = Helpers.PointLineDistance(v0, v1, p);
        oldD = 1000;
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                p = new Vector3f(x, y, heightMap.getHeight(x, y));
                d = Helpers.PointLineDistance(v0, v1, p);
                if (d < oldD) {
                    selected.x = x;
                    selected.y = y;
                    oldD = d;
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        dragOrigin = e.getPoint();
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            xOrig = xPos;
            yOrig = yPos;
        }

    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        if (Math.abs(e.getX() - dragOrigin.x) < 16 && Math.abs(e.getY() - dragOrigin.y) < 16) {
            boolean primary;
            if (e.getButton() == MouseEvent.BUTTON1) {
                if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
                    primary = false;
                } else {
                    primary = true;
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                primary = false;
            } else {
                return;
            }
            client.sendMessage(new UpDown(selected.x, selected.y, primary));
        } else {
            mouseMoved(e);

        }

    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isShiftDown()) {
            xPos -= e.getWheelRotation();
        } else {
            yPos -= e.getWheelRotation();
        }
    }
}
