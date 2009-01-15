package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Tile;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.TextureIO;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.Map.Entry;
import java.util.Random;
import javax.media.opengl.*;
import static javax.media.opengl.GL.*;

/**
 *
 * @author gef
 */
public class HeightMapNoShader implements HeightMapImpl, GLObject {
    /*
     * b layout: float x,y,z,nx,ny,nz,tx,ty;
     *
     * 12 vertices per tile:
     *
     *  7-------6
     * 9 \     / 4
     * |\ \   / /|
     * | \ \ / / |
     * |  \ 8 /  |
     * |  11 5   |
     * |  / 2 \  |
     * | / / \ \ |
     * |/ /   \ \|
     *10 /     \ 3
     *  0-------1
     *
     */

    com.sun.opengl.util.texture.Texture tex;
    private FloatBuffer b;
    private int rowstride,  tilestride,  vertexstride;
    private static final int VX = 0,  VY = 1,  VZ = 2,  NX = 3,  NY = 4,  NZ = 5,  TX = 6,  TY = 7;
    private HeightMap heightMap;  
    private boolean[] changed;
    private int displaylist;

    public void initialise(HeightMap h) {
        heightMap = h;       
        vertexstride = 8;
        tilestride = 12 * vertexstride;
        rowstride = tilestride * (heightMap.width - 1);
        changed = new boolean[heightMap.breadth - 1];
        b = BufferUtil.newFloatBuffer((heightMap.width - 1) * (heightMap.breadth - 1) * tilestride);

        int x, y;
        for (y = 0; y < (heightMap.breadth - 1); y++) {
            for (x = 0; x < (heightMap.width - 1); x++) {

                // 0
                b.put(x);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0);
                b.put(0);
                // 1
                b.put((float) x + 1);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(31.0f);
                b.put(0);
                // 2
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(15.5f);
                b.put(15.5f);
                // 3
                b.put((float) x + 1);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(31.0f);
                b.put(0);
                // 4
                b.put((float) x + 1);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(31.0f);
                b.put(31.0f);
                // 5
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(15.5f);
                b.put(15.5f);
                // 6
                b.put((float) x + 1);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(31.0f);
                b.put(31.0f);
                // 7
                b.put(x);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0);
                b.put(31.0f);
                // 8
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(15.5f);
                b.put(15.5f);
                // 9
                b.put(x);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0);
                b.put(31.0f);
                // 10
                b.put(x);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0);
                b.put(0);
                // 11
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(15.5f);
                b.put(15.5f);
            }
        }
        b.flip();
    }

    private int bufPos(int x, int y, int vertex, int index) {
        return y * rowstride + x * tilestride + vertex * vertexstride + index;
    }

    public byte getHeight(int x, int y) {
        if (x == heightMap.width - 1 && y == heightMap.breadth - 1) {
            return (byte) b.get(bufPos(x - 1, y - 1, 4, VZ));
        }
        if (x == heightMap.width - 1) {
            return (byte) b.get(bufPos(x - 1, y, 3, VZ));
        }
        if (y == heightMap.breadth - 1) {
            return (byte) b.get(bufPos(x, y - 1, 7, VZ));
        }
        if (heightMap.inBounds(x, y)) {
            return (byte) b.get(bufPos(x, y, 0, VZ));
        }
        return 0;
    }

    public float getHeight2(int x, int y) {
        try {
            return b.get(bufPos(x, y, 2, VZ));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client getHeight2:" + x + ", " + y);

        }
        return 0;
    }

    public void setHeight(int x, int y, byte height) {
        try {
            if (heightMap.inBounds(x, y)) {
                if (x < heightMap.width - 1 && y < heightMap.width - 1) {
                    b.put(bufPos(x, y, 0, VZ), height);
                    b.put(bufPos(x, y, 10, VZ), height);
                }
                if (x >= 1 && y >= 1) {
                    b.put(bufPos(x - 1, y - 1, 4, VZ), height);
                    b.put(bufPos(x - 1, y - 1, 6, VZ), height);
                }
                if (y >= 1 && x < heightMap.width - 1) {
                    b.put(bufPos(x, y - 1, 7, VZ), height);
                    b.put(bufPos(x, y - 1, 9, VZ), height);
                }
                if (x >= 1 && y < heightMap.width - 1) {
                    b.put(bufPos(x - 1, y, 1, VZ), height);
                    b.put(bufPos(x - 1, y, 3, VZ), height);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.print(x + ", " + y + ": out of bounds in SetHeight()");
        }
    }

    protected void setMidTile(int x, int y) {

        if (x < 0 || y < 0 || x >= heightMap.width - 1 || y >= heightMap.breadth - 1) {
            return;
        }
        float m;

        m = heightMap.getHeight(x+0.5f, y+0.5f);

        try {
            b.put(bufPos(x, y, 2, VZ), m);
            b.put(bufPos(x, y, 5, VZ), m);
            b.put(bufPos(x, y, 8, VZ), m);
            b.put(bufPos(x, y, 11, VZ), m);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client setMidTile:" + x + ", " + y);
        }
        setNormals(x, y);
        changed[y] = true;
    }

    public void setTile(int x, int y, byte t) {
        float left, xmid, right, top, bottom, ymid;
        int texid;
        switch (Tile.values()[t]) {
            case SEA:
                texid = (new Random()).nextInt(3);
                break;
            case EMPTY_FLAT:
            case EMPTY_SLOPE:
            case ROCK:
            case TREE:
                texid = 3;
                break;
            case FARM:
                texid = 4;
                break;
            case LAVA:
                texid = 5;
                break;
            case BURNT:
                texid = 6;
                break;
            case SWAMP:
                texid = 7;
                break;
            default:
                texid = 8;
                break;
        }


        int tx, ty;
        tx = texid % 8;
        ty = texid / 8;
        left = tx * 32.0f;
        right = left + 31.0f;
        xmid = (left + right) / 2.0f;
        top = ty * 32.0f;
        bottom = top + 31.0f;
        ymid = (top + bottom) / 2.0f;

        b.put(bufPos(x, y, 0, TX), left);
        b.put(bufPos(x, y, 1, TX), right);
        b.put(bufPos(x, y, 2, TX), xmid);
        b.put(bufPos(x, y, 3, TX), right);
        b.put(bufPos(x, y, 4, TX), right);
        b.put(bufPos(x, y, 5, TX), xmid);
        b.put(bufPos(x, y, 6, TX), right);
        b.put(bufPos(x, y, 7, TX), left);
        b.put(bufPos(x, y, 8, TX), xmid);
        b.put(bufPos(x, y, 9, TX), left);
        b.put(bufPos(x, y, 10, TX), left);
        b.put(bufPos(x, y, 11, TX), xmid);

        b.put(bufPos(x, y, 0, TY), bottom);
        b.put(bufPos(x, y, 1, TY), bottom);
        b.put(bufPos(x, y, 2, TY), ymid);
        b.put(bufPos(x, y, 3, TY), bottom);
        b.put(bufPos(x, y, 4, TY), top);
        b.put(bufPos(x, y, 5, TY), ymid);
        b.put(bufPos(x, y, 6, TY), top);
        b.put(bufPos(x, y, 7, TY), top);
        b.put(bufPos(x, y, 8, TY), ymid);
        b.put(bufPos(x, y, 9, TY), top);
        b.put(bufPos(x, y, 10, TY), bottom);
        b.put(bufPos(x, y, 11, TY), ymid);

        changed[y] = true;
    }

    private void setNormals(int x, int y) {
        setNormals(x, y, 0, 1, 2);
        setNormals(x, y, 3, 4, 5);
        setNormals(x, y, 6, 7, 8);
        setNormals(x, y, 9, 10, 11);
    }

    private void setNormals(int x, int y, int vertA, int vertB, int vertC) {
        //TODO: profiled:candidate for optimisation
        Vector3 va, vb, vc,
                vn;
        va = new Vector3();
        vb = new Vector3();

        vc = new Vector3();

        try {
            va.x = b.get(bufPos(x, y, vertA, VX));
            va.y = b.get(bufPos(x, y, vertA, VY));
            va.z = b.get(bufPos(x, y, vertA, VZ));

            vb.x = b.get(bufPos(x, y, vertB, VX));
            vb.y = b.get(bufPos(x, y, vertB, VY));
            vb.z = b.get(bufPos(x, y, vertB, VZ));

            vc.x = b.get(bufPos(x, y, vertC, VX));
            vc.y = b.get(bufPos(x, y, vertC, VY));
            vc.z = b.get(bufPos(x, y, vertC, VZ));

            vn = Helpers.calcNormal(vc, va, vb);

            b.put(bufPos(x, y, vertA, NX), vn.x);
            b.put(bufPos(x, y, vertA, NY), vn.y);
            b.put(bufPos(x, y, vertA, NZ), vn.z);

            b.put(bufPos(x, y, vertB, NX), vn.x);
            b.put(bufPos(x, y, vertB, NY), vn.y);
            b.put(bufPos(x, y, vertB, NZ), vn.z);

            b.put(bufPos(x, y, vertC, NX), vn.x);
            b.put(bufPos(x, y, vertC, NY), vn.y);
            b.put(bufPos(x, y, vertC, NZ), vn.z);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client SetNormal:" + x + ", " + y);
        }
    }

    public void init(final GL gl) {

        try {
            URL u = getClass().getResource("/com/novusradix/JavaPop/textures/tex.png");
            tex = TextureIO.newTexture(u, false, "png");

        } catch (GLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        displaylist = gl.glGenLists(heightMap.breadth);
        for (int n = 0; n < heightMap.breadth - 1; n++) {
            changed[n] = true;
        }
    }

    public void display(GL gl, float time) {
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT1);

        gl.glColor3f(1, 1, 1);
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glScalef(1.0f / 255.0f, 1.0f / 255.0f, 1.0f);
        gl.glMatrixMode(GL_MODELVIEW);

        tex.enable();
        tex.bind();
        boolean firstChange = true;
        for (int n = 0; n < heightMap.breadth - 1; n++) { //todo - if this ever gets slow, limit to visible rows only
            if (changed[n]) {
                if (firstChange) {
                    gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
                    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
                    gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                    b.position(VX);
                    gl.glVertexPointer(3, GL.GL_FLOAT, vertexstride * 4, b);
                    b.position(NX);
                    gl.glNormalPointer(GL.GL_FLOAT, vertexstride * 4, b);
                    b.position(TX);
                    gl.glTexCoordPointer(2, GL.GL_FLOAT, vertexstride * 4, b);
                    firstChange = false;
                }
                gl.glNewList(displaylist + n, GL_COMPILE_AND_EXECUTE);

                synchronized (this) {
                    gl.glDrawArrays(GL.GL_TRIANGLES, n * (heightMap.width - 1) * 4 * 3, (heightMap.width - 1) * 4 * 3);
                }
                gl.glEndList();
                changed[n] = false;
            } else {
                gl.glCallList(displaylist + n);
            }
        }
        tex.disable();
    }

    public void applyUpdate(HeightMapUpdate u) {
        int x, y;
        synchronized (this) {
            if (!u.dirtyRegion.isEmpty()) {

                for (y = 0; y < u.dirtyRegion.height; y++) {
                    for (x = 0; x < u.dirtyRegion.width; x++) {
                        setHeight(u.dirtyRegion.x + x, u.dirtyRegion.y + y, u.heightData[x + y * u.dirtyRegion.width]);
                    }
                }
                for (y = -1; y < u.dirtyRegion.height + 1; y++) {
                    for (x = -1; x < u.dirtyRegion.width + 1; x++) {
                        setMidTile(u.dirtyRegion.x + x, u.dirtyRegion.y + y);
                    }
                }
            }
            for (Entry<Integer, Byte> e : u.texture.entrySet()) {
                setTile(e.getKey() % heightMap.width, e.getKey() / heightMap.width, e.getValue());
            }
        }
    }
}
