/*
 * Renders the main game view, and reacts to user input.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.GLHelper.GLHelperException;
import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Client.Tools.ToolGroup;
import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Server.ServerPlayer.PeonMode;
import java.awt.Point;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;


import java.awt.event.MouseWheelListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.DebugGL;
import static java.lang.Math.*;
import static java.awt.event.KeyEvent.*;
import static javax.media.opengl.GL.*;

public class MainCanvas extends GLCanvas implements GLEventListener, KeyListener, MouseWheelListener, MouseMotionListener, MouseListener {

    /**
     *
     * @author gef
     */
    private static final long serialVersionUID = 1L;
    private static final float fHeightScale = 0.4082f;
    private float xPos,  yPos,  xOrig,  yOrig;
    private int height,  width;
    private Point dragOrigin;
    private Point selected;
    private Matrix4 mvpInverse;
    private Game game;
    private long startMillis,  lastMillis;
    private boolean[] keys;
    float xMouse, yMouse;
    private int frameCount = 0;
    private long frameTime;
    private ClickableHandler clickables;
    private boolean mouseIsOver;
    public static GLHelper glHelper;
    private float tileSize = 64;
    private boolean zoomChanged = false;

    public MainCanvas(GLCapabilities caps, Game g) {
        super(caps);
        glHelper = GLHelper.glHelper;
        keys = new boolean[0x20e];
        startMillis = lastMillis = System.currentTimeMillis();
        this.game = g;
        mvpInverse = new Matrix4();

        selected = new Point();
        clickables = new ClickableHandler(this, this, this);
        addGLEventListener(this);
        addMouseListener(clickables);
        addMouseMotionListener(clickables);
        addKeyListener(this);
        addMouseWheelListener(this);

        requestFocus();
        xPos = 0;
        yPos = 0;

        for (ToolGroup tg : game.toolGroups) {
            GLToolGroupButton tgb = new GLToolGroupButton(tg, clickables, game.objects);
        }
        GLToolGroupButton.selectVisibleToolGroupButton(1);
        GLToolButton.selectDefault();

        for (PeonMode m : PeonMode.values()) {
            GLBehaviourButton b = new GLBehaviourButton(m, clickables, game.objects, game.client);
            if (m == PeonMode.SETTLE) {
                b.select();
            }
        }

        mouseIsOver = false;
    }

    public void display(final GLAutoDrawable glAD) {
        long now = System.currentTimeMillis();
        game.step((now - lastMillis) / 1000.0f);
        lastMillis = now;
        float time = (now - startMillis) / 1000.0f;

        try {
            if (mouseIsOver) {
                setCursor(GLToolButton.getSelected().getCursor(selected));
            }
            final GL gl = glAD.getGL();
            glHelper.checkGL(gl);

            if (zoomChanged) {
                gl.glMatrixMode(GL.GL_PROJECTION);
                gl.glLoadIdentity();
                gl.glOrtho(-width / tileSize, width / tileSize, -height / tileSize, height / tileSize, 1, 100);
                zoomChanged = false;
            }
            gl.glClearColor(0, 0, 0, 0);
            gl.glEnable(GL.GL_LIGHTING);

            gl.glShadeModel(GL.GL_FLAT);
            gl.glEnable(GL.GL_DEPTH_TEST);

            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL.GL_PROJECTION);

            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPushMatrix();


            gl.glTranslatef(0, 0, -50);
            gl.glRotatef(-60.0f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
            gl.glTranslatef((0.70711f * xPos - yPos / 0.70711f), -(yPos / 0.70711f) - xPos * 0.70711f, 0);

            gl.glScalef(1.0f, 1.0f, fHeightScale);


            float[] buf = new float[16];
            gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, buf, 0);

            Matrix4 m_mvn, m_pn;
            m_mvn = new Matrix4();
            m_pn = new Matrix4();
            gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, buf, 0);

            m_mvn.set(buf);
            m_mvn.transpose();
            gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, buf, 0);
            m_pn.set(buf);
            m_pn.transpose();
            mvpInverse.mul(m_pn, m_mvn);
            mvpInverse.invert();

            gl.glEnable(GL.GL_BLEND);
            gl.glEnable(GL.GL_MULTISAMPLE);
            gl.glEnable(GL.GL_LIGHT0);

            Model.setRenderVolume(mvpInverse);

            glHelper.checkGL(gl);

            for (GLObject glo : game.objects) {
                glo.display(gl, time);
            }
            glHelper.checkGL(gl);
            for (GLObject glo : game.transparentObjects) {
                glo.display(gl, time);
            }
            glHelper.checkGL(gl);
            synchronized (game.effects) {
                for (Effect e : game.effects.values()) {
                    e.display(gl, time, game);
                }
            }
            displayCursor(gl);

            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPopMatrix();

            flush(gl);
            handleKeys();
            //printFPS();

            glHelper.checkGL(gl);

        } catch (GLHelperException ex) {
            Logger.getLogger(MainCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void lookAt(Point p) {
        xPos = (p.y - p.x) / 1.4f;
        yPos = (p.x + p.y) / 2.8f;
    //rough and ready
    }

    private void flush(GL gl) { //separate method helps when profiling
        //gl.glFlush();
    }

    private void displayCursor(final GL gl) {
        if (mouseIsOver) {
            float cW, cH;
            cW = 0.02f;
            cH = 0.1f;
            gl.glDisable(GL.GL_LIGHTING);
            gl.glEnable(GL.GL_BLEND);
            gl.glShadeModel(GL.GL_SMOOTH);
            gl.glDisable(GL.GL_DEPTH_TEST);
            gl.glUseProgram(0);
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glDisable(GL_TEXTURE_2D);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glTranslatef(selected.x, selected.y, game.heightMap.getHeight(selected.x, selected.y));
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
    }

    public void displayChanged(final GLAutoDrawable arg0, final boolean arg1, final boolean arg2) {
    }

    public void init(final GLAutoDrawable glDrawable) {
        //Called before first display and on fullscreen/mode changes
        //glDrawable.setGL(new DebugGL(glDrawable.getGL()));

        final GL gl = glDrawable.getGL();
        Logger.getLogger(MainCanvas.class.getName()).log(Level.INFO, "GL Init");
        glHelper.init(gl);
        try {
            gl.setSwapInterval(1);
            gl.glEnable(GL.GL_LIGHTING);
            gl.glEnable(GL.GL_LIGHT0);

            gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, FloatBuffer.wrap(new float[]{0.1f, 0.1f, 0.1f, 1.0f}));

            gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f}));

            gl.glEnable(GL.GL_COLOR_MATERIAL);
            gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            Vector3 l = new Vector3(-1, 0, 3);
            l.normalize();
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, FloatBuffer.wrap(new float[]{l.x, l.y, l.z, 0.0f}));

            for (GLObject glo : game.objects) {
                glo.init(gl);
            }
            glHelper.checkGL(gl);

            for (GLObject glo : game.transparentObjects) {
                glo.init(gl);
            }
            glHelper.checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(MainCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reshape(final GLAutoDrawable glDrawable, final int x, final int y, final int w, int h) {
        final GL gl = glDrawable.getGL();
        height = h;
        width = w;

        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
        gl.glViewport(0, 0, width, height); //strictly unneccesary as the component calls this automatically
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-width / tileSize, width / tileSize, -height / tileSize, height / tileSize, 1, 100);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_F:
                game.frame.setFullscreen(!game.frame.fullscreen);
                break;
            case VK_ESCAPE:
                game.frame.setFullscreen(false);
                break;
        }
        if (e.getKeyCode() < 0x20e) {
            keys[e.getKeyCode()] = true;
        }
    }

    public void keyReleased(final KeyEvent e) {
        if (e.getKeyCode() < 0x20e) {
            keys[e.getKeyCode()] = false;
        }
    }

    public void keyTyped(final KeyEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            xPos = xOrig + (e.getX() - dragOrigin.x) / (tileSize / 2.0f);
            yPos = yOrig + (e.getY() - dragOrigin.y) / (tileSize / 2.0f);
        }
    }

    public void mouseMoved(MouseEvent e) {

        if (width > 0 && height > 0) {
            xMouse = (2.0f * e.getX() / width - 1.0f);
            yMouse = (-2.0f * e.getY() / height + 1.0f);

            mousePick();
        }
    }

    private void mousePick() {
        float l;

        Vector3 z0, z1, s;
        z0 = new Vector3(xMouse, yMouse, 10);
        z1 = new Vector3(xMouse, yMouse, 11);

        mvpInverse.transform(z0);
        mvpInverse.transform(z1);

        Vector3 v0n, v1n;
        v0n = new Vector3(z0);
        v1n = new Vector3(z1);

        z1.sub(z0);
        l = -z0.z / z1.z;
        s = new Vector3();
        s.scaleAdd(l, z1, z0);

        selected.x = max(min((int) Math.round(s.x), game.heightMap.getWidth() - 1), 0);
        selected.y = max(min((int) Math.round(s.y), game.heightMap.getBreadth() - 1), 0);

        selected = iterateSelection(selected, v0n, v1n);
    }

    private void handleKeys() {
        boolean bPick = false;
        if (keys[VK_UP]) {
            yPos += 0.2f;
            bPick = true;
        }
        if (keys[VK_DOWN]) {
            yPos -= 0.2f;
            bPick = true;
        }
        if (keys[VK_LEFT]) {
            xPos += 0.2f;
            bPick = true;
        }
        if (keys[VK_RIGHT]) {
            xPos -= 0.2f;
            bPick = true;
        }

        for (int n = 0; n < 6; n++) {
            if (keys[VK_1 + n]) {
                if (keys[VK_SHIFT]) {
                    GLToolGroupButton.selectVisibleToolGroupButton(n + 1);
                } else {
                    GLToolGroupButton.selectVisibleToolButton(n + 1);
                }
            }
        }

        if (keys[VK_EQUALS]) {
            tileSize *= 1.05;
            zoomChanged = true;
        }
        if (keys[VK_MINUS]) {
            tileSize /= 1.05;
            zoomChanged = true;
        }


        if (bPick) {
            mousePick();
        }
    }

    private Point iterateSelection(Point current, Vector3 v0, Vector3 v1) {
        Vector3 p;

        float d, oldD;
        p = new Vector3(current.x, current.y, game.heightMap.getHeight(current.x, current.y));
        d = Helpers.PointLineDistance(v0, v1, p);
        oldD = d;

        int x, y;
        for (Point offset : Helpers.rings[1]) {
            x = current.x + offset.x;
            y = current.y + offset.y;

            if (x >= 0 && y >= 0 && x < game.heightMap.getWidth() && y < game.heightMap.getBreadth()) {
                p = new Vector3(x, y, game.heightMap.getHeight(x, y));
                d = Helpers.PointLineDistance(v0, v1, p);
                if (d < oldD) {
                    return iterateSelection(new Point(x, y), v0, v1);
                }
            }
        }
        return current;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        mouseIsOver = true;
    }

    public void mouseExited(MouseEvent e) {
        mouseIsOver = false;
    }

    public void mousePressed(MouseEvent e) {
        dragOrigin = e.getPoint();
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            xOrig = xPos;
            yOrig = yPos;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            GLToolButton.getSelected().PrimaryDown(selected);
        }if (e.getButton() == MouseEvent.BUTTON3) {
            GLToolButton.getSelected().SecondaryDown(selected);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
             mouseMoved(e);
        }
        else
        {
            if (e.getButton() == MouseEvent.BUTTON1) {
                GLToolButton.getSelected().PrimaryUp(selected);
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
            GLToolButton.getSelected().SecondaryUp(selected);
            }
        }
        
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isShiftDown()) {
            xPos -= e.getWheelRotation();
        } else {
            yPos -= e.getWheelRotation();
        }
    }

    private void printFPS() {
        if (frameCount % 100 == 0) {
            long t = System.currentTimeMillis();

            if (frameCount > 0) {
                float fps = 100000.0f / (t - frameTime);
                System.out.print(fps + " fps\n");
            }
            frameTime = t;
        }
        frameCount++;
    }
}
