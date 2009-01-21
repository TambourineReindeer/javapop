package com.novusradix.JavaPop.Client;

import com.sun.opengl.util.texture.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL.*;

public class GLHelper {

    final static public GLHelper glHelper = new GLHelper();
    private Map<URL, Texture> textures;

    private GLHelper() {
    }

    public String CompileShader(GL gl, int shader, String[] source) {
        int[] is = new int[1];
        byte[] chars;
        gl.glShaderSource(shader, source.length, source, (int[]) null, 0);
        gl.glCompileShader(shader);
        gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, is, 0);
        if (is[0] > 0) {
            chars = new byte[is[0]];
            gl.glGetShaderInfoLog(shader, is[0], is, 0, chars, 0);
            return new String(chars);
        }
        return "OK\n";
    }

    public int LoadShaderProgram(GL gl, String vertexSource, String fragmentSource) throws IOException, GLHelperException {
        StringBuffer sb = new StringBuffer();
        int v, f;
        String[] strings;
        int[] is = new int[1];
        byte[] chars;
        int shaderprogram = gl.glCreateProgram();
        if (vertexSource != null) {
            v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
            strings = loadResource(vertexSource);
            sb.append("Vertex shader compile:");
            sb.append(CompileShader(gl, v, strings));
            gl.glAttachShader(shaderprogram, v);
        }
        if (fragmentSource != null) {
            f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
            strings = loadResource(fragmentSource);
            sb.append("Fragment shader compile:");
            sb.append(CompileShader(gl, f, strings));
            gl.glAttachShader(shaderprogram, f);
        }
        gl.glLinkProgram(shaderprogram);
        gl.glGetProgramiv(shaderprogram, GL_INFO_LOG_LENGTH, is, 0);
        if (is[0] > 0) {
            chars = new byte[is[0]];
            gl.glGetProgramInfoLog(shaderprogram, is[0], is, 0, chars, 0);
            sb.append("Link:");
            sb.append(new String(chars));
        }
        gl.glValidateProgram(shaderprogram);
        gl.glGetProgramiv(shaderprogram, GL_INFO_LOG_LENGTH, is, 0);
        if (is[0] > 0) {
            chars = new byte[is[0]];
            gl.glGetProgramInfoLog(shaderprogram, is[0], is, 0, chars, 0);
            sb.append("Validate:");
            sb.append(new String(chars));
        }
        gl.glGetProgramiv(shaderprogram, GL_VALIDATE_STATUS, is, 0);
        if (is[0] == GL_FALSE || sb.toString().contains("ERROR")) {
            throw new GLHelperException(0, sb.toString());
        }
        checkGL(gl);
       
        return shaderprogram;
    }

    public Texture getTexture(GL gl, String textureName) throws IOException {

        URL u = getClass().getResource(textureName);
        return getTexture(gl, u);
    }

    public Texture getTexture(GL gl, URL textureURL) throws IOException {
        Texture tex = null;
        try {
            checkGL(gl);
            if (textures.containsKey(textureURL)) {
                return textures.get(textureURL);
            }
            tex = TextureIO.newTexture(textureURL, true, "png");
            tex.bind();
            tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT);
            tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT);
            textures.put(textureURL, tex);

            checkGL(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(GLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            message = extraInfo + "\n" + glu.gluErrorString(errorcode);
            code = errorcode;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
