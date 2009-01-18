package javapoptools;

import com.novusradix.JavaPop.Client.GLHelper;
import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector3;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

/**
 *
 * @author gef
 */
public class MainPanel extends GLJPanel implements GLEventListener {

    private static final float fHeightScale = 0.4082f;
    Model model;
    ModelData data;
    Vector3 modelPosition;
    Matrix4 modelBasis;

    public MainPanel(GLCapabilities caps)
    {
        super(caps);
        addGLEventListener(this);
    }
    
    public void setData(ModelData d) {
        data = d;
        model = new Model(d, "");
        modelPosition = new Vector3();
        modelBasis = new Matrix4(Matrix4.identity);
    }

    public void init(GLAutoDrawable glAD) {
        //Called before first display and on fullscreen/mode changes
        final GL gl = glAD.getGL();
        GLHelper.getHelper().init(gl);
        gl.setSwapInterval(1);
        gl.glEnable(GL.GL_LIGHTING);
        float global_ambient[] = {0.1f, 0.1f, 0.1f, 1.0f};
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global_ambient));

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f}));

        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    }

    public void display(GLAutoDrawable glAD) {
        final GL gl = glAD.getGL();
        gl.glClearColor(0, 0, 0, 0);
        gl.glEnable(GL.GL_LIGHTING);

        gl.glShadeModel(GL.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();

        gl.glTranslatef(0, 0, -50);
        gl.glRotatef(-60.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);

        gl.glScalef(1.0f, 1.0f, fHeightScale);

        if (model != null) {
            model.prepare(gl);
            model.display(modelPosition, modelBasis, gl);
        }
        gl.glPopMatrix();

    }

    public void reshape(final GLAutoDrawable glDrawable, final int x, final int y, final int w, int h) {
        final GL gl = glDrawable.getGL();

        if (h <= 0) // avoid a divide by zero error!
        {
            h = 1;
        }
        gl.glViewport(0, 0, w, h); //strictly unneccesary as the component calls this automatically
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-w / 64.0f, w / 64.0f, -h / 64.0f, h / 64.0f, 1, 100);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
