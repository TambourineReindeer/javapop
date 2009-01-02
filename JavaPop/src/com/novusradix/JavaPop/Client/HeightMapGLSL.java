package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.GLHelper.GLHelperException;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

import static javax.media.opengl.GL.*;

public class HeightMapGLSL implements HeightMapImpl {
    /*
     * b layout: float x,y;
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
    private static final int VX = 0,  VY = 1;
    private int width,  breadth;
    private ByteBuffer heights,  tiles;
    private int[] textures;
    private Rectangle dirtyHeights,  dirtyTiles;
    private int shader;

    public void initialise(Dimension mapSize) {
        width = mapSize.width;
        breadth = mapSize.height;
        vertexstride = 2;
        tilestride = 12 * vertexstride;
        rowstride = tilestride * (width - 1);
        textures = new int[2];
        dirtyHeights = null;
        dirtyTiles = null;

        b = BufferUtil.newFloatBuffer((width - 1) * (breadth - 1) * tilestride);

        int x, y;
        for (y = 0; y < (breadth - 1); y++) {
            for (x = 0; x < (width - 1); x++) {
                // 0
                b.put(x);
                b.put(y);
                // 1
                b.put((float) x + 1);
                b.put(y);
                // 2
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                // 3
                b.put((float) x + 1);
                b.put(y);
                // 4
                b.put((float) x + 1);
                b.put((float) y + 1);
                // 5
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                // 6
                b.put((float) x + 1);
                b.put((float) y + 1);
                // 7
                b.put(x);
                b.put((float) y + 1);
                // 8
                b.put(x + 0.5f);
                b.put(y + 0.5f);
                // 9
                b.put(x);
                b.put((float) y + 1);
                // 10
                b.put(x);
                b.put(y);
                // 11
                b.put(x + 0.5f);
                b.put(y + 0.5f);
            }
        }
        b.flip();
        heights = BufferUtil.newByteBuffer(width * breadth);
        tiles = BufferUtil.newByteBuffer(width * breadth);
    }

    public void init(GLAutoDrawable glDrawable) {
        final GL gl = glDrawable.getGL();
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

        b.position(VX);
        gl.glVertexPointer(3, GL.GL_FLOAT, vertexstride * 4, b);

        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glScalef(1.0f / 255.0f, 1.0f / 255.0f, 1.0f);

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

        GLHelper glh = new GLHelper();
        try {
            shader = glh.LoadShaderProgram(gl, "/com/novusradix/JavaPop/Client/Shaders/HeightMapVertex.shader", "/com/novusradix/JavaPop/Client/Shaders/HeightMapFragment.shader");
        } catch (IOException ex) {
            Logger.getLogger(HeightMapGLSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(HeightMapGLSL.class.getName()).log(Level.SEVERE, null, ex);
        }
        gl.glGenTextures(2, textures, 0);
        gl.glBindTexture(GL_TEXTURE_2D, textures[0]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, breadth, 0, GL_RED, GL_BYTE, heights);
        dirtyHeights = null;
        gl.glBindTexture(GL_TEXTURE_2D, textures[1]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, breadth, 0, GL_RED, GL_BYTE, tiles);
        dirtyTiles = null;
    }

    public void display(GL gl, double time) {
        int[] i = new int[1];
        gl.glGetIntegerv(GL.GL_CURRENT_PROGRAM, i, 0);
        if (i[0] != shader) {
            gl.glUseProgram(shader);
        }
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL_CLAMP);
        gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL_CLAMP);
        if (dirtyHeights != null) {
            gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, breadth, GL_RED, GL_BYTE, heights);
            dirtyHeights = null;
        }

        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);
        if (dirtyTiles != null) {
            //gl.glTexSubImage2D(GL_TEXTURE_2D, 0, dirtyTiles.x, dirtyTiles.y, dirtyTiles.width, dirtyTiles.height, GL_RED, GL_BYTE, tiles);
            gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, breadth, GL_RED, GL_BYTE, tiles);
            dirtyTiles = null;
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL_CLAMP);
        gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL_CLAMP);

        gl.glActiveTexture(GL_TEXTURE2);
        tex.enable();
        tex.bind();
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        gl.glUniform1i(gl.glGetUniformLocation(shader, "tex1"), 2);
        gl.glUniform1i(gl.glGetUniformLocation(shader, "tile"), 1);
        gl.glUniform1i(gl.glGetUniformLocation(shader, "height"), 0);

        synchronized (this) {
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, (width - 1) * (breadth - 1) * 4 * 3);
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
                        markHeightsDirty(u.dirtyRegion);
                    }
                }
            }
            for (Entry<Point, Byte> e : u.texture.entrySet()) {
                setTile(e.getKey(), e.getValue());
            }
        }
    }

    public void setTile(Point p, byte t) {
        try {
            tiles.put(p.x + width * p.y, t);
            markTilesDirty(p);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void setHeight(Point p, byte height) {
        heights.put(p.x + width * p.y, height);
    }

    public byte getHeight(Point p) {
        return heights.get(p.x + width * p.y);
    }

    private void markHeightsDirty(Rectangle r) {
        if (dirtyHeights == null) {
            dirtyHeights = r;
        } else {
            dirtyHeights = dirtyHeights.union(r);
        }
    }

    private void markTilesDirty(Point p) {
        if (dirtyTiles == null) {
            dirtyTiles = new Rectangle(p);
        }
        if (p.x < dirtyTiles.x) {
            dirtyTiles.width += dirtyTiles.x - p.x;
            dirtyTiles.x = p.x;
        }
        if (p.x > dirtyTiles.x + dirtyTiles.width) {
            dirtyTiles.width = p.x - dirtyTiles.x;
        }
        if (p.y < dirtyTiles.y) {
            dirtyTiles.height += dirtyTiles.y - p.y;
            dirtyTiles.y = p.y;
        }
        if (p.y > dirtyTiles.height) {
            dirtyTiles.height = p.y - dirtyTiles.y;
        }

    }
}
