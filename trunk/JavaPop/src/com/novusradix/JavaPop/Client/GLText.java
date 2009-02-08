package com.novusradix.JavaPop.Client;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class GLText {

    Texture font;
    private float[][] kerning;

    public GLText() {
        kerning = new float[26][26];
        try {
            DataInputStream is;
            try {
                is = new DataInputStream(new FileInputStream("kerning.dat"));
            } catch (FileNotFoundException e) {
                is = new DataInputStream(getClass().getResourceAsStream("/com/novusradix/JavaPop/kerning.dat"));
            }
            for (int a = 0; a < 26; a++) {
                for (int b = 0; b < 26; b++) {
                    kerning[a][b] = is.readFloat();
                }
            }
        } catch (IOException ex) {
            for (int a = 0; a < 26; a++) {
                for (int b = 0; b < 26; b++) {
                    kerning[a][b] = 0.7f;
                }
            }
        }

    }

    public void saveKerning() {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream("kerning.dat"));
            for (int a = 0; a < 26; a++) {
                for (int b = 0; b < 26; b++) {
                    os.writeFloat(kerning[a][b]);
                }
            }
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(GLText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void increaseKern(int a, int b) {
        kerning[a][b] += 0.05f;
    }

    public void decreaseKern(int a, int b) {
        kerning[a][b] -= 0.05f;
    }

    public void drawString(GL gl, String text, float x, float y, float size) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glScalef(2.0f, 2.0f, 1.0f);
        gl.glTranslatef(-0.5f, -0.5f, 0.0f);
        gl.glMatrixMode(GL.GL_TEXTURE);

        gl.glLoadIdentity();

        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glUseProgram(0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glColor4f(1, 1, 1, 1);
        gl.glUseProgram(0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        font.bind();

        int lastIndex = 0;
        float lastScale = 1.0f;
        boolean first = true;
        for (char c : text.toCharArray()) {
            int index = 0;
            float scale = 1.0f;
            if ('A' <= c && c <= 'Z') {
                index = c - 'A';
                scale = 1.0f;
            }
            if ('a' <= c && c <= 'z') {
                index = c - 'a';
                scale = 0.7f;
            }
            if (!first) {
                x += (scale + lastScale) / 2.0f * size * kerning[lastIndex][index];
            }
            first = false;
            drawChar(gl, index, x, y, size*scale);
            lastIndex = index;
            lastScale = scale;

        }

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();

    }

    public void init(GL gl) {
        try {
            font = TextureIO.newTexture(getClass().getResource("/com/novusradix/JavaPop/textures/Font.png"), false, "png");
        } catch (IOException ex) {
            Logger.getLogger(GLText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void drawChar(GL gl, int index, float x, float y, float size) {
        float tx, ty;
        tx = (index % 8) / 8.0f;
        ty = (index / 8) / 8.0f;
        gl.glBegin(GL.GL_QUADS);
        {
            gl.glTexCoord2f(tx, ty);
            gl.glVertex2f(x, y + size);

            gl.glTexCoord2f(tx, ty + 1.0f / 8.0f);
            gl.glVertex2f(x, y);

            gl.glTexCoord2f(tx + 1.0f / 8.0f, ty + 1.0f / 8.0f);
            gl.glVertex2f(x + size, y);

            gl.glTexCoord2f(tx + 1.0f / 8.0f, ty);
            gl.glVertex2f(x + size, y + size);
        }
        gl.glEnd();
    }
}
