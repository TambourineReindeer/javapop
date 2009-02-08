package javapoptools.Fonts;

import com.novusradix.JavaPop.Client.GLText;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author gef
 */
public class KerningPanel extends GLCanvas implements GLEventListener, MouseListener, KeyListener {

    GLText text;

    KerningPanel(GLCapabilities caps) {
        super(caps);
        addGLEventListener(this);
        addMouseListener(this);
        addKeyListener(this);
        text = new GLText();
    }

    public void init(GLAutoDrawable glad) {
        GL gl = glad.getGL();
        text.init(gl);
    }

    public void display(GLAutoDrawable glad) {
        GL gl = glad.getGL();
        gl.glClearColor(0.1f, 0.1f, 0.5f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        StringBuffer sb = new StringBuffer();
        sb.setLength(2);

        for (int a = 0; a < 26; a++) {
            sb.setCharAt(0, (char) ('A' + a));

            for (int b = 0; b < 26; b++) {
                sb.setCharAt(1, (char) ('A' + b));
                String s = sb.toString();
                text.drawString(gl, s, a / 26.0f, b / 26.0f, 0.02f);
            }
        }
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }

    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        int a, b;
        a = 26 * p.x / getWidth();
        b = 25 - 26 * p.y / getHeight();

        if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK) {
            text.increaseKern(a, b);
        } else {
            text.decreaseKern(a, b);
        }
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 's':
                text.saveKerning();
                break;
            default:


        }
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
    }
}
