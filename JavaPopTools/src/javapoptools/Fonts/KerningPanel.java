package javapoptools.Fonts;

import com.novusradix.JavaPop.Client.GLText;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author gef
 */
public class KerningPanel extends GLCanvas implements GLEventListener {

    GLText text;

    KerningPanel(GLCapabilities caps) {
        super(caps);
        addGLEventListener(this);
        text = new GLText();
    }

    public void init(GLAutoDrawable glad) {
        GL gl = glad.getGL();
        text.init(gl);
    }

    public void display(GLAutoDrawable glad) {
        GL gl = glad.getGL();
        gl.glClearColor(0.1f,0.1f, 0.5f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
             
        StringBuffer sb = new StringBuffer();
        sb.setLength(2);

        for (int a = 0; a < 26; a++) {
            sb.setCharAt(0, (char) ('A' + a));

            for (int b = 0; b < 26; b++) {
                sb.setCharAt(1, (char) ('A' + b));
                String s = sb.toString();
                text.drawString(gl, s, a / 26.0f, b  / 26.0f, 0.02f);
            }
        }
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }
}
