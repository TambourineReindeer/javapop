package com.novusradix.JavaPop.Client;

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
 * A generic 3D model class.
 * Clips rendering to the current view volume, given a model radius.
 * @author gef
 */
public class Model {

    private ModelData data;
    private URL textureURL;
    private Texture tex;
    private int shader,  defaultShader;
    private boolean customShader;
    private int[] vbos;
    private static Plane3 left,  right,  top,  bottom;
    private static Vector3 tl,  tr,  bl,  br,  ftl,  fbr;
    private boolean initialised = false;
    private static boolean clip = false;
    private boolean newTexture = false;
    private int[] is = new int[1];
    private float[] colour = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    

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
        customShader = false;
    }

    public Model(ModelData model, URL texture) {
        data = model;
        textureURL = texture;
        customShader = false;
    }

    public Model(ModelData model, URL texture, int shaderProgram) {
        data = model;
        textureURL = texture;
        customShader = true;
        shader = shaderProgram;
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

    /** Call to set the color that will be used after the next prepare()
     * Does not have to be called in an OpenGL thread
     */
    public void setColor(float r, float g, float b, float a) {
        colour[0] = r;
        colour[1] = g;
        colour[2] = b;
        colour[3] = a;

    }

    /** Call to set the color that will be used in the next display()
     * Has to be called in an OpenGL thread
     * Can be called after prepare()
     */
    public void setColor(GL gl, float r, float g, float b, float a) {
        setColor(r, g, b, a);
        gl.glValidateProgram(shader);
        gl.glGetProgramiv(shader, GL_VALIDATE_STATUS, is, 0);
        if (is[0] == GL_TRUE) {
            int l = gl.glGetUniformLocation(shader, "color");
            if (l != -1) {
                gl.glUniform4fv(l, 1, colour, 0);
            }
        } else {
            gl.glColor4fv(colour, 0);
        }
    }

    void setColor(GL gl, float[] srcColour) {
        System.arraycopy(srcColour, 0, colour, 0, srcColour.length);
        gl.glValidateProgram(shader);
        gl.glGetProgramiv(shader, GL_VALIDATE_STATUS, is, 0);
        if (is[0] == GL_TRUE) {
            int l = gl.glGetUniformLocation(shader, "color");
            if (l != -1) {
                gl.glUniform4fv(l, 1, colour, 0);
            }
        } else {
            gl.glColor4fv(colour, 0);
        }
    }

    /** Call before display()
     * Has to be called in an OpenGL thread
     */
    public void prepare(GL gl) {
        try {
            if (!initialised) {
                init(gl);
            }
            if (tex == null || newTexture) {
                texInit(gl);
            }
            GLHelper.glHelper.checkGL(gl);
            gl.glActiveTexture(GL.GL_TEXTURE0);
            GLHelper.glHelper.checkGL(gl);
            if (tex != null) {
                tex.enable();
                tex.bind();
            GLHelper.glHelper.checkGL(gl);
            } else {
                gl.glActiveTexture(GL.GL_TEXTURE0); 
                gl.glDisable(GL_TEXTURE_2D);
            GLHelper.glHelper.checkGL(gl);
            }
            GLHelper.glHelper.checkGL(gl);
            boolean useShader = false;
            if (shader != 0) {
                gl.glValidateProgram(shader);
                gl.glGetProgramiv(shader, GL_VALIDATE_STATUS, is, 0);
                if (is[0] == GL_TRUE) {
                    useShader = true;
                }
                gl.glGetProgramiv(shader, GL_INFO_LOG_LENGTH, is, 0);
                if (is[0] > 0) {
                    byte[] chars = new byte[is[0]];
                    gl.glGetProgramInfoLog(shader, is[0], is, 0, chars, 0);
                    String info = new String(chars);
                    if (info.toUpperCase().contains("ERROR")) {
                        useShader = false;
                    }
                }
            }
            GLHelper.glHelper.checkGL(gl);
            if (!useShader) {
                gl.glUseProgram(0);
                gl.glEnable(GL_LIGHTING);
                gl.glColor4fv(colour, 0);
                gl.glShadeModel(GL.GL_SMOOTH);
            } else {
                gl.glUseProgram(shader);
                GLHelper.glHelper.checkGL(gl);
                int l;
                l = gl.glGetUniformLocation(shader, "tex1");
                GLHelper.glHelper.checkGL(gl);
                if (l != -1) {
                    gl.glUniform1i(l, 0);
                }
                l = gl.glGetUniformLocation(shader, "color");
                GLHelper.glHelper.checkGL(gl);
                if (l != -1) {
                    gl.glUniform4fv(l, 1, colour, 0);
                }
                gl.glDisable(GL_LIGHTING);
                GLHelper.glHelper.checkGL(gl);
            }
            GLHelper.glHelper.checkGL(gl);
            gl.glEnable(GL.GL_BLEND);
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

            GLHelper.glHelper.checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays the model
     * Has to be called in an OpenGL thread
     * Must be called after prepare()
     */
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
            GLHelper.glHelper.checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialises the model. Compiles shaders etc.
     * If not called before prepare(), it will be called automatically from there.
     * Has to be called in an OpenGL thread
     */
    public void init(GL gl) {
        GLHelper glh = GLHelper.glHelper;
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
            defaultShader = glh.LoadShaderProgram(gl, "/com/novusradix/JavaPop/Client/Shaders/ModelVertex.shader", "/com/novusradix/JavaPop/Client/Shaders/ModelFragment.shader");
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.INFO, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!customShader) {
            shader = defaultShader;
        }
        try {
            glh.checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        tex=null;
        initialised = true;
    }

    public void setShader(int newshader) {
        shader = newshader;
        customShader = true;
    }

    public void setDefaultShader() {
        shader = defaultShader;
        customShader = false;
    }

    private void texInit(GL gl) {
        try {
            tex = GLHelper.glHelper.getTexture(gl, textureURL);
        } catch (IOException ex) {
            //no problem , just no texture.
        }
    }

    public boolean isInitialised() {
        return initialised;
    }
}
