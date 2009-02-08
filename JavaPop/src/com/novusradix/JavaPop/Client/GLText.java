package com.novusradix.JavaPop.Client;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;

/**
 *
 * @author gef
 */
public class GLText {

    Texture font;

    public GLText() {
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

        for (char c : text.toCharArray()) {
            drawChar(gl, c, x, y, size);
            x += size * 0.7f;
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

    private void drawChar(GL gl, char c, float x, float y, float size) {
        int index = c - 'A';
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
