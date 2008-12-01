package com.novusradix.JavaPop.Client;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.TextureIO;

public class HeightMap {

    private int width,  breadth;
    private FloatBuffer b;
    private static int rowstride,  tilestride,  vertexstride;
    private static final int VX = 0,  VY = 1,  VZ = 2,  NX = 3,  NY = 4,  NZ = 5,  TX = 6,  TY = 7;

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

    public HeightMap(Dimension mapSize) {
        this.breadth = mapSize.height;
        this.width = mapSize.width;

        vertexstride = 8;
        tilestride = 12 * vertexstride;
        rowstride = tilestride * (width - 1);

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
                b.put(1);
                b.put(0);
                // 2
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0.5f);
                b.put(0.5f);
                // 3
                b.put((float) x + 1);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(1);
                b.put(0);
                // 4
                b.put((float) x + 1);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(1);
                b.put(1);
                // 5
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0.5f);
                b.put(0.5f);
                // 6
                b.put((float) x + 1);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(1);
                b.put(1);
                // 7
                b.put(x);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0);
                b.put(1);
                // 8
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0.5f);
                b.put(0.5f);
                // 9
                b.put(x);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
                b.put(0);
                b.put(1);
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
                b.put(0.5f);
                b.put(0.5f);
            }
        }
        b.flip();



    }

    public int getWidth() {
        return width;
    }

    public int getBreadth() {
        return breadth;
    }

    private static int bufPos(int x, int y, int vertex, int index) {
        return y * rowstride + x * tilestride + vertex * vertexstride + index;
    }

    public int getHeight(int x, int y) {
        try {
            if (x == width - 1 && y == breadth - 1) {
                return (int) b.get(bufPos(x - 1, y-1, 4, VZ));
            }
            if (x == width - 1) {
                return (int) b.get(bufPos(x - 1, y, 3, VZ));
            }
            if (y == breadth - 1) {
                return (int) b.get(bufPos(x, y - 1, 7, VZ));
            }
            return (int) b.get(bufPos(x, y, 0, VZ));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client getHeight:" + x + ", " + y);

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

    public float getHeight(float x, float y) {
        int x1, x2, y1, y2;
        float ha, hb, hc, hd, hm;
        x1 = (int) Math.floor(x);
        x2 = (int) Math.ceil(x);
        y1 = (int) Math.floor(y);
        y2 = (int) Math.ceil(y);

        x = x - x1;
        y = y - y1;

        ha = getHeight(x1, y1);
        hb = getHeight(x1, y2);
        hc = getHeight(x2, y1);
        hd = getHeight(x2, y2);
        hm = ha;
        if (hb > ha || hc > ha || hd > ha) {
            hm = ha + 0.5f;
        }
        if (hb < ha || hc < ha || hd < ha) {
            hm = ha - 0.5f;
        }
        if (y > x) {
            if (y > 1 - x) {
                // BMD
                return hb + (hd - hb) * x + (hm - (hd + hb) / 2.0f) * (1 - y);
            } else {
                // AMB
                return ha + (hb - ha) * y + (hm - (hb + ha) / 2.0f) * x;
            }
        } else {
            if (y > 1 - x) {
                // CMD
                return hc + (hd - hc) * y + (hm - (hd + hc) / 2.0f) * (1 - x);
            } else {
                // AMC
                return ha + (hc - ha) * x + (hm - (ha + hb) / 2.0f) * y;
            }
        }
    }

    public Point2f getSlope(float x, float y) {
        int x1, x2, y1, y2;
        float a, b, c, d, m;
        x1 = (int) Math.floor(x);
        x2 = (int) Math.ceil(x);
        y1 = (int) Math.floor(y);
        y2 = (int) Math.ceil(y);

        x = x - x1;
        y = y - y1;

        a = getHeight(x1, y1);
        b = getHeight(x1, y2);
        c = getHeight(x2, y1);
        d = getHeight(x2, y2);

        if (a == b && b == c && c == d) {
            return new Point2f(0, 0);
        }
        m = a;
        if (b > a || c > a || d > a) {
            m = a + 0.5f;
        }
        if (b < a || c < a || d < a) {
            m = a - 0.5f;
        }
        if (y > x) {
            if (y > 1 - x) {
                // BMD
                return new Point2f(d - b, 2.0f * ((d + b) / 2.0f - m));
            } else {
                // AMB
                return new Point2f(2.0f * (m - (a + b) / 2.0f), b - a);
            }
        } else {
            if (y > 1 - x) {
                // CMD
                return new Point2f(2.0f * ((c + d) / 2.0f - m), d - c);
            } else {
                // AMC
                return new Point2f(c - a, 2.0f * (m - (a + c) / 2.0f));
            }
        }
    }

    private void setHeight(int x, int y, int height) {
        try {
            if (x >= 0 && y >= 0 && x < width && y < breadth) {
                if (x < width - 1 && y < width - 1) {
                    b.put(bufPos(x, y, 0, VZ), height);
                    b.put(bufPos(x, y, 10, VZ), height);
                }
                if (x >= 1 && y >= 1) {
                    b.put(bufPos(x - 1, y - 1, 4, VZ), height);
                    b.put(bufPos(x - 1, y - 1, 6, VZ), height);
                }
                if (y >= 1 && x < width - 1) {
                    b.put(bufPos(x, y - 1, 7, VZ), height);
                    b.put(bufPos(x, y - 1, 9, VZ), height);
                }
                if (x >= 1 && y < width - 1) {
                    b.put(bufPos(x - 1, y, 1, VZ), height);
                    b.put(bufPos(x - 1, y, 3, VZ), height);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.print(x + ", " + y + ": out of bounds in SetHeight()");
        }
    }

    private void setMidTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width - 1 || y >= breadth - 1) {
            return;
        }
        float m;
        m = Math.max(Math.max(getHeight(x, y), getHeight(x, y + 1)), Math.max(getHeight(x + 1, y), getHeight(x + 1, y + 1))) + Math.min(Math.min(getHeight(x, y), getHeight(x, y + 1)), Math.min(getHeight(x + 1, y), getHeight(x + 1, y + 1)));

        try {
            b.put(bufPos(x, y, 2, VZ), m * 0.5f);
            b.put(bufPos(x, y, 5, VZ), m * 0.5f);
            b.put(bufPos(x, y, 8, VZ), m * 0.5f);
            b.put(bufPos(x, y, 11, VZ), m * 0.5f);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Array out of bounds in Client setMidTile:" + x + ", " + y);
        }
        setNormals(x, y);

        if (getHeight(x, y) == 0 && getHeight(x, y + 1) == 0 && getHeight(x + 1, y) == 0 && getHeight(x + 1, y + 1) == 0) {
            setTexture(x, y, 0);
        } else {
            setTexture(x, y, 1);
        }

    }

    public void setTexture(int x, int y, int t) {
        b.put(bufPos(x, y, 0, TX), t);
        b.put(bufPos(x, y, 1, TX), t + 1);
        b.put(bufPos(x, y, 2, TX), t + 0.5f);
        b.put(bufPos(x, y, 3, TX), t + 1);
        b.put(bufPos(x, y, 4, TX), t + 1);
        b.put(bufPos(x, y, 5, TX), t + 0.5f);
        b.put(bufPos(x, y, 6, TX), t + 1);
        b.put(bufPos(x, y, 7, TX), t);
        b.put(bufPos(x, y, 8, TX), t + 0.5f);
        b.put(bufPos(x, y, 9, TX), t);
        b.put(bufPos(x, y, 10, TX), t);
        b.put(bufPos(x, y, 11, TX), t + 0.5f);

    }

    private void setNormals(int x, int y) {
        setNormals(x, y, 0, 1, 2);
        setNormals(x, y, 3, 4, 5);
        setNormals(x, y, 6, 7, 8);
        setNormals(x, y, 9, 10, 11);
    }

    private void setNormals(int x, int y, int vertA, int vertB, int vertC) {
        Vector3f va, vb, vc, vn;
        va = new Vector3f();
        vb = new Vector3f();
        vc = new Vector3f();

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

            vn = calcNormal(vc, va, vb);

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

    public boolean isFlat(int x, int y) {
        int ha = 0, hb = 0, hc = 0, hd = 0;
        if (x < 0 || y < 0 || x + 1 >= width || y + 1 >= breadth) {
            return false;
        }
        ha = getHeight(x, y);
        hb = getHeight(x, y + 1);
        hc = getHeight(x + 1, y);
        hd = getHeight(x + 1, y + 1);
        return (ha == hb && hb == hc && hc == hd);
    }

    public void display(GL gl) {

        Vector3f l = new Vector3f(new float[]{-9, -5, 10});
        l.normalize();

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, FloatBuffer.wrap(new float[]{l.x, l.y, l.z, 0.0f}));

        gl.glColor3f(1, 1, 1);

        /*gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);
        gl.glEnable(GL.GL_TEXTURE_2D);
         */
        tex.enable();
        tex.bind();
        synchronized (this) {
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, (width - 1) * (breadth - 1) * 4 * 3);
        }
        tex.disable();
    }

    private Vector3f calcNormal(final Vector3f a, final Vector3f b, final Vector3f c) {
        Vector3f ab, ac, n;
        ab = (Vector3f) b.clone();
        ab.sub(a);
        ac = (Vector3f) c.clone();
        ac.sub(a);

        n = new Vector3f();
        n.cross(ab, ac);
        n.normalize();

        return n;
    }

    public void init(final GLAutoDrawable glDrawable) {
        final GL gl = glDrawable.getGL();
        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

        b.position(VX);
        gl.glVertexPointer(3, GL.GL_FLOAT, vertexstride * 4, b);
        b.position(NX);
        gl.glNormalPointer(GL.GL_FLOAT, vertexstride * 4, b);
        b.position(TX);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, vertexstride * 4, b);

        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glScalef(32.0f / 256.0f, 32.0f / 256.0f, 1.0f);

        try {
            URL u = getClass().getResource("/com/novusradix/JavaPop/textures/tex.png");
            tex = TextureIO.newTexture(u, false, "png");
            tex.enable();
            tex.bind();
        } catch (GLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void applyUpdate(HeightMapUpdate u) {
        synchronized (this) {
            int x, y;
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
    }
}