/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abstractllc.hipparcos;

import com.abstractllc.hipparcos.GLHelper.GLHelperException;
import com.sun.opengl.util.BufferUtil;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import com.sun.opengl.util.texture.*;

import static javax.media.opengl.GL.*;

/**
 * distance unit - parsec
 *
 * @author gef
 */
public class GalaxyRenderer implements GLObject {

    float x, y, z;
    private HipparcosInstance hi;
    FloatBuffer vertices;
    private int[] vbos;
    private int shader;
    final private static int vertexStride = 8;
    private Texture starTex;

    public GalaxyRenderer(HipparcosInstance hi) {
        this.hi = hi;

        /* vertices layout:
         * x y z
         * r g b 
         *
         *
         *
         */

        vertices = BufferUtil.newFloatBuffer(hi.hipEntries.size() * vertexStride);
        float[] c = new float[3];
        float linearMag;
        for (HipEntry h : hi.hipEntries.values()) {
            vertices.put(h.x);
            vertices.put(h.y);
            vertices.put(h.z);
            ColourFromBV(h.BV, c);
            linearMag = (float) Math.pow(10, 2 * (11 - h.aMag) / 5);
            c[0] *= linearMag;
            c[1] *= linearMag;
            c[2] *= linearMag;
            vertices.put(c);
            vertices.put(0);//padding
            vertices.put(0);//padding
        }
        vertices.flip();

    }

    private void ColourFromBV(float fBV, float[] out) {

        if (fBV < 0.43) {
            out[0] = 0.792f + 0.484f * fBV;
            out[1] = 0.839f + 0.374f * fBV;
            out[2] = 1.0f;
        } else {
            out[0] = 1.0f;
            out[1] = 1.088f - 0.205f * fBV;
            out[2] = 1.187f - 0.434f * fBV;
        }
    }

    public void display(GL gl, float exposure) {
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE); //Additive
        gl.glEnable(GL_TEXTURE_2D);
        starTex.bind();
        gl.glUseProgram(shader);
        gl.glEnable(GL.GL_POINT_SPRITE);
        gl.glEnable(GL.GL_PROGRAM_POINT_SIZE_EXT);
        gl.glPointParameterf(GL_POINT_SIZE_MAX, 32f);
        gl.glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, GL_TRUE);

        GLHelper.glHelper.setShaderUniform(gl, shader, "tex", 0);
        int loc = gl.glGetAttribLocation(shader, "color");
        gl.glVertexPointer(3, GL.GL_FLOAT, vertexStride * BufferUtil.SIZEOF_FLOAT, 0);
        gl.glVertexAttribPointer(loc, 3, GL_FLOAT, false, vertexStride * BufferUtil.SIZEOF_FLOAT, 3 * BufferUtil.SIZEOF_FLOAT);
        gl.glEnableVertexAttribArray(loc);
        GLHelper.glHelper.setShaderUniform(gl, shader, "exposure", exposure);
        gl.glDrawArrays(GL.GL_POINTS, 0, hi.hipEntries.size());

        gl.glUseProgram(0);
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        gl.glDisableVertexAttribArray(0);

    }

    public void init(GL gl) {
        if (vbos != null) {
            gl.glDeleteBuffers(1, vbos, 0);
        }
        vbos = new int[1];
        gl.glGenBuffers(1, vbos, 0);

        // Init VBOs and transfer data.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
        // Copy data to the server into the VBO.
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity() * BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);
        try {
            shader = GLHelper.glHelper.LoadShaderProgram(gl, "/com/abstractllc/hipparcos/shaders/GalaxyVertex.shader", "/com/abstractllc/hipparcos/shaders/GalaxyFragment.shader");

        } catch (IOException ex) {
            Logger.getLogger(GalaxyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(GalaxyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            starTex = GLHelper.glHelper.getTexture(gl, getClass().getResource("/com/abstractllc/hipparcos/textures/star32.png"));
        } catch (IOException ex) {
            Logger.getLogger(GalaxyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
