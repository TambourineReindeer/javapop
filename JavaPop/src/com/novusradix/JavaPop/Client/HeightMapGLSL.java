package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.GLHelper.GLHelperException;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Tile;
import com.sun.opengl.util.BufferUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.*;
import static javax.media.opengl.GL.*;

/**
 * The class responsible for drawing the height map, if GLSL is available.
 * @author gef
 */
public class HeightMapGLSL implements HeightMapImpl, GLObject {
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
    private static final int VX = 0,  VY = 1,  VZ = 2,  NX = 3,  NY = 4,  NZ = 5;
    private HeightMap heightMap;
    private boolean[] changed;
    private int displaylist;
    private static float[] tempFloats;
    private int shader;
    private int[] textures;
    private byte[] tiles;
    private boolean dirtyTiles = false;

    public void initialise(HeightMap h) {
        heightMap = h;
        vertexstride = 6;
        tilestride = 12 * vertexstride;
        rowstride = tilestride * (heightMap.width - 1);
        changed = new boolean[heightMap.breadth - 1];
        b = BufferUtil.newFloatBuffer((heightMap.width - 1) * (heightMap.breadth - 1) * tilestride);
        tiles = new byte[(heightMap.width) * (heightMap.breadth) * 3];
        tempFloats = new float[tilestride];
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

                // 1
                b.put((float) x + 1);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 2
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 3
                b.put((float) x + 1);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 4
                b.put((float) x + 1);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 5
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 6
                b.put((float) x + 1);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 7
                b.put(x);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 8
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 9
                b.put(x);
                b.put((float) y + 1);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 10
                b.put(x);
                b.put(y);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);

                // 11
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                b.put(0.0f);
                b.put(0);
                b.put(0);
                b.put(1);
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

        m = heightMap.getHeight(x + 0.5f, y + 0.5f);

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
        byte texid;
        switch (Tile.tiles[t]) {
            case SEA:
                texid = (byte) (new Random()).nextInt(3);
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
            case FUNGUS_SEED:
                texid = 6;
                break;
            case SWAMP:
            case FUNGUS_LIVE:
                texid = 7;
                break;
   case FUNGUS_DORMANT:
                     case BASALT:
                texid = 8;
                break;
            case EARTHQUAKE:
            default:
                texid = 63;
                break;
        }

