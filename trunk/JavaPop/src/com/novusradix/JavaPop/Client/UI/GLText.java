package com.novusradix.JavaPop.Client.UI;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Float;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class GLText {

    private static final char[] alpha = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] numeric = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] symbols = new char[]{'.', ',', '!', '?'};
    private static String all;


    static {
        char[] allChars = new char[alpha.length + numeric.length + symbols.length];
        System.arraycopy(alpha, 0, allChars, 0, alpha.length);
        System.arraycopy(numeric, 0, allChars, alpha.length, numeric.length);
        System.arraycopy(symbols, 0, allChars, alpha.length + numeric.length, symbols.length);
        all = new String(allChars);
    }
    Texture font;
    private Map<KernPair, Float> kerning;

    class KernPair {

        final char a,  b;

        public KernPair(char a, char b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() == KernPair.class) {
                return ((KernPair) o).a == a && ((KernPair) o).b == b;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + this.a;
            hash = 41 * hash + this.b;
            return hash;
        }
    }

    public GLText() {
        kerning = new HashMap<KernPair, Float>();
        loadKerning();
    }

    private void loadKerning() {
        int i;
        try {
            DataInputStream is;

            try {
                is = new DataInputStream(new FileInputStream("kerning2.dat"));
            } catch (FileNotFoundException e2) {
                is = new DataInputStream(getClass().getResourceAsStream("/com/novusradix/JavaPop/kerning2.dat"));
            }
            kerning = new HashMap<KernPair, Float>();
            int n = is.readInt();
            char a, b;
            float f;

            for (i = 0; i < n; i++) {
                a = is.readChar();
                b = is.readChar();
                f = is.readFloat();
                kerning.put(new KernPair(a, b), f);
            }

        } catch (IOException ex) {
            for (int a = 0; a < 26; a++) {
                for (int b = 0; b < 26; b++) {
                    kerning.put(new KernPair(alpha[a], alpha[b]), 0.7f);
                }
            }
        }
    }

    public void saveKerning() {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream("kerning2.dat"));
            os.writeInt(kerning.size());

            for (Entry<KernPair, Float> e : kerning.entrySet()) {
                os.writeChar(e.getKey().a);
                os.writeChar(e.getKey().b);
                os.writeFloat(e.getValue());
            }
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(GLText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private float getKern(KernPair k) {
        Float f = kerning.get(k);
        if (f != null) {
            return f;
        }
        return 0.7f;
    }

    private float getKern(char a, char b) {
        KernPair k = new KernPair(a, b);
        return getKern(k);
    }

    public void increaseKern(char a, char b) {
        KernPair k = new KernPair(a, b);
        float f = getKern(k);
        kerning.put(k, f + 0.05f);
    }

    public void decreaseKern(char a, char b) {
        KernPair k = new KernPair(a, b);
        float f = getKern(k);
        kerning.put(k, f - 0.05f);
    }

    public float getWidth(String text, float size){
        float x=0;
         char lastChar = 0;
        float lastScale = 1.0f;
        boolean first = true;
        for (char c : text.toCharArray()) {
            int index = 0;
            float scale = 1.0f;
            index = all.indexOf(c);
            if (index == -1) {
                index = all.toLowerCase().indexOf(c);
                if (index != -1) {
                    scale = 0.7f;
                } else {
                    x += size * 0.6f * lastScale;
                    continue;
                }
            }
            if (!first) {
                x += (lastScale * 1.0f) * size * getKern(lastChar, c);
            }
            first = false;
            lastChar = c;
            lastScale = scale;
        }
        x += (lastScale * 1.0f) * size * getKern(lastChar, '.');
        return x;
    }
    public void drawString(GL gl, String text, float x, float y, float size) {
            
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glUseProgram(0);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(1, 1, 1, 1);

        font.bind();

        char lastChar = 0;
        float lastScale = 1.0f;
        boolean first = true;
        for (char c : text.toCharArray()) {
            int index = 0;
            float scale = 1.0f;
            index = all.indexOf(c);
            if (index == -1) {
                index = all.toLowerCase().indexOf(c);
                if (index != -1) {
                    scale = 0.7f;
                } else {
                    x += size * 0.6f * lastScale;
                    continue;
                }
            }
            if (!first) {
                x += (scale * 0.0f + lastScale * 1.0f) * size * getKern(lastChar, c);
            }
            first = false;
            drawChar(gl, index, x, y, size * scale);
            lastChar = c;
            lastScale = scale;
        }
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
            gl.glVertex2f(x, y - 0.8f * size);

            gl.glTexCoord2f(tx, ty + 1.0f / 8.0f);
            gl.glVertex2f(x, y + 0.2f * size);

            gl.glTexCoord2f(tx + 1.0f / 8.0f, ty + 1.0f / 8.0f);
            gl.glVertex2f(x + size, y + 0.2f * size);

            gl.glTexCoord2f(tx + 1.0f / 8.0f, ty);
            gl.glVertex2f(x + size, y - 0.8f * size);
        }
        gl.glEnd();
    }
}
