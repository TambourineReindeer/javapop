package com.novusradix.JavaPop.Client;

import com.sun.opengl.util.texture.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL.*;

public class GLHelper {

    static private GLHelper instance;

    public static GLHelper getHelper() {
        if (instance == null) {
            instance = new GLHelper();
        }
        return instance;
    }
    private Map<URL, Texture> textures;

    public GLHelper() {
    }

    public int LoadShaderProgram(GL gl, String vertexSource, String fragmentSource) throws IOException, GLHelperException {
        int v, f;
        String[] strings;
        int[] is = new int[1];
        byte[] chars;
        int shaderprogram = gl.glCreateProgram();
        if (vertexSource != null) {
            v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
            strings = loadResource(vertexSource);
            gl.glShaderSource(v, strings.length, strings, (int[]) null, 0);
            gl.glCompileShader(v);
            gl.glGetShaderiv(v, GL_INFO_LOG_LENGTH, is, 0);
            if (is[0] > 0) {
                chars = new byte[is[0]];
                gl.glGetShaderInfoLog(v, is[0], is, 0, chars, 0);
                String s = new String(chars);
                throw new GLHelperException(0, s);
            }
            gl.glAttachShader(shaderprogram, v);
        }
        if (fragmentSource != null) {
            f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
            strings = loadResource(fragmentSource);
            gl.glShaderSource(f, strings.length, strings, (int[]) null, 0);
            gl.glCompileShader(f);
            gl.glGetShaderiv(f, GL_INFO_LOG_LENGTH, is, 0);
            if (is[0] > 0) {
                chars = new byte[is[0]];
                gl.glGetShaderInfoLog(f, is[0], is, 0, chars, 0);
                String s = new String(chars);
                throw new GLHelperException(0, s);

            }
            gl.glAttachShader(shaderprogram, f);
        }
        gl.glLinkProgram(shaderprogram);
        checkGL(gl);
        gl.glValidateProgram(shaderprogram);
        checkGL(gl);
        gl.glUseProgram(shaderprogram);
        checkGL(gl);
        return shaderprogram;
    }

    public Texture getTexture(GL gl, String textureName) throws IOException {

        URL u = getClass().getResource(textureName);
        return getTexture(gl, u);
    }

    public Texture getTexture(GL gl, URL textureURL) throws IOException {
        if (textures.containsKey(textureURL)) {
            return textures.get(textureURL);
        }

        Texture tex;

        tex = TextureIO.newTexture(textureURL, true, "png");
        tex.bind();
        tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT);
        tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT);
        textures.put(textureURL, tex);
        return tex;
    }

    public void init(GL gl) {
        textures = new HashMap<URL, Texture>();
    }

    public void checkGL(GL gl) throws GLHelperException {
        int e = gl.glGetError();
        if (e != 0) {
            throw new GLHelperException(e);
        }
    }

    public void checkGL(GL gl, String extraInfo) throws GLHelperException {
        int e = gl.glGetError();
        if (e != 0) {
            throw new GLHelperException(e, extraInfo);
        }
    }

    private String[] loadResource(String resource) throws IOException {
        BufferedReader brv;
        ArrayList<String> vsrc = new ArrayList<String>();
        String line;
        brv = new BufferedReader(new InputStreamReader(getClass().getResource(resource).openStream()));
        while ((line = brv.readLine()) != null) {
            vsrc.add(line);
        }
        String[] strings = new String[vsrc.size()];
        vsrc.toArray(strings);
        return strings;
    }

    public class GLHelperException extends Exception {

        private String message;
        public int code;

        public GLHelperException(GL gl) {
            this(gl.glGetError());
        }

        public GLHelperException(int errorcode) {
            GLU glu = new GLU();
            message = glu.gluErrorString(errorcode);
            code = errorcode;
        }

        public GLHelperException(int errorcode, String extraInfo) {
            GLU glu = new GLU();
            message = extraInfo + "/n" + glu.gluErrorString(errorcode);
            code = errorcode;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
