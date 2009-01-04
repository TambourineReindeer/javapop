package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.GLHelper.GLHelperException;
import com.novusradix.JavaPop.Math.Matrix4;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;
import javax.media.opengl.GLException;

/**
 *
 * @author gef
 */
public class XModel implements GLObject {

    private ArrayList<Vector8> vertices;
    private ArrayList<Integer> indices;
    private FloatBuffer b;
    private final int VX = 0,  VY = 1,  VZ = 2,  NX = 3,  NY = 4,  NZ = 5,  TX = 6,  TY = 7;
    private final int vertexstride = 8;
    private String textureName;
    private Texture tex;
    private int triangleCount;
    private int displayList;
    private boolean listCreated;
    private Matrix4 transform;
    private int shader;
    
    public XModel(String model, String texture) {
        this.textureName = texture;
        vertices = new ArrayList<Vector8>();

        indices = new ArrayList<Integer>();
        transform = new Matrix4(Matrix4.identity);

        StreamTokenizer ms;
        try {
            ms = loadResource(model);
            parseRoot(ms);
        } catch (IOException ex) {
            Logger.getLogger(XModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        deduplicate();
        fillBuffer();

        vertices = null;
        indices = null;
    }

    public void display(GL gl, float time) {
        tex.enable();
        tex.bind();
        gl.glEnable(GL_LIGHTING);
        gl.glDisable(GL.GL_BLEND);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glScalef(1, 1, 2);
        gl.glMultMatrixf(transform.getArray(), 0);

        gl.glUseProgram(shader);
        if (!listCreated) {
            gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

            b.position(VX);
            gl.glVertexPointer(3, GL.GL_FLOAT, vertexstride * 4, b);
            b.position(NX);
            gl.glNormalPointer(GL.GL_FLOAT, vertexstride * 4, b);
            b.position(TX);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, vertexstride * 4, b);
            gl.glNewList(displayList, GL.GL_COMPILE_AND_EXECUTE);
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, triangleCount * 3);
            gl.glEndList();
            listCreated = true;
        } else {
            gl.glCallList(displayList);
        }
        gl.glPopMatrix();
        tex.disable();
    }

