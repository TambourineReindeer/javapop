package javapoptools;

import com.novusradix.JavaPop.Client.GLHelper;
import com.novusradix.JavaPop.Client.GLHelper.GLHelperException;
import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Plane3;
import com.novusradix.JavaPop.Math.Vector3;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;
import javax.media.opengl.GLException;

/**
 *
 * @author gef
 */
public class Model {

    private ModelData data;
    private URL textureURL;
    private Texture tex;
    private int shader;
    private int[] vbos;
    private static Plane3 left,  right,  top,  bottom;
    private static Vector3 tl,  tr,  bl,  br,  ftl,  fbr;
    private boolean initialised = false;
    private static boolean clip = false;
    private boolean newTexture = false;
    

    static {
        tl = new Vector3();
        tr = new Vector3();
        bl = new Vector3();
        br = new Vector3();
        ftl = new Vector3();
        fbr = new Vector3();
        left = new Plane3();
        right = new Plane3();
        top = new Plane3();
        bottom = new Plane3();
    }

    public Model(ModelData model) {
        data = model;

    }

    public Model(ModelData model, URL texture) {
        data = model;
        textureURL = texture;
    }

    public void setTextureURL(URL u) {
        this.textureURL = u;
        newTexture = true;
    }

    public static void setRenderVolume(Matrix4 inverseMVP) {
        tl.set(-1, -1, 0);
        tr.set(1, -1, 0);
        bl.set(-1, 1, 0);
        br.set(1, 1, 0);
        ftl.set(-1, -1, 1);
        fbr.set(1, 1, 1);

        inverseMVP.transform(tl);
        inverseMVP.transform(tr);
        inverseMVP.transform(bl);
        inverseMVP.transform(br);
        inverseMVP.transform(ftl);
        inverseMVP.transform(fbr);
        left.set(tl, ftl, bl);
        right.set(br, fbr, tr);
        top.set(tl, tr, ftl);
        bottom.set(br, bl, fbr);
        clip = true;
    }

    public void prepare(GL gl) {
        if (!initialised) {
            init(gl);
        }
        if (tex == null || newTexture) {
            texInit(gl);
        }
        gl.glActiveTexture(GL.GL_TEXTURE0);
        if (tex != null) {
            tex.enable();
            tex.bind();
            gl.glUseProgram(shader);
            gl.glUniform1i(gl.glGetUniformLocation(shader, "tex1"), 0);
        } else {
            gl.glDisable(GL_TEXTURE_2D);
            gl.glUseProgram(0);
        }

        gl.glEnable(GL_LIGHTING);
        gl.glDisable(GL.GL_BLEND);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
        gl.glVertexPointer(3, GL.GL_FLOAT, data.getVertexStride() * 4, 0);
        gl.glNormalPointer(GL.GL_FLOAT, data.getVertexStride() * 4, data.getNormalOffset() * 4);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, data.getVertexStride() * 4, data.getTexCoordOffset() * 4);
        try {
            GLHelper.getHelper().checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void display(Vector3 position, Matrix4 basis, GL gl) {
        if (clip) {
            if (left.distance(position) + data.radius < 0 || right.distance(position) + data.radius < 0 || top.distance(position) + data.radius < 0 || bottom.distance(position) + data.radius < 0) {
                return;
            }
        }
        gl.glMatrixMode(GL_MODELVIEW);

        gl.glPushMatrix();
        gl.glTranslatef(position.x, position.y, position.z);
        gl.glMultMatrixf(basis.getArray(), 0);
        gl.glScalef(1, 1, 2);
        gl.glMultMatrixf(data.transform.getArray(), 0);

        gl.glDrawArrays(GL.GL_TRIANGLES, 0, data.triangleCount * 3);
        gl.glPopMatrix();
        try {
            GLHelper.getHelper().checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init(GL gl) {
        GLHelper glh = GLHelper.getHelper();
        if (vbos != null) {
            gl.glDeleteBuffers(1, vbos, 0);
        }
        vbos = new int[1];
        gl.glGenBuffers(1, vbos, 0);

        // Init VBOs and transfer data.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
        // Copy data to the server into the VBO.
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                data.vertices.capacity() * BufferUtil.SIZEOF_FLOAT, data.vertices,
                GL.GL_STATIC_DRAW);

        try {
            if (shader == 0) {
                gl.glDeleteProgram(shader);
            }
            shader = glh.LoadShaderProgram(gl, null, "/com/novusradix/JavaPop/Client/Shaders/ModelFragment.shader");
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.INFO, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            glh.checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        initialised = true;
    }

    private void texInit(GL gl) {
        try {
            tex = GLHelper.getHelper().getTexture(gl, textureURL);
        } catch (IOException ex) {
            //no problem , just no texture.
        }
    }

    public boolean isInitialised() {
        return initialised;
    }
}
