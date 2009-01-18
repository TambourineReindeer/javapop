package javapoptools;

import com.novusradix.JavaPop.Math.Matrix4;
import com.sun.opengl.util.BufferUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class XImporter {

    private ArrayList<Vector8> vertices;
    private ArrayList<Integer> indices;
    private FloatBuffer b;
    private final int vertexstride = 8;
    private int triangleCount;
    private Matrix4 transform;
    private float radius;

    public XImporter(URL model) {
        vertices = new ArrayList<Vector8>();

        indices = new ArrayList<Integer>();
        transform = new Matrix4(Matrix4.identity);

        StreamTokenizer ms;
        try {
            ms = loadResource(model);
            parseRoot(ms);
        } catch (IOException ex) {
            Logger.getLogger(XImporter.class.getName()).log(Level.SEVERE, null, ex);
        }

        fillBuffer();

        vertices = null;
        indices = null;
    }

    public ModelData getModel() {
        ModelData m = new ModelData();
        m.vertices = BufferUtil.newFloatBuffer(b.limit());
        m.vertices.put(b);
        m.vertices.flip();
        m.normals = true;
        m.texcoords = true;
        m.indices = null;
        m.triangleCount = triangleCount;
        m.radius = radius;
        m.transform = new Matrix4(transform);               
        return m;
    }

    private void fillBuffer() {
        b = BufferUtil.newFloatBuffer(triangleCount * 3 * vertexstride);
        int index;
        Vector8 v;
        for (int i = 0; i < indices.size(); i++) {
            index = indices.get(i);
            v = vertices.get(index);
            radius = (float) Math.max(radius, Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
            b.put(v.x);
            b.put(v.y);
            b.put(v.z);
            b.put(v.nx);
            b.put(v.ny);
            b.put(v.nz);
            b.put(v.tx);
            b.put(v.ty);
        }
        b.flip();
    }

    private StreamTokenizer loadResource(URL u) throws IOException {
        BufferedReader brv;
        brv = new BufferedReader(new InputStreamReader(u.openStream()));
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

        float x,  y,  z,  nx,  ny,  nz,  tx,  ty;

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