    public void init(GL gl) {

        try {
            URL u = getClass().getResource(textureName);
            tex = TextureIO.newTexture(u, false, "png");
            tex.bind();
            tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT);
            tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT);
            GLHelper glh = new GLHelper();
            shader = glh.LoadShaderProgram(gl, null, "/com/novusradix/JavaPop/Client/Shaders/ModelFragment.shader");

        } catch (IOException ex) {
            Logger.getLogger(XModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(XModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        displayList = gl.glGenLists(1);
        listCreated = false;


    }

    private void deduplicate() {
        ArrayList<Vector8> newVertices;
        newVertices = new ArrayList<Vector8>();
        for (Vector8 v : vertices) {
            if (!newVertices.contains(v)) {
                newVertices.add(v);
            }
        }
        ArrayList<Integer> newIndices;
        newIndices = new ArrayList<Integer>();
        int newIndex;
        for (Integer i : indices) {
            newIndex = newVertices.indexOf(vertices.get(i));
            newIndices.add(newIndex);
        }
        indices = newIndices;
        vertices = newVertices;
    }

    private void fillBuffer() {
        b = BufferUtil.newFloatBuffer(triangleCount * 3 * vertexstride);
        int index;
        Vector8 v;
        for (int i = 0; i < indices.size(); i++) {
            index = indices.get(i);
            v = vertices.get(index);
            b.put(v.x);
            b.put(v.y);
            b.put(v.z);
            b.put(v.nx);
            b.put(v.ny);
            b.put(v.nz);
            b.put(v.tx);
            b.put(v.ty);
        }
    }

    private StreamTokenizer loadResource(String resource) throws IOException {
        BufferedReader brv;
        brv = new BufferedReader(new InputStreamReader(getClass().getResource(resource).openStream()));
        StreamTokenizer st = new StreamTokenizer(brv);
        st.commentChar('#');
        st.eolIsSignificant(false);
        return st;
    }

    private void parseRoot(StreamTokenizer ms) throws IOException {
        while (true) {
            switch (ms.nextToken()) {
                case StreamTokenizer.TT_WORD:
                     {
                        if (ms.sval.equals("Frame")) {
                            ms.nextToken();
                            break;
                        }
                        if (ms.sval.equals("Mesh")) {
                            parseVertices(ms);
                            break;
                        }
                        if (ms.sval.equals("MeshNormals")) {
                            parseNormals(ms);
                            break;
                        }
                        if (ms.sval.equals("MeshTextureCoords")) {
                            parseTextureCoords(ms);
                            break;
                        }
                        if (ms.sval.equals("FrameTransformMatrix")) {
                            parseFrameMatrix(ms);
                            break;
                        }

                    }
                    break;
                case StreamTokenizer.TT_NUMBER:
                     {
                    }
                    break;
                case StreamTokenizer.TT_EOF:
                    return;
            }
        }
    }

    private void parseVertices(StreamTokenizer ms) throws IOException {
        nextNumber(ms);
        int n = (int) ms.nval;

        Vector8 v;
        for (int i = 0; i < n; i++) {
            v = new Vector8();
            nextNumber(ms);
            v.x = (float) ms.nval;
            nextNumber(ms);
            v.y = (float) ms.nval;
            nextNumber(ms);
            v.z = (float) ms.nval;
            vertices.add(v);
        }
        nextNumber(ms);
        triangleCount = (int) ms.nval;
        for (int i = 0; i < triangleCount; i++) {

            nextNumber(ms);
            int m = (int) ms.nval;
            for (int j = 0; j < m; j++) {
                nextNumber(ms);
                indices.add((int) ms.nval);
            }
        }
    }

    private void parseNormals(StreamTokenizer ms) throws IOException {
        nextNumber(ms);
        int n = (int) ms.nval;

        Vector8 v;
        for (int i = 0; i < n; i++) {
            v = vertices.get(i);
            nextNumber(ms);
            v.nx = (float) ms.nval;
            nextNumber(ms);
            v.ny = (float) ms.nval;
            nextNumber(ms);
            v.nz = (float) ms.nval;
        }
    }

    private void parseTextureCoords(StreamTokenizer ms) throws IOException {
        nextNumber(ms);
        int n = (int) ms.nval;

        Vector8 v;
        for (int i = 0; i < n; i++) {
            v = vertices.get(i);
            nextNumber(ms);
            v.tx = (float) ms.nval;
            nextNumber(ms);
            v.ty = 1.0f + (float) ms.nval;
        }
    }

    private void parseFrameMatrix(StreamTokenizer ms) throws IOException {

        Matrix4 m = new Matrix4();
        float[] fs = new float[16];
        for (int i = 0; i < 16; i++) {
            nextNumber(ms);
            fs[i] = (float) ms.nval;

        }
        m.set(fs);
        transform.mul(transform, m);
    }

    private void nextNumber(StreamTokenizer ms) throws IOException {
        do {
            ms.nextToken();
        } while (ms.ttype != StreamTokenizer.TT_NUMBER);
    }

    private class Vector8 {

        float x, y, z, nx, ny, nz, tx, ty;

        public Vector8() {
        }

        @Override
        public int hashCode() {
            return Float.valueOf(x).hashCode() ^ Float.valueOf(y).hashCode() ^ Float.valueOf(z).hashCode() ^ Float.valueOf(nx).hashCode() ^ Float.valueOf(ny).hashCode() ^ Float.valueOf(nz).hashCode() ^ Float.valueOf(tx).hashCode() ^ Float.valueOf(ty).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Vector8) {
                Vector8 v = (Vector8) o;
                return v.x == x && v.y == y && v.z == z && v.nx == nx && v.ny == ny && v.nz == nz && v.tx == tx && v.ty == ty;
            }
            return false;
        }
    }
}
