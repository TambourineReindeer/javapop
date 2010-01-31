/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abstractllc.hipparcos;

import com.sun.opengl.util.BufferUtil;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.vecmath.Vector3d;
import static java.lang.Math.*;

/**
 *
 * @author gef
 */
public class Nebula implements GLObject {

    private static final int particles = 100000;
    private final HipparcosInstance hi;
    FloatBuffer vertices;
    private int[] vbos;
    boolean initialised = false;

    public Nebula(HipparcosInstance hi) {
        this.hi = hi;
        vertices = BufferUtil.newFloatBuffer(3 * particles);

        double A, B, C, D;
        A = -0.9629629;
        B = 2.791139;
        C = 1.85185185;
        D = 1.5;
        changeParams(-0.9629629f, 2.791139f, 1.85185185f, 1.5f);
    }

    public void changeParams(float a, float b, float c, float d) {
        Vector3d pos, oldpos;
        pos = new Vector3d();
        oldpos = new Vector3d();
        double A, B, C, D;
        A = a;
        B = b;
        C = c;
        D = d;
        pos.x = 1;

        for (int i = 0; i < particles; i++) {

            pos.x = sin(A * oldpos.y) - oldpos.z * cos(B * oldpos.x);;
            pos.z = sin(oldpos.x);
            pos.y = oldpos.z * sin(C * oldpos.x) - cos(D * oldpos.y);
            oldpos.set(pos);
            vertices.put((float) pos.x);
            vertices.put((float) pos.y);
            vertices.put((float) pos.z);


        }
        vertices.flip();
        initialised = false;
    }

    public void display(GL gl, float time) {
        if (!initialised) {
            reinit(gl);
        }
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE); //Additive
        gl.glColor3f(0.1f, 0.1f, 0.1f);
        gl.glPointSize(2f);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glDrawArrays(GL.GL_POINTS, 0, particles);

        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
    }

    public void init(GL gl) {
        if (vbos != null) {
            gl.glDeleteBuffers(1, vbos, 0);
        }
        vbos = new int[1];
        gl.glGenBuffers(1, vbos, 0);



    }

    private void reinit(GL gl) {
        initialised = true;
        // Init VBOs and transfer data.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
        // Copy data to the server into the VBO.
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity() * BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);

    }
}
