package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Tile;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.Map.Entry;
import java.util.Random;
import javax.media.opengl.*;
import static java.lang.Math.*;
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
    private int width,  breadth;
    private boolean[] changed;
    private int displaylist;

    public void initialise(Dimension mapSize) {
        width = mapSize.width;
        breadth = mapSize.height;
        vertexstride = 8;
        tilestride = 12 * vertexstride;
        rowstride = tilestride * (width - 1);
        changed = new boolean[breadth - 1];
        b = BufferUtil.newFloatBuffer((width - 1) * (breadth - 1) * tilestride);

        int x, y;
        for (y = 0; y < (breadth - 1); y++) {
            for (x = 0; x < (width - 1); x++) {

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

    private int bufPos(Point p, int vertex, int index) {
        return p.y * rowstride + p.x * tilestride + vertex * vertexstride + index;
    }

    public byte getHeight(Point p) {
        int x, y;
        x = p.x;
        y = p.y;
        if (x == width - 1 && y == breadth - 1) {
            return (byte) b.get(bufPos(x - 1, y - 1, 4, VZ));
        }
        if (x == width - 1) {
            return (byte) b.get(bufPos(x - 1, y, 3, VZ));
        }
        if (y == breadth - 1) {
            return (byte) b.get(bufPos(x, y - 1, 7, VZ));
        }
        if (inBounds(x, y)) {
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

    public void setHeight(Point p, byte height) {
        try {
            if (inBounds(p)) {
                if (p.x < width - 1 && p.y < width - 1) {
                    b.put(bufPos(p.x, p.y, 0, VZ), height);
                    b.put(bufPos(p.x, p.y, 10, VZ), height);
                }
                if (p.x >= 1 && p.y >= 1) {
                    b.put(bufPos(p.x - 1, p.y - 1, 4, VZ), height);
                    b.put(bufPos(p.x - 1, p.y - 1, 6, VZ), height);
                }
                if (p.y >= 1 && p.x < width - 1) {
                    b.put(bufPos(p.x, p.y - 1, 7, VZ), height);
                    b.put(bufPos(p.x, p.y - 1, 9, VZ), height);
                }
                if (p.x >= 1 && p.y < width - 1) {
                    b.put(bufPos(p.x - 1, p.y, 1, VZ), height);
                    b.put(bufPos(p.x - 1, p.y, 3, VZ), height);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.print(p.x + ", " + p.y + ": out of bounds in SetHeight()");
        }
    }

    protected void setMidTile(Point p) {

        if (p.x < 0 || p.y < 0 || p.x >= width - 1 || p.y >= breadth - 1) {
            return;
        }
        float m;
        Point pb, pc, pd;
        pb = new Point(p.x, p.y + 1);
        pc = new Point(p.x + 1, p.y);
        pd = new Point(p.x + 1, p.y + 1);
        m = max(max(getHeight(p), getHeight(pb)), max(getHeight(pc), getHeight(pd))) + min(min(getHeight(p), getHeight(pb)), min(getHeight(pc), getHeight(pd)));

        try {
            b.put(bufPos(p, 2, VZ), m * 0.5f);
            b.put(bufPos(p, 5, VZ), m * 0.5f);
            b.put(bufPos(p, 8, VZ), m * 0.5f);
            b.put(bufPos(p, 11, VZ), m * 0.5f);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client setMidTile:" + p.x + ", " + p.y);
        }
        setNormals(p);
        changed[p.y] = true;
    }

    public void setTile(Point p, byte t) {
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
        int x,y;
        x=texid % 8;
        y=texid/8;
        left = x * 32.0f;
        right = left + 31.0f;
        xmid = (left + right) / 2.0f;
top = y*32.0f;
bottom = top+31.0f;
ymid = (top+bottom)/2.0f;
        
        b.put(bufPos(p, 0, TX), left);
        b.put(bufPos(p, 1, TX), right);
        b.put(bufPos(p, 2, TX), xmid);
        b.put(bufPos(p, 3, TX), right);
        b.put(bufPos(p, 4, TX), right);
        b.put(bufPos(p, 5, TX), xmid);
        b.put(bufPos(p, 6, TX), right);
        b.put(bufPos(p, 7, TX), left);
        b.put(bufPos(p, 8, TX), xmid);
        b.put(bufPos(p, 9, TX), left);
        b.put(bufPos(p, 10, TX), left);
        b.put(bufPos(p, 11, TX), xmid);
        
        b.put(bufPos(p, 0, TY), bottom);
        b.put(bufPos(p, 1, TY), bottom);
        b.put(bufPos(p, 2, TY), ymid);
        b.put(bufPos(p, 3, TY), bottom);
        b.put(bufPos(p, 4, TY), top);
        b.put(bufPos(p, 5, TY), ymid);
        b.put(bufPos(p, 6, TY), top);
        b.put(bufPos(p, 7, TY), top);
        b.put(bufPos(p, 8, TY), ymid);
        b.put(bufPos(p, 9, TY), top);
        b.put(bufPos(p, 10, TY), bottom);
        b.put(bufPos(p, 11, TY), ymid);
     
        changed[p.y] = true;
    }

    private void setNormals(Point p) {
        setNormals(p, 0, 1, 2);
        setNormals(p, 3, 4, 5);
        setNormals(p, 6, 7, 8);
        setNormals(p, 9, 10, 11);
    }

    private void setNormals(Point p, int vertA, int vertB, int vertC) {
        //TODO: profiled:candidate for optimisation
        Vector3 va,vb ,vc ,vn ;
        va = new Vector3();
        vb = new Vector3();
        vc = new Vector3();

        try {
            va.x = b.get(bufPos(p, vertA, VX));
            va.y = b.get(bufPos(p, vertA, VY));
            va.z = b.get(bufPos(p, vertA, VZ));

            vb.x = b.get(bufPos(p, vertB, VX));
            vb.y = b.get(bufPos(p, vertB, VY));
            vb.z = b.get(bufPos(p, vertB, VZ));

            vc.x = b.get(bufPos(p, vertC, VX));
            vc.y = b.get(bufPos(p, vertC, VY));
            vc.z = b.get(bufPos(p, vertC, VZ));

            vn = Helpers.calcNormal(vc, va, vb);

            b.put(bufPos(p, vertA, NX), vn.x);
            b.put(bufPos(p, vertA, NY), vn.y);
            b.put(bufPos(p, vertA, NZ), vn.z);

            b.put(bufPos(p, vertB, NX), vn.x);
            b.put(bufPos(p, vertB, NY), vn.y);
            b.put(bufPos(p, vertB, NZ), vn.z);

            b.put(bufPos(p, vertC, NX), vn.x);
            b.put(bufPos(p, vertC, NY), vn.y);
            b.put(bufPos(p, vertC, NZ), vn.z);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client SetNormal:" + p.x + ", " + p.y);
        }
    }

    public boolean inBounds(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < breadth);
    }

    public boolean inBounds(Point p) {
        return (p.x >= 0 && p.y >= 0 && p.x < width && p.y < breadth);
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
        displaylist = gl.glGenLists(breadth);
        for (int n = 0; n < breadth - 1; n++) {
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
        for (int n = 0; n < breadth - 1; n++) { //todo - if this ever gets slow, limit to visible rows only
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
                    gl.glDrawArrays(GL.GL_TRIANGLES, n * (width - 1) * 4 * 3, (width - 1) * 4 * 3);
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
        synchronized (this) {
            if (!u.dirtyRegion.isEmpty()) {
                int x,  y;

                for (y = 0; y < u.dirtyRegion.height; y++) {
                    for (x = 0; x < u.dirtyRegion.width; x++) {
                        setHeight(new Point(u.dirtyRegion.x + x, u.dirtyRegion.y + y), u.heightData[x + y * u.dirtyRegion.width]);
                    }
                }
                for (y = -1; y < u.dirtyRegion.height + 1; y++) {
                    for (x = -1; x < u.dirtyRegion.width + 1; x++) {
                        setMidTile(new Point(u.dirtyRegion.x + x, u.dirtyRegion.y + y));
                    }
                }
            }
            for (Entry<Point, Byte> e : u.texture.entrySet()) {
                setTile(e.getKey(), e.getValue());
            }
        }
    }
}