        tiles[3 * (x + y * (heightMap.width))] = (byte) (texid % 8);
        tiles[3 * (x + y * (heightMap.width)) + 1] = (byte) (texid / 8);
        tiles[3 * (x + y * (heightMap.width)) + 2] = 0;
        dirtyTiles = true;
    //changed[y] = true; //no need to re generate the display list 
    }

    private void setNormals(int x, int y) {
        //read entire tile
        b.position(bufPos(x, y, 0, VX));
        b.get(tempFloats, 0, tilestride);
        setNormals(x, y, 0, 1, 2, tempFloats);
        setNormals(x, y, 3, 4, 5, tempFloats);
        setNormals(x, y, 6, 7, 8, tempFloats);
        setNormals(x, y, 9, 10, 11, tempFloats);
        b.position(bufPos(x, y, 0, VX));
        b.put(tempFloats, 0, tilestride);
    }

    private void setNormals(int x, int y, int vertA, int vertB, int vertC, float[] tileData) {
        float ax, ay, az,
                bx, by, bz, cx, cy, cz;
        float nx, ny, nz;
        ax = tileData[bufPos(0, 0, vertA, VX)];
        ay = tileData[bufPos(0, 0, vertA, VY)];
        az = tileData[bufPos(0, 0, vertA, VZ)];

        bx = tileData[bufPos(0, 0, vertB, VX)] - ax;
        by = tileData[bufPos(0, 0, vertB, VY)] - ay;
        bz = tileData[bufPos(0, 0, vertB, VZ)] - az;

        cx = tileData[bufPos(0, 0, vertC, VX)] - ax;
        cy = tileData[bufPos(0, 0, vertC, VY)] - ay;
        cz = tileData[bufPos(0, 0, vertC, VZ)] - az;

        nx = by * cz - bz * cy;
        ny = bz * cx - bx * cz;
        nz = bx * cy - by * cx;

        ax = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);

        nx /= ax;
        ny /= ax;
        nz /= ax;

        tileData[bufPos(0, 0, vertA, NX)] = nx;
        tileData[bufPos(0, 0, vertA, NY)] = ny;
        tileData[bufPos(0, 0, vertA, NZ)] = nz;

        tileData[bufPos(0, 0, vertB, NX)] = nx;
        tileData[bufPos(0, 0, vertB, NY)] = ny;
        tileData[bufPos(0, 0, vertB, NZ)] = nz;

        tileData[bufPos(0, 0, vertC, NX)] = nx;
        tileData[bufPos(0, 0, vertC, NY)] = ny;
        tileData[bufPos(0, 0, vertC, NZ)] = nz;
    }

    public void init(final GL gl) {
        try {
            //URL u = getClass().getResource();
            tex = MainCanvas.glHelper.getTexture(gl, "/com/novusradix/JavaPop/textures/tex.png");
        } catch (GLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        displaylist = gl.glGenLists(heightMap.breadth);
        for (int n = 0; n <
                heightMap.breadth - 1; n++) {
            changed[n] = true;
        }
        try {
            shader = GLHelper.glHelper.LoadShaderProgram(gl, "/com/novusradix/JavaPop/Client/Shaders/HeightMapVertex.shader", "/com/novusradix/JavaPop/Client/Shaders/HeightMapFragment.shader");
        } catch (IOException ex) {
            Logger.getLogger(HeightMapGLSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(HeightMapGLSL.class.getName()).log(Level.SEVERE, null, ex);
        }
        textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL_TEXTURE_2D, textures[0]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, heightMap.width, heightMap.breadth, 0, GL_RGB, GL_UNSIGNED_BYTE, ByteBuffer.wrap(tiles));
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    }

    public void display(GL gl, float time) {


        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT1);
        gl.glColor3f(1, 1, 1);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glEnable(GL.GL_BLEND);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glPushMatrix();

        gl.glScalef(1.0f / 255.0f, 1.0f / 255.0f, 1.0f);
        tex.enable();
        tex.bind();

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, textures[0]);
        if (dirtyTiles) {
            gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, heightMap.width, heightMap.breadth, GL_RGB, GL_UNSIGNED_BYTE, ByteBuffer.wrap(tiles));
            dirtyTiles = false;
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL_CLAMP);
        gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL_CLAMP);

        gl.glUseProgram(shader);
        gl.glUniform1i(gl.glGetUniformLocation(shader, "tex1"), 0);
        gl.glUniform1i(gl.glGetUniformLocation(shader, "tiles"), 1);
        boolean firstChange = true;
        for (int n = 0; n < heightMap.breadth - 1; n++) { //todo - if this ever gets slow, limit to visible rows only
            if (changed[n]) {
                synchronized (this) {
                    if (firstChange) {
                        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
                        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
                        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                        b.position(VX);
                        gl.glVertexPointer(3, GL.GL_FLOAT, vertexstride * 4, b);
                        b.position(NX);
                        gl.glNormalPointer(GL.GL_FLOAT, vertexstride * 4, b);

                        firstChange = false;
                    }
                    gl.glNewList(displaylist + n, GL_COMPILE_AND_EXECUTE);
                    gl.glDrawArrays(GL.GL_TRIANGLES, n * (heightMap.width - 1) * 4 * 3, (heightMap.width - 1) * 4 * 3);
                }
                gl.glEndList();
                changed[n] = false;
            } else {
                gl.glCallList(displaylist + n);
            }
        }
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glPopMatrix();
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
                heightMap.setTile(e.getKey() % heightMap.width, e.getKey() / heightMap.width, e.getValue());
            }
        }
    }
}
